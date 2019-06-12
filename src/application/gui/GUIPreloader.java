package application.gui;

import application.gui.controller.AlertBox;
import application.makemove.MakeMoveFactory;
import application.makemove.port.MakeMoveManagement;
import application.statemachine.port.State;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIPreloader extends Preloader {
private Stage preloaderStage;
private State currentState;
private MakeMoveManagement model =  MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(null);

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("views/throwDiceScene.fxml"));
     // Parent root = FXMLLoader.load(getClass().getResource("views/chooseFigureScene.fxml"));
      //Parent root = FXMLLoader.load(getClass().getResource("views/chooseCategoryScene.fxml"));
      //Parent root = FXMLLoader.load(getClass().getResource("views/chooseAnswerScene.fxml"));
        preloaderStage.setTitle("SWE Spiel v1.0 beta");
        preloaderStage.setScene(new Scene(root, 485, 385));
        preloaderStage.setResizable(false);
        preloaderStage.show();
        AlertBox.display("Spielstart","Neues Spiel wird geladen");
        AlertBox.display("Spielstart",String.format("Zug von Spieler: %s", this.model.getAktuellerSpieler().getSpielerName()));

   }
}
