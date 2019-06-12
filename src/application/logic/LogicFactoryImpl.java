package application.logic;

import java.util.List;

import application.logic.port.MVCPort;
import application.logic.port.ManagerPort;
import application.makemove.MakeMoveFactory;
import application.makemove.impl.players.Spieler;
import application.makemove.port.MakeMoveManagement;
import application.makemove.port.SimpleManagerPort;
import application.statemachine.StateMachineFactory;
import application.statemachine.port.Subject;
import application.statemachine.port.SubjectPort;

public class LogicFactoryImpl implements LogicFactory, ManagerPort, MVCPort {

	private SubjectPort subjectPort = StateMachineFactory.FACTORY.subjectPort();
	private SimpleManagerPort simpleManagerPort = MakeMoveFactory.FACTORY.simpleManagerPort();

	@Override
	public synchronized MVCPort MVCPort() {
		return this;
	}

	@Override
	public synchronized ManagerPort managerPort() {
		return this;
	}

	@Override
	public synchronized Subject subject() {
		return this.subjectPort.subject();
	}

	@Override
	public MakeMoveManagement makeMoveManagement(List<Spieler> players) {
		return this.simpleManagerPort.makeMoveManagement(players);
	}

}
