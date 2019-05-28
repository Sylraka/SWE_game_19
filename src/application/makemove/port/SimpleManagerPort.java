package application.makemove.port;

import application.makemove.impl.players.Player;
import java.util.List;

public interface SimpleManagerPort {

	MakeMoveManagement makeMoveManagement(List<Player> players);
}
