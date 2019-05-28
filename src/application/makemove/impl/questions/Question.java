package application.makemove.impl.questions;

import application.makemove.impl.questions.KnowledgeLevel.QuestionCategories;
public class Question {

  private final QuestionCategories qCategory;
  private final String questionBody;
  private final String[] answers;

  public Question(QuestionCategories qCategory, String questionBody, String[] answers){
    this.qCategory = qCategory;
    this.questionBody = questionBody;
    this.answers = answers;
  }

  public String getQuestionBody(){
    return questionBody;
  }

  public String[] getAnswers(){
    return answers;
  }

  public QuestionCategories getCategory(){
    return qCategory;
  }
}
