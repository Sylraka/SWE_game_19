package application.gui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        System.out.println("Started observer console");
        currentState = State.S.InitialState;
        this.model = MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(null);
        MVCPort mvcPort = LogicFactory.FACTORY.MVCPort();
        mvcPort.subject().attach(this);
        initComponents();
    }

    //TODO einfache Variante auswählen -> ein button hinzufügen
    private void initComponents() {
        getInput();
    }

    private void getInput() {
        String str = new String();
        BufferedReader in;
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

/*			if (str.matches(".*\\d+.*") && currentState == State.S.WurfState) {
				this.model.throwCheatDice(Integer.parseInt(str));
				continue;
			}*/
/*
            if (str.equals("y") && currentState == State.S.InitialState) {
            //TODO noch hinzufügen
                this.model.neueRundeStarten();
                continue;
            }*/

            if (str.matches(".*\\d+.*") && currentState == State.S.WahlState) {
                this.model.bewegeFigur(Integer.parseInt(str));
                continue;
            }

            if (str.matches("e") && currentState == State.S.EndGameState) {
                this.model.spielBeenden();
            }
        }
    }

    private void printEingabemoeglichkeiten() {
        System.out.println(this.model.getAktuellerSpieler().getSpielerName() + " ist am Zug\n");
        printFieldStatus();

        if (currentState == State.S.WurfState) {
            System.out.println("Verbleibende Versuche: " + (this.model.getUebrigeAnzahlVersuche()));
            System.out.println("Zum Würfeln drücken Sie 'w'");
        }

        if (currentState == State.S.WahlState) {

            printMoeglicheBewegungen();
            System.out.format("Figur zum Bewegen auswählen\n" +
                    "Geben Sie die Figurnummer ein.");
        }

		if (currentState == State.S.EndGameState) {
			System.out.println("Winner: "+this.model.getGewinner().getSpielerName());
			System.out.println("Drücke e zum Beenden des Speieles");
		}
    }

    public void update(State newState) {
        if (newState == null)
            return;

        if (currentState == State.S.WurfState) {
            System.out.println("Gewürfelte Zahl: " + this.model.getAugenzahl());
        }

        currentState = newState;
    }

    private void printMoeglicheBewegungen() {
        Spieler spieler = this.model.getAktuellerSpieler();
        System.out.println("Mögliche Bewegungen:");
        for (Figur figur : spieler.getFiguren()) {
            if (figur.getPosition() != -1) {
                System.out.println("\t Figur " +
                        figur.getFigurNummer() + " von Feld " + figur.getPosition() + " auf Feld " +
                        this.model.getMoeglicheSchritte().get(figur));
            }
        }
    }

//	private void printQuestion() {
//		Question curQ = this.model.getCurrentQuestion();
//		System.out.println("*******Question******");
//		System.out.format("%s\nAnswers:\n", curQ.getQuestionBody());
//
//		for (int i = 0; i < curQ.getAnswers().length; i++)
//			System.out.format("#%d: %s\n", i, curQ.getAnswers()[i]);
//
//		System.out.println("*******Question******");
//	}

    private void printFieldStatus() {
        for (Spieler spieler : this.model.getSpielerliste()) {
            String figurenImSpielAusgabe = "";
            int figurenCounter = 0;
            for (Figur figur : spieler.getFiguren()) {
                if (figur.getPosition() != -1) {
                    figurenImSpielAusgabe += "\t Figur " + figur.getFigurNummer() + " Feld " + figur.getPosition() + "\n";
                    figurenCounter++;
                }
            }
            System.out.println(spieler.getSpielerName() + ": \t" + figurenCounter + " von 3 Figuren im Spiel");
            System.out.println(figurenImSpielAusgabe);
        }
    }
}
