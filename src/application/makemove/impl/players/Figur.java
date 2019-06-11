package application.makemove.impl.players;

public class Figur {

	private final int figurNummer;
	private int position;

	public Figur(int figurNummer) {
		this.figurNummer = figurNummer;
		this.position = -1;// home
	}

	public int getFigurNummer() {
		return figurNummer;
	}

	public void setPosition(int pos) {
		position = pos;
	}

	public int getPosition() {
		return position;
	}

	public boolean isHeimatsfeld() {
		return this.position == -1;
	}
}
