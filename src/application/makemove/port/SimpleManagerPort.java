package application.makemove.port;

import application.makemove.impl.players.Spieler;
import java.util.List;

public interface SimpleManagerPort {

	MakeMoveManagement makeMoveManagement(List<Spieler> players);
}
