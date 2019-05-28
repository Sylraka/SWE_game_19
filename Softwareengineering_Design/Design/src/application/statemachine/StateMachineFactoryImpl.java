package application.statemachine;

import application.statemachine.impl.StateMachineImpl;
import application.statemachine.port.Observer;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;
import application.statemachine.port.Subject;
import application.statemachine.port.SubjectPort;

class StateMachineFactoryImpl implements StateMachineFactory, SubjectPort, StateMachinePort, StateMachine, Subject {

	/**
	 * @directed true
	 * @link composition
	 * @supplierRole stateMachine
	 */

	private StateMachineImpl stateMachine;

	private void mkStateMachine() {
		if (this.stateMachine == null)
			this.stateMachine = new StateMachineImpl();
	}

	@Override
	public synchronized SubjectPort subjectPort() {
		return this;
	}

	@Override
	public synchronized StateMachinePort stateMachinePort() {
		return this;
	}

	@Override
	public synchronized StateMachine stateMachine() {
		this.mkStateMachine();
		return this;
	}

	@Override
	public synchronized Subject subject() {
		this.mkStateMachine();
		return this;
	}

	@Override
	public synchronized void attach(Observer obs) {
		this.stateMachine.attach(obs);
	}

	@Override
	public synchronized void detach(Observer obs) {
		this.stateMachine.detach(obs);
	}

	@Override
	public synchronized application.statemachine.port.StateMachine.State getState() {
		return this.stateMachine.getState();
	}

	@Override
	public synchronized void setState(application.statemachine.port.StateMachine.State state) {
		this.stateMachine.setState(state);
	}

}
