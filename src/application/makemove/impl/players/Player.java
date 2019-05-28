package application.makemove.impl.players;

import application.makemove.impl.questions.KnowledgeLevel.QuestionCategories;
import application.makemove.impl.questions.KnowledgeLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import application.makemove.impl.Figure;
import application.makemove.impl.players.Colors;

public class Player {
  private static final int NUMBER_OF_FIGURES = 3;

  private int id;
  private Colors color;
  private String name;
  private int startField;

  private Figure[] playerFigures;
  private KnowledgeLevel[] knowledgeLevels = new KnowledgeLevel[4];

  public Player(int id, Colors color) {
    this.id = id;
    this.color = color;
    this.name = color.getName();
    initializeFields();
  }

  public Player(int id, Colors color, String name) {
    this.id = id;
    this.name = name;
    this.color = color;
    initializeFields();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getPlayerName() {
    return name;
  }

  public Colors getColor() {
    return color;
  }

  public void setStartField(int startField) {
    this.startField = startField;
  }

  public int getStartField() {
    return startField;
  }

  public Figure[] getFigures() {
    return playerFigures;
  }

  public KnowledgeLevel getKnowledgeLevelsByCategory(QuestionCategories qc) {
    for (int i = 0; i < knowledgeLevels.length; i++) {
      if(knowledgeLevels[i].getQuestionCategory().equals(qc)){
        return knowledgeLevels[i];
      }
    }
    return null;
  }

  public boolean increaseKnowledgeLevelsByCategory(QuestionCategories qc) {
    for (int i = 0; i < knowledgeLevels.length; i++) {
      if(knowledgeLevels[i].getQuestionCategory().equals(qc)){
        if( knowledgeLevels[i].increaseLevel()){
          if(areAllMax())
            return false;
          else
            return true;
        }
        else
          break;
      }
    }

    for (int i = 0; i < knowledgeLevels.length; i++) {
      if( knowledgeLevels[i].increaseLevel()){
        if(areAllMax())
          return false;
        else
          return true;
      }
    }

    return false;
  }

  private boolean areAllMax(){
    for(KnowledgeLevel kl: knowledgeLevels)
      if(kl.getLvl() != KnowledgeLevel.MAX_LVL)
        return false;

    return true;
  }

  public boolean decreaseKnowledgeLevelsByCategory(QuestionCategories qc) {
    for (int i = 0; i < knowledgeLevels.length; i++) {
      if(knowledgeLevels[i].getQuestionCategory().equals(qc)){
        if( knowledgeLevels[i].decreaseLevel())
          return true;
        else
          return false;
      }
    }
    return false;
  }

  private void initializeFields() {
    playerFigures = new Figure[NUMBER_OF_FIGURES];
    for (int i = 0; i < playerFigures.length; i++) {
      playerFigures[i] = new Figure(i, this.id);
    }

    knowledgeLevels[0] = new KnowledgeLevel(QuestionCategories.YELLOW);
    knowledgeLevels[1] = new KnowledgeLevel(QuestionCategories.RED);
    knowledgeLevels[2] = new KnowledgeLevel(QuestionCategories.GREEN);
    knowledgeLevels[3] = new KnowledgeLevel(QuestionCategories.BLUE);
  }

}
