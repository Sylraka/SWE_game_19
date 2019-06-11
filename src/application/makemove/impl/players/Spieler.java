package application.makemove.impl.players;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import application.makemove.impl.players.Colors;

public class Spieler {
	private static final int NUMBER_OF_FIGURES = 3;

	private int id;
	private Colors color;
	private String name;
	private int startField;

	private Figur[] playerFigures;

	public Spieler(int id, Colors color) {
		this.id = id;
		this.color = color;
		this.name = color.getName();
		initializeFields();
	}

	public Spieler(int id, Colors color, String name) {
		this.id = id;
		this.name = name;
		this.color = color;
		initializeFields();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlayerName() {
		return name;
	}

	public Colors getColor() {
		return color;
	}

	public void setStartField(int startField) {
		this.startField = startField;
	}

	public int getStartField() {
		return startField;
	}

	public Figur[] getFigures() {
		return playerFigures;
	}

	private void initializeFields() {
		playerFigures = new Figur[NUMBER_OF_FIGURES];
		for (int i = 0; i < playerFigures.length; i++) {
			playerFigures[i] = new Figur(i);
		}

	}
}