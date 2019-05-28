package application.makemove.impl;

public class Figure{

  private final int id;
  private final int playerId;
  private int position;

  public Figure(int id, int playerId){
    this.id = id;
    this.playerId = playerId;
    this.position = -1;//home
  }

  public int getId(){
    return id;
  }

  public int getPlayerId(){
    return playerId;
  }

  public void setPosition(int pos){
    position = pos;
  }

  public int getPosition(){
    return position;
  }

}
