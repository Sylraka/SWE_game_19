package application;

import application.gui.GUIPreloader;
import java.util.ArrayList;
import java.util.List;

import application.gui.ConsoleIOHandler;
import com.sun.javafx.application.LauncherImpl;

import application.makemove.MakeMoveFactory;
import application.makemove.impl.players.Colors;
import application.makemove.impl.players.Player;

public class Main {
	public static void main(String[] args) {
		List<Player> players = new ArrayList<Player>();
		players.add(new Player(1, Colors.RED, "Red guy"));
		players.add(new Player(2, Colors.BLUE, "Blue guy"));
//		players.add(new Player(3, Colors.GREEN, "Green guy"));
//		players.add(new Player(4, Colors.YELLOW, "Yellow guy"));

		MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(players);
		new ConsoleIOHandler();
//		LauncherImpl.launchApplication(GUIPreloader.class, args);
	}

}
