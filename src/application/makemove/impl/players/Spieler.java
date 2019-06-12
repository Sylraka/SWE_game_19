package application.makemove.impl.players;

public class Spieler {

	private int[] startFeld = { 0, 12, 24, 36 };
	private int spielerNummer;
	private String[] farben = { "ROT", "BLAU", "GRUEN", "GELB" };
	private String name;

	private Figur[] spielerFiguren = new Figur[3];

	public Spieler(int spielerNummer) {
		this.spielerNummer = spielerNummer;
		// farbe/name wird abhängig von der Spielernummer aus dem String farben gesetzt
		this.name = farben[spielerNummer - 1];
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

	public int getStartFeld() {
		return this.startFeld[this.spielerNummer];
	}

	public Figur[] getFiguren() {
		return spielerFiguren;
	}

	// setzt die 3 figuren des Spielers, auf ihr jeweiliges Heimatfeld
	private void setzeFiguren() {
		for (int i = 0; i < 3; i++) {
			spielerFiguren[i] = new Figur(i);
		}

	}
}