package application.makemove;

import java.util.List;
import java.util.Map;

import application.makemove.impl.MakeMoveManagementImpl;
import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;
import application.makemove.port.MakeMoveManagement;
import application.makemove.port.SimpleManagerPort;
import application.statemachine.StateMachineFactory;
import application.statemachine.port.State;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;

public class MakeMoveFactoryImpl implements MakeMoveFactory, MakeMoveManagement, SimpleManagerPort {
	private StateMachinePort stateMachinePort = StateMachineFactory.FACTORY.stateMachinePort();

	private StateMachine stateMachine;
	private MakeMoveManagementImpl moveManager;

	private void mkMoveManager(List<Spieler> players) {
		if (this.moveManager == null) {
			this.stateMachine = this.stateMachinePort.stateMachine();
			this.moveManager = new MakeMoveManagementImpl(this.stateMachinePort, players);
		}
	}

	@Override
	public SimpleManagerPort simpleManagerPort() {
		return this;
	}

	@Override
	public MakeMoveManagement makeMoveManagement(List<Spieler> players) {
		this.mkMoveManager(players);
		return this;
	}

	@Override
	public void neueRundeStarten() {
		if (!this.stateMachine.getState().isSubStateOf(State.S.InitialState))
			return;
		this.moveManager.neueRundeStarten();
	}

	@Override
	public void wuerfeln() {
		if (!this.stateMachine.getState().isSubStateOf(State.S.WurfState))
			return;
		this.moveManager.wuerfeln();
	}

	@Override
	public void bewegeFigur(int figurNummer) {
		if (!this.stateMachine.getState().isSubStateOf(State.S.WahlState))
			return;
		this.moveManager.bewegeFigur(figurNummer);
	}

	@Override
	public int getAktuelleRunde() {
		return this.moveManager.getAktuelleRunde();
	}

	@Override
	public Spieler getAktuellerSpieler() {
		return this.moveManager.getAktuellerSpieler();
	}

	@Override
	public int getAugenzahl() {
		return this.moveManager.getAugenzahl();
	}

	@Override
	public int getUebrigeAnzahlVersuche() {
		return this.moveManager.getUebrigeAnzahlVersuche();
	}

	@Override
	public List<Spieler> getSpielerliste() {
		return this.moveManager.getSpielerliste();
	}

	@Override
	public void spielBeenden() {
		if (!this.stateMachine.getState().isSubStateOf(State.S.EndGameState))
			return;
		this.moveManager.spielBeenden();
	}

	@Override
	public Spieler getGewinner() {
		if (!this.stateMachine.getState().isSubStateOf(State.S.EndGameState))
			return null;
		return this.moveManager.getGewinner();
	}

	@Override
	public Map<Figur, Integer> getMoeglicheSchritte() {
		return this.moveManager.getMoeglicheSchritte();
	};
	// TODO: delete, for debugging only
	// @Override
	// public void throwCheatDice(int cheatDice){
	// if (!this.stateMachine.getState().isSubStateOf(State.S.WurfState))
	// return;
	// this.moveManager.throwCheatDice(cheatDice);
	// }
	//
	// //TODO: delete, for debugging only
	// @Override
	// public Figur getFigureByField(int pos){
	// return this.moveManager.getFigureByField(pos);
	// }

}
