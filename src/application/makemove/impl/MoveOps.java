package application.makemove.impl;

public class MoveOps {
  public final Figure figureAttacker;
  public final Figure figureDefender;
  public final int destinationField;
  
	public MoveOps(Figure f1, Figure f2, int field) {
    figureAttacker = f1;
    figureDefender = f2;
    destinationField = field;
	}
}
