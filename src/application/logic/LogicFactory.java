package application.logic;

import application.logic.port.ManagerPort;
import application.logic.port.MVCPort;

public interface LogicFactory {

	LogicFactory FACTORY = new LogicFactoryImpl();

	MVCPort MVCPort();

	ManagerPort managerPort();
}
