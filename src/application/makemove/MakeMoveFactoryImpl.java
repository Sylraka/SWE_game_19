package application.makemove;

import application.makemove.impl.MoveOps;
import java.util.List;
import application.statemachine.port.State;
import application.makemove.impl.MakeMoveManagementImpl;
import application.statemachine.port.StateMachine;
import application.statemachine.StateMachineFactory;
import application.statemachine.port.StateMachinePort;
import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;
import application.makemove.port.MakeMoveManagement;
import application.makemove.port.SimpleManagerPort;

public class MakeMoveFactoryImpl implements MakeMoveFactory, MakeMoveManagement, SimpleManagerPort{
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
	public void startNewRound() {
		if (!this.stateMachine.getState().isSubStateOf(State.S.InitialState))
			return;
		this.moveManager.startNewRound();
	}

	@Override
	public void throwDice() {
		if (!this.stateMachine.getState().isSubStateOf(State.S.WurfState))
			return;
		this.moveManager.throwDice();
	}

	@Override
	public void chooseMove(int optionId) {
		if (!this.stateMachine.getState().isSubStateOf(State.S.WahlState))
			return;
		this.moveManager.chooseMove(optionId);
	}


	@Override
	public int getRoundId() {
		return this.moveManager.getRoundId();
	}

	@Override
	public Spieler getCurrentPlayer() {
		return this.moveManager.getCurrentPlayer();
	}

	@Override
	public int getDiceNumber() {
		return this.moveManager.getDiceNumber();
	}

	@Override
	public int getTriesLeft() {
		return this.moveManager.getTriesLeft();
	}

	@Override
	public List<Spieler> allPlayers(){
		return this.moveManager.allPlayers();
	}

	@Override
	public List<MoveOps> getMoveOps() {
		return this.moveManager.getMoveOps();
	}

	@Override
	public void endGame(){
		if (!this.stateMachine.getState().isSubStateOf(State.S.EndGameState))
			return;
		this.moveManager.endGame();
	}

	@Override
	public Spieler getWinner(){
		if (!this.stateMachine.getState().isSubStateOf(State.S.EndGameState))
			return null;
		return this.moveManager.getWinner();
	}

	//TODO: delete, for debugging only
	@Override
	public void throwCheatDice(int cheatDice){
		if (!this.stateMachine.getState().isSubStateOf(State.S.WurfState))
			return;
		this.moveManager.throwCheatDice(cheatDice);
	}

	//TODO: delete, for debugging only
	@Override
  public Figur getFigureByField(int pos){
		return this.moveManager.getFigureByField(pos);
	}

}
