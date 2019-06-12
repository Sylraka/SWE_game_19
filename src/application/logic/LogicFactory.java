package application.logic;

import application.logic.port.MVCPort;
import application.logic.port.ManagerPort;

public interface LogicFactory {

	LogicFactory FACTORY = new LogicFactoryImpl();

	MVCPort MVCPort();

	ManagerPort managerPort();
}
