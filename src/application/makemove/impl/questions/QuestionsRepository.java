package application.makemove.impl.questions;

import application.makemove.impl.questions.KnowledgeLevel.QuestionCategories;
import java.util.ArrayList;
import java.util.List;

public class QuestionsRepository {

	private List<Question> redRepository;
	private List<Question> blueRepository;
	private List<Question> yellowRepository;
	private List<Question> greenRepository;

	public QuestionsRepository() {
		this.redRepository = new ArrayList<>();
		this.blueRepository = new ArrayList<>();
		this.yellowRepository = new ArrayList<>();
		this.greenRepository = new ArrayList<>();
	}

	public boolean addQuestionInRepo(Question question, QuestionCategories qCategory) {
		return resolveByCategory(qCategory).add(question);
	}

	public Question getRandomQuestion(QuestionCategories qCategory) {
		// Mock question
		return new Question(qCategory, "1+1?", new String[] { "2", "3" });
	}

	private List<Question> resolveByCategory(QuestionCategories qCategory) {
		switch (qCategory) {
		case RED:
			return this.redRepository;
		case BLUE:
			return this.blueRepository;
		case YELLOW:
			return this.yellowRepository;
		case GREEN:
			return this.greenRepository;
		}
		return null;
	}
}
