package application.statemachine.port;

import application.statemachine.port.Observer;

public interface Subject
{

	public void attach(Observer obs);

	public void detach(Observer obs);

}
