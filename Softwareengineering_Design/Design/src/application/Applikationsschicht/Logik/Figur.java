package application.Applikationsschicht.Logik;

public class Figur {
	private int figurNummer;
	private int position;

	public int getFigurNummer() {
		return figurNummer;
	}

	public void setFigurNummer(int figurNummer) {
		this.figurNummer = figurNummer;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isHeimatsfeld() {
		return this.position == -1;
	}
}
