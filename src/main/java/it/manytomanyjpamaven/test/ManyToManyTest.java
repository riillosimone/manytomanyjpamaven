package it.manytomanyjpamaven.test;

import java.time.LocalDate;
import java.util.List;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
//			initRuoli(ruoloServiceInstance);
//
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testInserisciNuovoUtente(utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testModificaStatoUtente(utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
//
//			testRimuoviRuoloDaUtente(ruoloServiceInstance, utenteServiceInstance);
//			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

//			testRimuoviRuolo(ruoloServiceInstance);
//			System.out.println("Gli elementi della tabella RUOLO sono: "+ruoloServiceInstance.listAll().size());

//			testListaUtentiCreatiNelMeseENellAnno(utenteServiceInstance);
//			System.out.println("Gli elementi della tabella UTENTE sono: "+utenteServiceInstance.listAll().size());
			
//			testContaUtentiAdmin(utenteServiceInstance);
//			System.out.println("Gli elementi della tabella UTENTE sono: "+utenteServiceInstance.listAll().size());
			
			testListaDistintaDescrizioniRuoloConUtentiAssociati(ruoloServiceInstance);
			System.out.println("Gli elementi della tabella RUOLO sono: "+ruoloServiceInstance.listAll().size());
			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", LocalDate.now());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", LocalDate.now());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", LocalDate.now());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testRimuoviRuoloDaUtente(RuoloService ruoloServiceInstance, UtenteService utenteServiceInstance)
			throws Exception {
		System.out.println(".......testRimuoviRuoloDaUtente inizio.............");

		// carico un ruolo e lo associo ad un nuovo utente
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN");
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("aldo.manuzzi", "pwd@2", "aldo", "manuzzi", LocalDate.now());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente non inserito ");
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);

		// ora ricarico il record e provo a disassociare il ruolo
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		boolean confermoRuoloPresente = false;
		for (Ruolo ruoloItem : utenteReloaded.getRuoli()) {
			if (ruoloItem.getCodice().equals(ruoloEsistenteSuDb.getCodice())) {
				confermoRuoloPresente = true;
				break;
			}
		}

		if (!confermoRuoloPresente)
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: utente e ruolo non associati ");

		// ora provo la rimozione vera e propria ma poi forzo il caricamento per fare un
		// confronto 'pulito'
		utenteServiceInstance.rimuoviRuoloDaUtente(utenteReloaded.getId(), ruoloEsistenteSuDb.getId());
		utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (!utenteReloaded.getRuoli().isEmpty())
			throw new RuntimeException("testRimuoviRuoloDaUtente fallito: ruolo ancora associato ");

		System.out.println(".......testRimuoviRuoloDaUtente fine: PASSED.............");
	}

	private static void testRimuoviRuolo(RuoloService ruoloService) throws Exception {
		System.out.println(".......testRimuoviRuolo inizio.............");
		List<Ruolo> listaRuoli = ruoloService.listAll();
		if (listaRuoli.size() < 1) {
			throw new RuntimeException("testRimuoviRuolo FAILED: non ci sono ruoli presenti in lista");
		}
		Long idRuoloDaRimuovere = listaRuoli.get(0).getId();
		ruoloService.rimuovi(idRuoloDaRimuovere);
		System.out.println("Fine test");
	}

	private static void testListaUtentiCreatiNelMeseENellAnno(UtenteService utenteService) throws Exception {
		System.out.println(".......testListaUtentiCreatiNelMeseENellAnno inizio.............");
		List<Utente> listaUtenti = utenteService.listAll();
		if (listaUtenti.size() < 1) {
			throw new RuntimeException(
					"testListaUtentiCreatiNelMeseENellAnno FAILED: non ci sono ruoli presenti in lista");
		}
		int meseDaCercare = 2;
		int annoDaCercare = 2023;
		List<Utente> listaUtentiCreatiNelMeseENellAnno = utenteService.listaUtentiCreatiNelMeseENellAnno(meseDaCercare,
				annoDaCercare);
		if (listaUtentiCreatiNelMeseENellAnno.size() < 1) {
			throw new RuntimeException("Attenzione: non sono presenti voci per questo anno e questo mese.");
		}
		System.out.println(listaUtentiCreatiNelMeseENellAnno);
		System.out.println(".......testListaUtentiCreatiNelMeseENellAnno fine: PASSED.............");
	}
	
	private static void testContaUtentiAdmin(UtenteService utenteService) throws Exception {
		System.out.println(".......testContaUtentiAdmin inizio.............");
		List<Utente> listaUtenti = utenteService.listAll();
		if (listaUtenti.size() < 1) {
			throw new RuntimeException(
					"testListaUtentiCreatiNelMeseENellAnno FAILED: non ci sono ruoli presenti in lista");
		}
		Long contatoreAdmin = utenteService.contaQuantiUtentiConRuoloAdmin();
		if (contatoreAdmin == 0) {
			throw new RuntimeException("Attenzione: non sono presenti Admin.");
		}
		System.out.println("Il contatore segna: "+contatoreAdmin);
		System.out.println(".......testContaUtentiAdmin fine: PASSED.............");
	}
	
	private static void testListaDistintaDescrizioniRuoloConUtentiAssociati(RuoloService ruoloService) throws Exception {
		System.out.println(".......testRimuoviRuolo inizio.............");
		List<Ruolo> listaRuoli = ruoloService.listAll();
		if (listaRuoli.size() < 1) {
			throw new RuntimeException("testRimuoviRuolo FAILED: non ci sono ruoli presenti in lista");
		}
		List<String> listaDescrizioni = ruoloService.listaDistintaDescrizioneRuoliUtentiAssociati();
		System.out.println(listaDescrizioni);
		System.out.println("Fine test");
	}
	
}
