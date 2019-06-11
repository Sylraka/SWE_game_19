package application.makemove.impl.players;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class Spieler {

	private int[] startFeld = { 0, 12, 24, 36 };
	private int spielerNummer;
	private String[] farben = {"ROT","BLAU","GRUEN","GELB"};
	private String name;
	private int startField;

	private Figur[] spielerFiguren;

	public Spieler(int spielerNummer) {
		this.spielerNummer = spielerNummer;
		// farbe/name wird abhängig von der Spielernummer aus dem String farben gesetzt
		this.name = farben[spielerNummer];
		setzeFiguren();
	}

	public int getSpielerNummer() {
		return spielerNummer;
	}

	public void setSpielerNummer(int id) {
		this.spielerNummer = id;
	}

	public String getSpielerName() {
		return name;
	}

	public String getFarbe() {
		return this.farben[this.spielerNummer];
	}

	public int getStartField() {
		return this.startFeld[this.spielerNummer];
	}

	public Figur[] getFiguren() {
		return spielerFiguren;
	}

	//setzt die 3 figuren des Spielers, auf ihr jeweiliges Heimatfeld
	private void setzeFiguren() {
		for (int i = 0; i < 3; i++) {
			spielerFiguren[i] = new Figur(i);
		}

	}
}