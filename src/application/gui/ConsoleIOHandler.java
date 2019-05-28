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
		
		
		// unsere states sind: initial, wurf, wahl
		
		if (currentState == State.S.InitialState) {
			posKeys = "y - begin round";
		}

		if (currentState == State.S.WurfState) {
			posKeys = "t - throw";
			System.out.println("Tries left: " + this.model.getTriesLeft());
		}

		if (currentState == State.S.WahlState) {
			posKeys = "number of option";
			printChooseMove();
		}

		if (currentState == State.S.EndGameState) {
			System.out.println("Winner: "+this.model.getWinner().getPlayerName());
			posKeys = "e - exit";
		}

		if (currentState == State.S.IsSelfAnswer) {
			posKeys = "y - yes, n - no";
			System.out.println("Do you wish to answer yourself?");
		}

		System.out.format("Possible keys to press: ( %s )\n", posKeys);
		System.out.println("====================");
	}

	public void update(State newState) {
		if (newState == null)
			return;

		if (currentState == State.S.WurfState) {
			System.out.println("*******************");
			System.out.println("Thrown: " + this.model.getDiceNumber());
			System.out.println("*******************");
		}

		currentState = newState;
	}

	private void printChooseMove() {
		if (currentState == State.S.WahlState) {
			System.out.println("*******************");
			System.out.println("It's time to choose, Mr.Freeman.");
			int i = 0;
			for (MoveOps mo : this.model.getMoveOps()) {
				if (mo.figureDefender == null) {
					System.out.format("Option#%d: FigureID#%d from position (%d) to %d. Free field!\n", i,
							mo.figureAttacker.getId(), mo.figureAttacker.getPosition(), mo.destinationField);
				} else if (mo.figureDefender.getPlayerId() == mo.figureAttacker.getPlayerId()) {
					System.out.format("Option#%d: FigureID#%d from position (%d) to %d. -1Regel (attacker %d, defender %d)!\n", i,
							mo.figureAttacker.getId(), mo.figureAttacker.getPosition(), mo.destinationField,
							mo.figureAttacker.getPlayerId(), mo.figureDefender.getPlayerId());
				}

				else {
					String defPlayerName = "";
					for (Player pl : this.model.allPlayers()) {
						if (pl.getId() == mo.figureDefender.getPlayerId()) {
							defPlayerName = pl.getPlayerName();
							break;
						}
					}

					System.out.format(
							"Option#%d: FigureID#%d from position (%d) to %d. Attack on %s (attacker %d, defender %d)!\n", i,
							mo.figureAttacker.getId(), mo.figureAttacker.getPosition(), mo.destinationField, defPlayerName,
							mo.figureAttacker.getPlayerId(), mo.figureDefender.getPlayerId());
				}
				i++;
			}
			System.out.println("*******************");
		}
	}

	private void printQuestion() {
		Question curQ = this.model.getCurrentQuestion();
		System.out.println("*******Question******");
		System.out.format("%s\nAnswers:\n", curQ.getQuestionBody());

		for (int i = 0; i < curQ.getAnswers().length; i++)
			System.out.format("#%d: %s\n", i, curQ.getAnswers()[i]);

		System.out.println("*******Question******");
	}

	private void printFieldStatus() {
		System.out.println("*******STATUS******");
		for (Player pl : this.model.allPlayers()) {
			System.out.println("\nStatus of player " + pl.getPlayerName());
			System.out.println("---------------------------------");
			System.out.print("RED: " + pl.getKnowledgeLevelsByCategory(QuestionCategories.RED).getLvl());
			System.out.print(", BLUE: " + pl.getKnowledgeLevelsByCategory(QuestionCategories.BLUE).getLvl());
			System.out.print(", YELLOW: " + pl.getKnowledgeLevelsByCategory(QuestionCategories.YELLOW).getLvl());
			System.out.print(", GREEN: " + pl.getKnowledgeLevelsByCategory(QuestionCategories.GREEN).getLvl());
			System.out.println("\n---------------------------------");
			for (Figure fg : pl.getFigures()) {
				System.out.format("Figure#%d: position = %d\n", fg.getId(), fg.getPosition());
			}
		}
		System.out.println("*****STATUS*******");
	}

	private void printFigureMap() {
		System.out.println("*******ALL FIELDS******");
		for (int i = 0; i < 48; i++) {
			Figure curFig = this.model.getFigureByField(i);
			if (curFig == null)
				System.out.format("On the field %d: nothing...\n", i);
			else
				System.out.format("On the field %d(self->%d): figure#%d (Player#%d)\n", i, curFig.getPosition(), curFig.getId(),
						curFig.getPlayerId());
		}
		System.out.println("*******ALL FIELDS******");
	}

}
