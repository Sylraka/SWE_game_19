package application.makemove.impl;


import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import application.statemachine.port.State;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;
import application.makemove.port.MakeMoveManagement;
import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;

public class MakeMoveManagementImpl implements MakeMoveManagement {
  private static final int MAX_NUMBER_OF_TRIES = 3;
  private static final int BOARD_SIZE = 48;
  private static final int HOME_POSITION = -1;
  private static final int MOCK_CORRECT_ANSWER = 0;
  private final List<Spieler> players;
  private StateMachine stateMachine;

  private int roundId;
  private int triesLeft;
  private int diceNumber;
  private Spieler currentPlayer;
  private Spieler winner;

  //Two maps for quick search
  private Map<Integer, Figur> figureByField;
  private List<MoveOps> moveOpsList;


  public MakeMoveManagementImpl(StateMachinePort stPort, List<Spieler> players) {
    System.out.println("Game started.");
    System.out.println("Number of players: " + players.size());
    this.stateMachine = stPort.stateMachine();

    this.players = players;
    this.currentPlayer = players.get(0);
    this.roundId = 0;
    this.triesLeft = MAX_NUMBER_OF_TRIES;
    this.figureByField = new HashMap<>();
    this.moveOpsList = new ArrayList<>();

 
    initPlayingBoard();
  }

  @Override
  public void startNewRound() {
    this.stateMachine.setState(State.S.WurfState);
  }

  @Override
  public void throwDice() {
    triesLeft--;
    diceNumber = getRandomDiceNumber();

    if (hasMoves())
      this.stateMachine.setState(State.S.WahlState);
    else if (triesLeft == 0) {
      this.stateMachine.setState(State.S.InitialState);
      prepareForNextRound();
    } else {
      this.stateMachine.setState(State.S.WurfState);
    }
  }

  @Override
  public void chooseMove(int optionId) {
    MoveOps mOps = null;
    try {
      mOps = moveOpsList.get(optionId);
    } catch (IndexOutOfBoundsException e) {
      return;
    }

    resolveFieldConflicts(mOps.figureAttacker, mOps.figureDefender, mOps.destinationField);
  }


  @Override
  public void endGame() {
    //TODO: Switch to next use case (Statistics) in next Sprint
    System.out.println("Game ended.");
    System.exit(0);
  }

  @Override
  public Spieler getWinner(){
    return this.winner;
  }

  @Override
  public int getRoundId() {
    return this.roundId;
  }

  @Override
  public Spieler getCurrentPlayer() {
    return this.currentPlayer;
  }

  @Override
  public int getDiceNumber() {
    return diceNumber;
  }

  @Override
  public List<Spieler> allPlayers() {
    return this.players;
  }

  @Override
  public int getTriesLeft() {
    return this.triesLeft;
  }

  @Override
  public List<MoveOps> getMoveOps() {
    return this.moveOpsList;
  }


  private void initPlayingBoard() {
    int currentStartMark = 0;
    for (Spieler pl : players) {
      pl.setStartField(currentStartMark);
      currentStartMark += BOARD_SIZE / players.size();
    }
  }

  private boolean hasMoves() {
    this.moveOpsList.clear();
    if (inJailBreak()) {
      if (diceNumber != 6)
        return false;
      setJailBreakMoveOps();
    } else
      calculateMoveOps();

    return true;
  }

  private boolean inJailBreak() {
    for (Figur f : currentPlayer.getFiguren()) {
      if (f.getPosition() != HOME_POSITION)
        return false;
    }
    return true;
  }

  private void prepareForNextRound() {
    moveOpsList.clear();

    setNextRound();
    setNextRoundPlayer();

    refreshTries();
    refreshDice();
  }

  private int getRandomDiceNumber() {
    return new Random().nextInt(6) + 1;
  }

  private void refreshDice() {
    diceNumber = 0;
  }

  private void setNextRound() {
    roundId++;
  }

  private void setNextRoundPlayer() {
    if (currentPlayer == null) {
      currentPlayer = players.get(0);
      return;
    }

    int nextId = (players.indexOf(currentPlayer) + 1) % players.size();
    currentPlayer = players.get(nextId);
  }

