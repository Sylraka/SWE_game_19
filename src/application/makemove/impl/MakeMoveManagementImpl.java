package application.makemove.impl;

import application.makemove.impl.questions.KnowledgeLevel.QuestionCategories;
import java.util.Iterator;
import application.makemove.impl.questions.Question;
import application.makemove.impl.questions.QuestionsService;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import application.statemachine.port.State;
import application.statemachine.port.StateMachine;
import application.statemachine.port.StateMachinePort;
import application.makemove.port.MakeMoveManagement;
import application.makemove.impl.players.Player;
import application.makemove.impl.Figure;

public class MakeMoveManagementImpl implements MakeMoveManagement {
  private static final int MAX_NUMBER_OF_TRIES = 3;
  private static final int BOARD_SIZE = 48;
  private static final int HOME_POSITION = -1;
  private static final int MOCK_CORRECT_ANSWER = 0;
  private final List<Player> players;
  private StateMachine stateMachine;

  private int roundId;
  private int triesLeft;
  private int diceNumber;
  private Player currentPlayer;
  private Player winner;

  //Two maps for quick search
  private Map<Integer, Figure> figureByField;
  private List<MoveOps> moveOpsList;

  private boolean lastQuestionAnswer = false;
  private boolean isSelfAnswerQuestion = false;
  private Question currentQuestion;

