package application.makemove.impl;

import application.makemove.impl.players.Figur;

public class MoveOps {
  public final Figur figureAttacker;
  public final Figur figureDefender;
  public final int destinationField;
  
	public MoveOps(Figur f1, Figur f2, int field) {
    figureAttacker = f1;
    figureDefender = f2;
    destinationField = field;
	}
}
