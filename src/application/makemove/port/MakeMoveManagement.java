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
	
	int getAktuelleRunde();

	int getAugenzahl();

	int getUebrigeAnzahlVersuche();

	boolean istEinfacheVariante();

	List<Spieler> getSpielerliste();

	Spieler getAktuellerSpieler();

	Map<Figur, Integer> getMoeglicheSchritte();

	void spielBeenden();

	Spieler getGewinner();
	
	void frageBeantworten(boolean isRichtig);

//	 TODO: Für Testzwecke
	void zahlWuerfeln(int augenzahl);

}
