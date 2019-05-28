package application.makemove.impl.questions;
public class KnowledgeLevel{

  public static final int MAX_LVL = 3;

	private int lvl;
	private final QuestionCategories qCat;

	public KnowledgeLevel(QuestionCategories qCat){
		this.lvl = 0;
		this.qCat = qCat;
	}

	public QuestionCategories getQuestionCategory(){
		return qCat;
	}

	public int getLvl(){
		return lvl;
	}

	public boolean increaseLevel(){
		if(lvl == MAX_LVL)
			return false;

		++lvl;
		return true;
	}

	public boolean decreaseLevel(){
		if(lvl == 0)
			return false;

		--lvl;
		return true;
	}


	public enum QuestionCategories {
		RED("Red"), BLUE("Blue"), YELLOW("Yellow"), GREEN("Green");

		private final String categoryName;

		private QuestionCategories(String name) {
			categoryName = name;
		}

		public String getCategoryName() {
			return this.categoryName;
		}
	}


}
