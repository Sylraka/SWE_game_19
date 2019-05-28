package application.makemove.impl.questions;
public class QuestionsService {

	private static QuestionsService qss;

	private static QuestionsRepository qr;

	private QuestionsService() {
	}

	public static QuestionsService getInstance() {
		if (qss == null) {
			qss = new QuestionsService();
			qr = new QuestionsRepository();
		}
		return qss;
	}

	public QuestionsRepository getQuestionRepository() {
		return qr;
	}
}
