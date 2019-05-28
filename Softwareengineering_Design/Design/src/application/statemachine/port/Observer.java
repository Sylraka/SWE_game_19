package application.statemachine.port;

public interface Observer {

	public void update(application.statemachine.port.StateMachine.State newState);

}
