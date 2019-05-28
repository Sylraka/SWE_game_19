package application.makemove.port;

import java.util.List;

import application.makemove.impl.Figure;
import application.makemove.impl.MoveOps;
import application.makemove.impl.players.Player;
import application.makemove.impl.questions.KnowledgeLevel.QuestionCategories;
import application.makemove.impl.questions.Question;


/**
 * @url element://model:project::SWE_Spiel/jdt:e_class:src:design.comp.MakeMoveManagement
 */

public interface MakeMoveManagement {

  void startNewRound();

  void throwDice();

  void chooseMove(int optionId);

  void chooseQuestionFromCategory(QuestionCategories qCat);

  void answerQuestion(int answer);

  void selfAnswer(boolean isSelfAnswer);

  int getRoundId();

  int getDiceNumber();

  int getTriesLeft();

  List<Player> allPlayers();

  Player getCurrentPlayer();

  List<MoveOps> getMoveOps();

  Question getCurrentQuestion();

  void endGame();

  boolean isQuestionAnsweredCorrectly();

  Player getWinner();

  //TODO: delete, for debugging
  void throwCheatDice(int cheatDice);

  //TODO: delete, for debugging
  Figure getFigureByField(int pos);

}
