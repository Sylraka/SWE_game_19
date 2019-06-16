package application.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import application.logic.LogicFactory;
import application.logic.port.MVCPort;
import application.makemove.MakeMoveFactory;
import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;
import application.makemove.port.MakeMoveManagement;
import application.statemachine.port.Observer;
import application.statemachine.port.State;

public class ConsoleIOHandler implements Observer {

	private MakeMoveManagement model;
	private State currentState;

	public ConsoleIOHandler() {
		currentState = State.S.InitialState;
		this.model = MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(null);
		MVCPort mvcPort = LogicFactory.FACTORY.MVCPort();
		mvcPort.subject().attach(this);
		initComponents();
	}

	private void initComponents() {
		System.out.println("Viel Spaß bei unserem Spiel! :)");
		System.out.println("Anzahl der Spieler: " + model.getSpielerliste().size() + "\n");

		getEingabe();
	}

	private void getEingabe() {
		String str = new String();
		BufferedReader in;

		waehleVarianteDesSpieles();

		while (true) {
			printEingabemoeglichkeiten();
			try {
				in = new BufferedReader(new InputStreamReader(System.in));
				str = in.readLine();
			} catch (IOException e) {
				System.out.println("EXCEPTION");
			}

			if (str.equals("w") && currentState == State.S.WurfState) {
				this.model.wuerfeln();
				continue;
			}

			if (str.matches(".*\\d+.*") && currentState == State.S.WurfState) {
				int augenzahl = Integer.parseInt(str);
				if (augenzahl > 6 || augenzahl < 1) {
					System.err.println(
							"Fehlerhafte Zahl bitte versuchen Sie es erneut.\n(Geben Sie eine Zahl von 1 bis 6 ein)");
				} else {
					this.model.zahlWuerfeln(augenzahl);
				}
				continue;
			}

			if (str.equals("s") && currentState == State.S.InitialState) {
				this.model.neueRundeStarten();
				continue;
			}

			if (str.equals("f") && currentState == State.S.FrageState) {
				System.out.println("Die Frage wurde falsch beantwortet, die Figur wird auf ihr Heimatsfeld gesetzt.");
				this.model.frageBeantworten(false);
				continue;
			}

			if (str.equals("r") && currentState == State.S.FrageState) {
				System.out.println("Die Frage wurde richtig beantwortet, die Figur wird auf ihr Startfeld gesetzt.");
				this.model.frageBeantworten(true);
				continue;
			}

			if (str.matches(".*\\d+.*") && currentState == State.S.WahlState) {
				int figurnummer = Integer.parseInt(str);

				if (figurnummer > 0 && figurnummer < 4 && isBewegungErlaubt(figurnummer)) {
					this.model.bewegeFigur(figurnummer);
				} else {
					System.err.println("Fehlerhafte Zahl! Bitte nochmal eine Figurnummer eingeben!\n");
				}
				continue;
			}

			// TODO: Im finalen Produkt sollte das nur im Zustand EndState funktionieren
			// if (str.matches("e") && currentState == State.S.EndState) {
			if (str.matches("e")) {
				System.out.println("Das Spiel wurde beendet.\nEmpfehlt und weiter :) Für Kritik einfach jetzt sprechen.");
				this.model.spielBeenden();
				System.out.println("Test");
			}
		}
	}

	private boolean isBewegungErlaubt(int figurnummer) {
		// Prüfe ob ausgewählte Figur in den erlaubten Schritten ist
		for (Map.Entry<Figur, Integer> schritt : model.getMoeglicheSchritte().entrySet()) {
			if (schritt.getKey().getFigurNummer() + 1 == figurnummer) {
				return true;
			}
		}
		return false;
	}

	private void printEingabemoeglichkeiten() {
		if (this.model.getUebrigeAnzahlVersuche() == 3) {
			// System.out.println();
			System.out.println("================================================");
			System.out.println();
		}
		System.out.println(this.model.getAktuellerSpieler().getSpielerName() + " ist am Zug\n");
		// printFigurenPositionen();

		if (this.model.getAugenzahl() > 0) {
			System.out.println("Gewürfelte Zahl: " + this.model.getAugenzahl() + "\n");
		}

		if (currentState == State.S.InitialState) {
			System.out.println("Zum Starten drücken Sie 's'");
		}

		if (currentState == State.S.WurfState) {
			printFigurenPositionen();
			System.out.println("Verbleibende Versuche: " + (this.model.getUebrigeAnzahlVersuche()));
			System.out.println("Zum Würfeln drücken Sie 'w'");
		}

		if (currentState == State.S.FrageState) {
			System.out.println(
					"Es wird eine Frage gestellt, soll diese richtig ('r') oder falsch ('f') beantwortet werden?");
		}

		if (currentState == State.S.WahlState) {
			printMoeglicheBewegungen();
			System.out.format("\nGeben Sie die Nummer der zu bewegenden Figur ein.\n");
		}

		if (currentState == State.S.EndGameState) {
			System.out.println("Winner: " + this.model.getGewinner().getSpielerName());
			System.out.println("Drücke e zum Beenden des Spieles");
		}
	}

	private void waehleVarianteDesSpieles() {
		String str = new String();
		BufferedReader in;

		System.out.println("Soll einfache 'j' Variante gespielt werden? Wenn nicht drücken Sie 'n'.");

		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			str = in.readLine();
		} catch (IOException e) {
			System.out.println("EXCEPTION");
		}

		if (str.equals("j")) {
			System.out.println("Die einfache Variante wurde ausgewählt.");
			this.model.setEinfacheVariante(true);
		}

		if (str.equals("n")) {
			System.out.println("Die schwierigere Variante wurde ausgewählt.");
			this.model.setEinfacheVariante(false);
		}
	}

	public void update(State newState) {
		if (newState == null)
			return;

		currentState = newState;
	}

	private void printMoeglicheBewegungen() {
		Spieler spieler = this.model.getAktuellerSpieler();
		System.out.println("Mögliche Bewegungen:");
		for (Figur figur : spieler.getFiguren()) {
			if (this.model.getMoeglicheSchritte().get(figur) != null) {
				System.out.println("\t\t Figur " + (figur.getFigurNummer() + 1)
						+ ((figur.getPosition() == -1) ? " vom Heimatsfeld" : (" von Feld " + figur.getPosition()))  +" auf Feld "
						+ this.model.getMoeglicheSchritte().get(figur));
			}
		}
	}

	private void printFigurenPositionen() {
		for (Spieler spieler : this.model.getSpielerliste()) {
			String figurenImSpielAusgabe = "";
			int figurenCounter = 0;
			for (Figur figur : spieler.getFiguren()) {

				if (figur.getPosition() != -1) {
					figurenImSpielAusgabe += "\t Figur " + (figur.getFigurNummer() + 1) + " Feld " + figur.getPosition()
							+ "\n";
					figurenCounter++;
				}
			}
			System.out.println(spieler.getSpielerName() + ": \t" + figurenCounter + " von 3 Figuren im Spiel");
			System.out.println(figurenImSpielAusgabe);
		}
	}
}
