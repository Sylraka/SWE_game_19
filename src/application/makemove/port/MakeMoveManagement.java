package application.makemove.port;

import java.util.List;
import java.util.Map;

import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;

/**
 * @url element://model:project::SWE_Spiel/jdt:e_class:src:design.comp.MakeMoveManagement
 */

public interface MakeMoveManagement {

	void neueRundeStarten();

	void wuerfeln();

	void bewegeFigur(int figurNummer);
	
	// TODO später löschen, interface aufräumen
	// void chooseQuestionFromCategory(QuestionCategories qCat);

	// void answerQuestion(int answer);

	// void selfAnswer(boolean isSelfAnswer);

	int getAktuelleRunde();

	int getAugenzahl();

	int getUebrigeAnzahlVersuche();

	boolean istEinfacheVariante();

	List<Spieler> getSpielerliste();

	Spieler getAktuellerSpieler();

	Map<Figur, Integer> getMoeglicheSchritte();

	// Question getCurrentQuestion();

	void spielBeenden();

	// boolean isQuestionAnsweredCorrectly();

	Spieler getGewinner();

	// TODO: delete, for debugging
	//void throwCheatDice(int cheatDice);

	// TODO: delete, for debugging
	//Figur getFigureByField(int pos);

}
