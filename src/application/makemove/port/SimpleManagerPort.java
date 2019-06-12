package application.makemove.port;

import java.util.List;

import application.makemove.impl.players.Spieler;

public interface SimpleManagerPort {

	MakeMoveManagement makeMoveManagement(List<Spieler> players);
}
