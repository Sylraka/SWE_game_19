package application.statemachine.impl;

import java.util.ArrayList;
import java.util.List;

import application.statemachine.port.Observer;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachine;
import application.statemachine.port.Subject;

public class StateMachineImpl implements StateMachine, Subject {





	/**
	 * @directed true
	 * @link aggregation
	 * @supplierRole observer
	 */

	private List<Observer> observers = new ArrayList<>();



	/**
	 * @link aggregation
	 * @supplierNavigability NAVIGABLE_EXPLICITLY
	 * @supplierRole currentState
	 */

	private application.statemachine.port.StateMachine.State currentState;


	public StateMachineImpl(){
		this.currentState = application.statemachine.port.StateMachine.State.S.Initial;
	}


	@Override
	public void attach(Observer obs) {
		this.observers.add(obs);
		obs.update(this.currentState);
	}

	@Override
	public void detach(Observer obs) {
		this.observers.remove(obs);
	}

	@Override
	public application.statemachine.port.StateMachine.State getState() {
		return this.currentState;
	}

	@Override
	public void setState(application.statemachine.port.StateMachine.State state) {
		this.currentState = state;
		this.observers.forEach(obs -> obs.update(state));
	}

}
