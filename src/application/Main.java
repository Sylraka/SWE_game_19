package application;

import java.util.ArrayList;
import java.util.List;

import application.gui.ConsoleIOHandler;
import application.makemove.MakeMoveFactory;
import application.makemove.impl.players.Spieler;

public class Main {
	public static void main(String[] args) {
		List<Spieler> players = new ArrayList<Spieler>();
		players.add(new Spieler(1));
		players.add(new Spieler(2));
		players.add(new Spieler(3));
		players.add(new Spieler(4));


		MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(players);
		new ConsoleIOHandler();
//		LauncherImpl.launchApplication(GUIPreloader.class, args);
	}

}
