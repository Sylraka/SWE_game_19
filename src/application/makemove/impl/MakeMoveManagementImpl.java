package application.makemove.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;
import application.makemove.port.MakeMoveManagement;
import application.statemachine.port.State;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;

public class MakeMoveManagementImpl implements MakeMoveManagement {
	private static final int MAX_ANZAHL_VERSUCHE = 3;
	private static final int SPIELFELDGROESSE = 48;
	private final List<Spieler> spielerliste;
	private StateMachine stateMachine;

	private int aktuelleRunde;
	private int anzahlWuerfe;
	private int augenzahl;
	private Spieler aktuellerSpieler;
	private Spieler gewinner;
	private int anzahlFigurenAufHeimatsfeld = 0;
	private boolean einfacheVariante = false;

	// für die Figur, wohin sie sich bewegen kann in diesem Zug, int = Endpunkt
	private Map<Figur, Integer> moeglicheSchritte = new HashMap<Figur, Integer>();

	public MakeMoveManagementImpl(StateMachinePort stPort, List<Spieler> spielerliste) {
		this.stateMachine = stPort.stateMachine();

		this.spielerliste = spielerliste;
		this.aktuellerSpieler = spielerliste.get(0);
		this.aktuelleRunde = 0;
		this.anzahlWuerfe = 0;
	}

	@Override
	public void neueRundeStarten() {
		this.stateMachine.setState(State.S.WurfState);
	}

	@Override
	public void wuerfeln() {
		anzahlWuerfe++;
		setCheatWuerfel(6);
		// augenzahl = getRandomAugenzahl();
		berechneMoeglicheSchritte();
	}

	/**
	 * hat der Spieler eine Figur ausgewählt, die er bewegen möchte, wird diese
	 * methode aufgerufen um die figur auf diese position zu setzten. ist das feld
	 * leer, wird er draufgesetzt ist das feld besetzt, wird eine frage gestellt
	 * &spieler wird drauf gesetzt -> richtige antwort: gegner auf startfeld ->
	 * falsche antwort: gegner auf heimatfeld
	 */
	@Override
	public void bewegeFigur(int figurNummer) {
		Figur figur = aktuellerSpieler.getFiguren()[figurNummer - 1];

		int aktuellesZiel = moeglicheSchritte.get(figur);
		Spieler spielerAufFeld = getSpielerAufFeld(aktuellesZiel);
		figur.setPosition(aktuellesZiel);

		if (spielerAufFeld == null) {
			this.stateMachine.setState(State.S.InitialState);

		} else {// auf dem feld sitzt eine andere figur

			// wenn ein fight auftritt
			// TODO das in die GUI verlagern
			// TODO eine Wahl, ob die Frage richtig oder falsch beantwortet wurde
			// implementieren
			System.out.println("Hier wird eine Frage an Spieler " + spielerAufFeld.getSpielerName()
					+ " gestellt, er hat die Frage falsch beantwortet");
			// falsche antwort: defender wird aufs heimatfeld gesetzt
			setFigurAufHeimatfeld(spielerAufFeld, aktuellesZiel);

			this.moeglicheSchritte.clear();
			this.stateMachine.setState(State.S.InitialState);
		}
		resetVariablenFuerNaechsteRunde();
	}

	@Override
	public void spielBeenden() {
		System.out.println("Das ist das Ende des Spiels, ich hoffe es hat euch spaß gemacht!");
		System.exit(0);
	}

	// TODO löschen
	@Override
	public Spieler getGewinner() {
		return this.gewinner;
	}

	@Override
	public int getAktuelleRunde() {
		return this.aktuelleRunde;
	}

	@Override
	public Spieler getAktuellerSpieler() {
		return this.aktuellerSpieler;
	}

	@Override
	public int getAugenzahl() {
		return augenzahl;
	}

	@Override
	public List<Spieler> getSpielerliste() {
		return this.spielerliste;
	}

	@Override
	public int getUebrigeAnzahlVersuche() {
		return MAX_ANZAHL_VERSUCHE - this.anzahlWuerfe;
	}

	@Override
	public Map<Figur, Integer> getMoeglicheSchritte() {
		return this.moeglicheSchritte;
	}

	@Override
	public boolean istEinfacheVariante() {
		return einfacheVariante;
	}

	private int getRandomAugenzahl() {
		return new Random().nextInt(6) + 1;
	}

	// TODO: delete, for debugging
	public void setCheatWuerfel(int cheatAugenzahl) {
		augenzahl = cheatAugenzahl;
	}

	private void resetAnzahlWuerfe() {
		anzahlWuerfe = 0;
	}

	private void resetWuerfel() {
		augenzahl = 0;
	}

	private void setNaechsteRunde() {
		aktuelleRunde++;
	}