  private void refreshTries() {
    if (inJailBreak())
      triesLeft = MAX_NUMBER_OF_TRIES;
    else
      triesLeft = 1;
  }

  private void calculateMoveOps() {
    int destPosition;
    boolean hasAtHome = false;

    if (diceNumber == 6) {
      for (Figur f1 : currentPlayer.getFiguren()) {
        if (f1.getPosition() == HOME_POSITION) {
          hasAtHome = true;
          destPosition = currentPlayer.getStartField();
        } else {
          destPosition = (f1.getPosition() + diceNumber) % BOARD_SIZE;
        }

        Figur f2 = figureByField.get(destPosition);
        this.moveOpsList.add(new MoveOps(f1, f2, destPosition));
      }
      if (hasAtHome) {
        for (Iterator<MoveOps> iterator = moveOpsList.iterator(); iterator.hasNext();) {
          MoveOps opt = iterator.next();
          if (opt.figureAttacker.getPosition() != HOME_POSITION) {
            iterator.remove();
          }
        }
      }

    } else {
      for (Figur f1 : currentPlayer.getFiguren()) {
        if (f1.getPosition() == HOME_POSITION)
          continue;

        destPosition = (f1.getPosition() + diceNumber) % BOARD_SIZE;
        Figur f2 = figureByField.get(destPosition);
        this.moveOpsList.add(new MoveOps(f1, f2, destPosition));
      }
    }
  }

  private void setJailBreakMoveOps() {
    for (Figur f1 : currentPlayer.getFiguren()) {
      Figur f2 = figureByField.get(currentPlayer.getStartField());
      this.moveOpsList.add(new MoveOps(f1, f2, currentPlayer.getStartField()));
    }
  }

  private void resolveFieldConflicts(Figur attackerFig, Figur defenderFig, int destField) {
    if (defenderFig == null) {

      setFigureNewPosition(attackerFig, destField, true);
      this.stateMachine.setState(State.S.InitialState);
      prepareForNextRound();

    } else if (attackerFig.getPlayerId() != defenderFig.getPlayerId()) {

      this.moveOpsList.clear();
      this.moveOpsList.add(new MoveOps(attackerFig, defenderFig, defenderFig.getPosition()));

      this.stateMachine.setState(State.S.ChooseQCategoryState);
    } else {
      applyCascadeResolution(attackerFig, defenderFig.getPosition());
    }
  }

  private void applyCascadeResolution(Figur fig, int destField) {
    Figur targetFig = figureByField.get(destField);
    while (targetFig != null) {
      destField = decreaseFieldNumb(destField);
      targetFig = figureByField.get(destField);
    }

    setFigureNewPosition(fig, destField, false);
    this.stateMachine.setState(State.S.InitialState);
    prepareForNextRound();
  }

  private int decreaseFieldNumb(int destField) {
    if (destField == 0)
      return 47;
    else
      return --destField;
  }

  private Spieler getPlayerById(int playerId) {
    for (Spieler pl : players) {
      if (pl.getSpielerNummer() == playerId)
        return pl;
    }
    return null;
  }

  private void setFigureNewPosition(Figur figure, int position, boolean withRemove) {
    if (figure == null)
      return;

    System.out.format("Replaced figure#%d of player#%d from %d to %d\n", figure.getFigurNummer(), figure.getPlayerId(),
        figure.getPosition(), position);

    if (withRemove)
      figureByField.remove(figure.getPosition());

    getPlayerById(figure.getPlayerId()).getFiguren()[figure.getFigurNummer()].setPosition(position);

    Figur oldFig = figureByField.get(position);
    if (oldFig != null)
      figureByField.remove(position);

    figureByField.put(position, figure);
  }

  //TODO: delete, for debugging
  public void throwCheatDice(int cheatDice) {
    triesLeft--;
    diceNumber = cheatDice;

    if (hasMoves())
      this.stateMachine.setState(State.S.WahlState);
    else if (triesLeft == 0) {
      this.stateMachine.setState(State.S.InitialState);
      prepareForNextRound();
    } else {
      this.stateMachine.setState(State.S.WurfState);
    }
  }

  //TODO: delete, for debugging
  public Figur getFigureByField(int pos) {
    return this.figureByField.get(pos);
  }

}