  public MakeMoveManagementImpl(StateMachinePort stPort, List<Player> players) {
    System.out.println("Game started.");
    System.out.println("Number of players: " + players.size());
    this.stateMachine = stPort.stateMachine();

    this.players = players;
    this.currentPlayer = players.get(0);
    this.roundId = 0;
    this.triesLeft = MAX_NUMBER_OF_TRIES;
    this.figureByField = new HashMap<>();
    this.moveOpsList = new ArrayList<>();

    //Test: first player is almost profi
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.GREEN);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.GREEN);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.GREEN);

    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.BLUE);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.BLUE);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.BLUE);

    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.YELLOW);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.YELLOW);

    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.RED);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.RED);
    players.get(0).increaseKnowledgeLevelsByCategory(QuestionCategories.RED);

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
  public void chooseQuestionFromCategory(QuestionCategories qCat) {
    this.stateMachine.setState(State.S.AnswerQState);
    currentQuestion = QuestionsService.getInstance().getQuestionRepository().getRandomQuestion(qCat);
  }

  @Override
  public void answerQuestion(int answer) {
    System.out.println("Answering question by answer" + answer);
    if (answer == MOCK_CORRECT_ANSWER) {
      this.lastQuestionAnswer = true;

      setFigureNewPosition(this.moveOpsList.get(0).figureAttacker, this.moveOpsList.get(0).destinationField, true);

      if (isSelfAnswerQuestion) {
        if (!this.currentPlayer.increaseKnowledgeLevelsByCategory(currentQuestion.getCategory())) {

          this.winner = currentPlayer;
          this.stateMachine.setState(State.S.EndGameState);
          return;
        }

        this.stateMachine.setState(State.S.InitialState);
        prepareForNextRound();
      } else {
        if (!getPlayerById(this.moveOpsList.get(0).figureDefender.getPlayerId())
            .increaseKnowledgeLevelsByCategory(currentQuestion.getCategory())) {

          this.winner = getPlayerById(this.moveOpsList.get(0).figureDefender.getPlayerId());
          this.stateMachine.setState(State.S.EndGameState);
          return;
        }

        applyCascadeResolution(this.moveOpsList.get(0).figureDefender,
            getPlayerById(this.moveOpsList.get(0).figureDefender.getPlayerId()).getStartField());
      }
    } else {
      this.lastQuestionAnswer = false;

      if (isSelfAnswerQuestion) {

        currentPlayer.decreaseKnowledgeLevelsByCategory(currentQuestion.getCategory());
        setFigureNewPosition(this.moveOpsList.get(0).figureAttacker, HOME_POSITION, true);

        this.stateMachine.setState(State.S.InitialState);
        prepareForNextRound();
      } else {

        getPlayerById(this.moveOpsList.get(0).figureDefender.getPlayerId())
            .decreaseKnowledgeLevelsByCategory(currentQuestion.getCategory());
        setFigureNewPosition(this.moveOpsList.get(0).figureDefender, HOME_POSITION, true);
        this.stateMachine.setState(State.S.IsSelfAnswer);
      }
    }
  }

  @Override
  public void selfAnswer(boolean isSelfAnswer) {
    if (isSelfAnswer) {

      isSelfAnswerQuestion = true;
      this.stateMachine.setState(State.S.AnswerQState);
    } else {

      isSelfAnswerQuestion = false;
      setFigureNewPosition(this.moveOpsList.get(0).figureAttacker, this.moveOpsList.get(0).destinationField, true);

      this.stateMachine.setState(State.S.InitialState);
      prepareForNextRound();
    }
  }

  @Override
  public void endGame() {
    //TODO: Switch to next use case (Statistics) in next Sprint
    System.out.println("Game ended.");
    System.exit(0);
  }

  @Override
  public Player getWinner(){
    return this.winner;
  }

  @Override
  public int getRoundId() {
    return this.roundId;
  }

  @Override
  public Player getCurrentPlayer() {
    return this.currentPlayer;
  }

  @Override
  public int getDiceNumber() {
    return diceNumber;
  }

  @Override
  public List<Player> allPlayers() {
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

  @Override
  public Question getCurrentQuestion() {
    return currentQuestion;
  }

  @Override
  public boolean isQuestionAnsweredCorrectly() {
    return lastQuestionAnswer;
  }

  private void initPlayingBoard() {
    int currentStartMark = 0;
    for (Player pl : players) {
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
    for (Figure f : currentPlayer.getFigures()) {
      if (f.getPosition() != HOME_POSITION)
        return false;
    }
    return true;
  }

  private void prepareForNextRound() {
    isSelfAnswerQuestion = false;
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
      for (Figure f1 : currentPlayer.getFigures()) {
        if (f1.getPosition() == HOME_POSITION) {
          hasAtHome = true;
          destPosition = currentPlayer.getStartField();
        } else {
          destPosition = (f1.getPosition() + diceNumber) % BOARD_SIZE;
        }

        Figure f2 = figureByField.get(destPosition);
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
      for (Figure f1 : currentPlayer.getFigures()) {
        if (f1.getPosition() == HOME_POSITION)
          continue;

        destPosition = (f1.getPosition() + diceNumber) % BOARD_SIZE;
        Figure f2 = figureByField.get(destPosition);
        this.moveOpsList.add(new MoveOps(f1, f2, destPosition));
      }
    }
  }

  private void setJailBreakMoveOps() {
    for (Figure f1 : currentPlayer.getFigures()) {
      Figure f2 = figureByField.get(currentPlayer.getStartField());
      this.moveOpsList.add(new MoveOps(f1, f2, currentPlayer.getStartField()));
    }
  }

  private void resolveFieldConflicts(Figure attackerFig, Figure defenderFig, int destField) {
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

  private void applyCascadeResolution(Figure fig, int destField) {
    Figure targetFig = figureByField.get(destField);
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

  private Player getPlayerById(int playerId) {
    for (Player pl : players) {
      if (pl.getId() == playerId)
        return pl;
    }
    return null;
  }

  private void setFigureNewPosition(Figure figure, int position, boolean withRemove) {
    if (figure == null)
      return;

    System.out.format("Replaced figure#%d of player#%d from %d to %d\n", figure.getId(), figure.getPlayerId(),
        figure.getPosition(), position);

    if (withRemove)
      figureByField.remove(figure.getPosition());

    getPlayerById(figure.getPlayerId()).getFigures()[figure.getId()].setPosition(position);

    Figure oldFig = figureByField.get(position);
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
  public Figure getFigureByField(int pos) {
    return this.figureByField.get(pos);
  }

}