	private boolean istMinEineFigurAufSpielfeld() {
		for (Figur f : aktuellerSpieler.getFiguren()) {
			if (!f.isHeimatsfeld())
				return true;
		}
		return false;
	}

	private void resetVariablenFuerNaechsteRunde() {
		moeglicheSchritte.clear();

		setNaechsteRunde();
		setNaechsterSpieler();

		resetAnzahlWuerfe();
		// TODO wollen wir das behalten?
		resetWuerfel();
	}

	private void setNaechsterSpieler() {
		if (aktuellerSpieler == null) {
			aktuellerSpieler = spielerliste.get(0);
		} else {
			int nextId = (spielerliste.indexOf(aktuellerSpieler) + 1) % spielerliste.size();
			aktuellerSpieler = spielerliste.get(nextId);
		}
	}

	private void berechneMoeglicheSchritte() {
		// private Map<Figur, Integer> moeglicheSchritte;
		berechneAnzahlFigurenAufHeimatsfeld();
		if (augenzahl == 6 && anzahlFigurenAufHeimatsfeld > 0) {
			// dann setze die figur aufs startfeld

			for (Figur figur : aktuellerSpieler.getFiguren()) {
				// wähle figur aus, die vom heimatfeld aufs startfeld soll
				if (figur.isHeimatsfeld()) {
					moeglicheSchritte.put(figur, aktuellerSpieler.getStartFeld());
				}
			}

			this.stateMachine.setState(State.S.WahlState);
		} else { // augenzahl 1-5 oder alle figuren im spiel (keine auf heimatsfeld)
			if (istEineFigurImSpiel()) {
				// TODO berechneMoeglicheSpielzüge(figurAktuellerSPieler)
				for (Figur figur : aktuellerSpieler.getFiguren()) {
					if (!figur.isHeimatsfeld()) {
						moeglicheSchritte.put(figur, (figur.getPosition() + augenzahl) % 48);
					}
				}
				if (!einfacheVariante) {
					entferneUnmoeglicheSchritte();
				}
				this.stateMachine.setState(State.S.WahlState);
			} else {
				if (anzahlWuerfe == 3) {
					resetVariablenFuerNaechsteRunde();
					this.stateMachine.setState(State.S.InitialState);
				} else {
					this.stateMachine.setState(State.S.WurfState);
				}
			}

		}

	}

	private void entferneUnmoeglicheSchritte() {
		for (Map.Entry<Figur, Integer> moegSchritte : moeglicheSchritte.entrySet()) {
			int zielFeld = moegSchritte.getValue();
			for (Figur figur : aktuellerSpieler.getFiguren()) {
				if (figur.getPosition() == zielFeld) {
					moeglicheSchritte.remove(figur);
				}
			}
		}
	}

	private boolean istEineFigurImSpiel() {
		return anzahlFigurenAufHeimatsfeld < 3;
	}

	private void berechneAnzahlFigurenAufHeimatsfeld() {
		anzahlFigurenAufHeimatsfeld = 0;
		for (Figur figur : aktuellerSpieler.getFiguren()) {
			if (figur.isHeimatsfeld()) {
				anzahlFigurenAufHeimatsfeld++;
			}
		}
	}

	private Spieler getSpielerAufFeld(int position) {
		for (Spieler spieler : spielerliste) {
			for (Figur figur : spieler.getFiguren()) {
				if (figur.getPosition() == position) {
					return spieler;
				}
			}
		}
		return null;
	}

	private void setFigurAufHeimatfeld(Spieler spieler, int position) {
		for (Figur figur : spieler.getFiguren()) {
			if (figur.getPosition() == position) {
				figur.setPosition(-1);
			}
		}
	}

	private void setFigurAufStartfeld(Spieler spieler, int position) {
		for (Figur figur : spieler.getFiguren()) {
			if (figur.getPosition() == position) {
				figur.setPosition(spieler.getStartFeld());
			}
		}
	}

	private Spieler getSpielerNummer(int SpielerNummer) {
		for (Spieler spieler : spielerliste) {
			if (spieler.getSpielerNummer() == SpielerNummer)
				return spieler;
		}
		return null;
	}

	// if (hasMoves())
	// this.stateMachine.setState(State.S.WahlState);
	// // TODO Überprüfen, ob es richtig ist
	// else if (anzahlWuerfe == 3) {
	// this.stateMachine.setState(State.S.InitialState);
	// resetVariablenFuerNaechsteRunde();
	// } else {
	// this.stateMachine.setState(State.S.WurfState);
	// }
	// }
	//
	// // TODO: delete, for debugging
	// public Figur getFigureByField(int pos) {
	// return this.figureByField.get(pos);
	// }

}
