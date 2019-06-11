package application.makemove.port;

import java.util.List;
import java.util.Map;

import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;

/**
 * @url element://model:project::SWE_Spiel/jdt:e_class:src:design.comp.MakeMoveManagement
 */

public interface MakeMoveManagement {

	void startNewRound();

	void throwDice();

	void bewegeFigur(Figur figur, int optionId);
	
	// TODO später löschen, interface aufräumen
	// void chooseQuestionFromCategory(QuestionCategories qCat);

	// void answerQuestion(int answer);

	// void selfAnswer(boolean isSelfAnswer);

	int getRoundId();

	int getDiceNumber();

	int getTriesLeft();

	List<Spieler> allPlayers();

	Spieler getCurrentPlayer();

	Map<Figur, Integer> getMoeglicheSchritte();

	// Question getCurrentQuestion();

	void endGame();

	// boolean isQuestionAnsweredCorrectly();

	Spieler getWinner();

	// TODO: delete, for debugging
	void throwCheatDice(int cheatDice);

	// TODO: delete, for debugging
	Figur getFigureByField(int pos);

}
