package application.makemove;

import application.makemove.port.SimpleManagerPort;

public interface MakeMoveFactory {

	MakeMoveFactory FACTORY = new MakeMoveFactoryImpl();

	SimpleManagerPort simpleManagerPort();
}
