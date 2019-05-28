package application.makemove.impl.players;

public enum Colors {
	ROT("rot"), BLAU("blau"), GELB("gelb"), GRUEN("gruen");

	private final String colorName;

	private Colors(String s) {
		colorName = s;
	}

	public String getName() {
		return this.colorName;
	}
}
