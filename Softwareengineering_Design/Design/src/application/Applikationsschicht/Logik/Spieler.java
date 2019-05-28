package application.Applikationsschicht.Logik;

import java.util.ArrayList;

public class Spieler {
	private int spielerNummer;
	public int getSpielerNummer() {
		return spielerNummer;
	}
	public void setSpielerNummer(int spielerNummer) {
		this.spielerNummer = spielerNummer;
	}
	public int getStartFeld() {
		return startFeld;
	}
	public void setStartFeld(int startFeld) {
		this.startFeld = startFeld;
	}
	public String getSpielerName() {
		return spielerName;
	}
	public void setSpielerName(String spielerName) {
		this.spielerName = spielerName;
	}
	public String getFarbe() {
		return farbe;
	}
	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}
	public ArrayList<Figur> getFiguren() {
		return figuren;
	}
	public void setFiguren(ArrayList<Figur> figuren) {
		this.figuren = figuren;
	}
	private int startFeld;
	private String spielerName;
	private String farbe;
	private ArrayList<Figur> figuren;

	
}
