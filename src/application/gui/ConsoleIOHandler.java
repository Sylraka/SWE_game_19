package application.gui;

import application.makemove.impl.questions.KnowledgeLevel;
import application.makemove.impl.questions.KnowledgeLevel.QuestionCategories;
import application.makemove.impl.questions.Question;
import application.makemove.impl.Figure;
import application.makemove.impl.players.Player;
import application.makemove.impl.MoveOps;
import application.logic.LogicFactory;
import application.logic.port.MVCPort;
import application.statemachine.port.State;
import application.makemove.MakeMoveFactory;
import application.makemove.port.MakeMoveManagement;
import application.statemachine.port.Observer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

	private void initComponents() {
		getInput();
	}

	private void getInput() {
		String str = new String();
		BufferedReader in;
		while (true) {
			printKeyPrompts();
			try {
				in = new BufferedReader(new InputStreamReader(System.in));
				str = in.readLine();
			} catch (IOException e) {
				System.out.println("EXCEPTION");
			}

			if (str.equals("st")) {
				printFieldStatus();
				continue;
			}

			if (str.equals("map")) {
				printFigureMap();
				continue;
			}

			if (str.equals("t") && currentState == State.S.WurfState) {
				this.model.throwDice();
				continue;
			}

			if (str.matches(".*\\d+.*") && currentState == State.S.WurfState) {
				this.model.throwCheatDice(Integer.parseInt(str));
				continue;
			}

			if (str.equals("y") && currentState == State.S.InitialState) {
				this.model.startNewRound();
				continue;
			}

			if (str.equals("y") && currentState == State.S.IsSelfAnswer) {
				this.model.selfAnswer(true);
				continue;
			}

			if (str.equals("n") && currentState == State.S.IsSelfAnswer) {
				this.model.selfAnswer(false);
				continue;
			}

			if (str.matches(".*\\d+.*") && currentState == State.S.WahlState) {
				this.model.chooseMove(Integer.parseInt(str));
				continue;
			}

			if (str.matches("r") && currentState == State.S.ChooseQCategoryState) {
				this.model.chooseQuestionFromCategory(QuestionCategories.RED);
				continue;
			}

			if (str.matches(".*\\d+.*") && currentState == State.S.AnswerQState) {
				this.model.answerQuestion(Integer.parseInt(str));
				continue;
			}

			if (str.matches("e") && currentState == State.S.EndGameState) {
				this.model.endGame();
			}
		}
	}

	private void printKeyPrompts() {
		String posKeys = new String();

		//System.out.println("====================");
		// System.out.println("State: " + currentState.toString());
		System.out.println(this.model.getCurrentPlayer().getPlayerName() + " ist am Zug");
		
		
		// unsere states sind: initial, wurf, wahl, evtl end
		
//		if (currentState == State.S.InitialState) {
//			posKeys = "y - begin round";
//		}

		if (currentState == State.S.WurfState) {
			posKeys = "";
			System.out.println("Wurf: " + (3 - this.model.getTriesLeft()));
		}

		if (currentState == State.S.WahlState) {
//			posKeys = 
			printChooseMove();
			System.out.format("Figur zum Bewegen auswählen\n" + 
					"Drücken Sie");
			//TODO: Optionen ausgeben

		}

//		if (currentState == State.S.EndGameState) {
//			System.out.println("Winner: "+this.model.getWinner().getPlayerName());
//			posKeys = "e - exit";
//		}

//		if (currentState == State.S.IsSelfAnswer) {
//			posKeys = "y - yes, n - no";
//			System.out.println("Do you wish to answer yourself?");
//		}

//		System.out.format("Possible keys to press: ( %s )\n", posKeys);
//		System.out.println("====================");
	}

	public void update(State newState) {
		if (newState == null)
			return;

		if (currentState == State.S.WurfState) {
			System.out.println("Gewürfelte Zahl: " + this.model.getDiceNumber());
		}

		currentState = newState;
	}

	private void printChooseMove() {
		if (currentState == State.S.WahlState) {
			for (MoveOps mo : this.model.getMoveOps()) {
				// Neues Feld is unbesetzt
				if (mo.figureDefender == null) {
					System.out.format("Figur %d von Feld (%d) auf Feld %d.\n", 
							mo.figureAttacker.getId(), mo.figureAttacker.getPosition(), mo.destinationField);
					
				// Neues Feld ist von eigener Figur besetzt
				// TODO: Regelabfrage ob selbst Frage gestellt werden darf
				} else if (mo.figureDefender.getPlayerId() == mo.figureAttacker.getPlayerId()) {
					System.out.format("Figur %d von Feld (%d) auf Feld %d.\n", 
							mo.figureAttacker.getId(), mo.figureAttacker.getPosition(), mo.destinationField);
				
				// Neues Feld ist von einer Figur eines Mitspielers besetzt, haben wir nicht umgesetzt
				} else {
					//TODO: Hier wird der Name des Spielers ermittelt der sich auf dem Feld befindet, bei uns nicht verwendet
//					String defPlayerName = "";
//					for (Player pl : this.model.allPlayers()) {
//						if (pl.getId() == mo.figureDefender.getPlayerId()) {
//							defPlayerName = pl.getPlayerName();
//							break;
//						}
//					}

					System.out.format(
							"Figur %d von Feld (%d) auf Feld %d.\n", 
							mo.figureAttacker.getId(), mo.figureAttacker.getPosition(), mo.destinationField);
				}
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
	
//TODO 
	private void printFieldStatus() {
		for (Player pl : this.model.allPlayers()) {
			System.out.println("\n" + pl.getPlayerName() + ":\t");
			for (Figure fg : pl.getFigures()) {
				System.out.format("Figur %d: Feld %d\n", fg.getId(), fg.getPosition());
			}
		}
	}

	private void printFigureMap() {
		//TODO ausgabe anpassen: anfrage der felder über den gespeicherten wert in figure.java
		for (int i = 0; i < 48; i++) {
			Figure curFig = this.model.getFigureByField(i);
			if (curFig != null) {
				System.out.format("On the field %d(self->%d): figure#%d (Player#%d)\n", i, curFig.getPosition(), curFig.getId(),
						curFig.getPlayerId());
		}
			}
	}

}
