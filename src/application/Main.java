package application;

import java.util.ArrayList;
import java.util.List;

import application.gui.ConsoleIOHandler;
import application.makemove.MakeMoveFactory;
import application.makemove.impl.players.Spieler;

public class Main {
	public static void main(String[] args) {
		List<Spieler> spieler = new ArrayList<Spieler>();
		spieler.add(new Spieler(1));
		spieler.add(new Spieler(2));
		spieler.add(new Spieler(3));
		spieler.add(new Spieler(4));


		MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(spieler);
		new ConsoleIOHandler();
//		LauncherImpl.launchApplication(GUIPreloader.class, args);
	}

}
