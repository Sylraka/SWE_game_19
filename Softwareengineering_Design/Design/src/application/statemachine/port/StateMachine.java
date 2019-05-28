package application.statemachine.port;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface StateMachine {

	public void setState(application.statemachine.port.StateMachine.State state);

	public application.statemachine.port.StateMachine.State getState();

	public static interface State {
	
			public boolean isSubStateOf(State state);
	
			public boolean isSuperStateOf(State state);
	
			public enum S implements State {
	
				Initial, Wurf, Wahl, Frage;
	
				/**
				* @clientNavigability NAVIGABLE
				* @directed true
				* @supplierRole subState
				*/
	
				private List<State> subStates;
	
				private S(State... subS) {
					this.subStates = new ArrayList<>(Arrays.asList(subS));
				}
	
				@Override
				public boolean isSuperStateOf(State s) {
					boolean result = s == null || this == s; // self contained
					for (State state : this.subStates) // or
						result |= state.isSuperStateOf(s); // contained in a substate!
					return result;
				}
	
				@Override
				public boolean isSubStateOf(State state) {
					return (state == null) ? false : state.isSuperStateOf(this);
				}
			}
	
		}
}
