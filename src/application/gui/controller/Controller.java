package application.gui.controller;

import application.logic.LogicFactory;
import application.logic.LogicFactoryImpl;
import application.logic.port.MVCPort;
import application.makemove.MakeMoveFactory;
import application.makemove.impl.players.Figur;
import application.makemove.impl.players.Spieler;
import application.makemove.impl.questions.KnowledgeLevel;
import application.makemove.port.MakeMoveManagement;
import application.statemachine.port.Observer;
import application.statemachine.port.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable, Observer {

    private MakeMoveManagement model =  MakeMoveFactory.FACTORY.simpleManagerPort().makeMoveManagement(null);
    private State currentState = State.S.InitialState;
    MVCPort mvcPort = LogicFactory.FACTORY.MVCPort();

    static boolean newGame = true;
    static String kek = "";

    @FXML private Text textOutput;
    @FXML private Text playerColor;
    @FXML private Text wsaRed;
    @FXML private Text wsaBlue;
    @FXML private Text wsaGreen;
    @FXML private Text wsaYellow;
    @FXML private Label playersFigures;
    @FXML private RadioButton radioFigure1;
    @FXML private RadioButton radioFigure2;
    @FXML private RadioButton radioFigure3;
    @FXML private Text textOutput2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mvcPort.subject().attach(this); // Надо вот эту хуйню как-то вынести  отсюда

        if (newGame == true) {

            loadOnScreenInfos();
            newGame = false;
            this.model.startNewRound();
        }

        if (currentState == State.S.InitialState) {
            System.out.println(currentState);
            AlertBox.display("Spielstart",String.format("Zug von Spieler: %s", this.model.getCurrentPlayer().getSpielerName()));
            loadOnScreenInfos();
            this.model.startNewRound(); //need a better place
        }

        if (currentState == State.S.WahlState){
            textOutput.setText(kek);
        }
    }

    @FXML
    private void throwDice(ActionEvent event) {
            System.out.println(currentState);
            this.model.throwDice();
            textOutput.setText(String.format("Gewürfelt: %d", this.model.getDiceNumber()));
            kek = String.format("Gewürfelt: %d", this.model.getDiceNumber());

            if (currentState == State.S.WahlState) {
                loadChooseFiguresScene(event);
            }
            if (currentState == State.S.InitialState) {
                loadThrowDiceScene(event);
            }

        }

    @FXML
    private void chooseFigure(ActionEvent event) {
        if (radioFigure1.isSelected()) this.model.chooseMove(0);
        if (radioFigure2.isSelected()) this.model.chooseMove(1);
        if (radioFigure3.isSelected()) this.model.chooseMove(2);

        textOutput.setText(kek);

    }

    @Override
    public void update(State newState) {
        if (newState == null)
            return;

        if (currentState == State.S.WurfState) {

            System.out.println("Gewürfelt: " + this.model.getDiceNumber());
            System.out.println(currentState);
        }

//        if (currentState == State.S.ChooseMoveState) {
//
//        }

        currentState = newState;
    }

    private void loadOnScreenInfos() {
        playerColor.setText(this.model.getCurrentPlayer().getSpielerName());
        loadPlayerWsaInfo();
        loadPlayersFiguresInfo();
    }

    private void loadChooseFiguresScene(ActionEvent event) {
        System.out.println(currentState);
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../views/chooseFigureScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 485, 385);
        stage.setScene(scene);
        stage.show();
    }

    private void loadThrowDiceScene(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../views/throwDiceScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 485, 385);
        stage.setScene(scene);
        stage.show();
    }

    private void loadPlayerWsaInfo() {
        Spieler player = this.model.getCurrentPlayer();
        wsaRed.setText(player.getKnowledgeLevelsByCategory(KnowledgeLevel.QuestionCategories.RED).getLvl() + " von 4");
        wsaBlue.setText(player.getKnowledgeLevelsByCategory(KnowledgeLevel.QuestionCategories.BLUE).getLvl() + " von 4");
        wsaGreen.setText(player.getKnowledgeLevelsByCategory(KnowledgeLevel.QuestionCategories.GREEN).getLvl() + " von 4");
        wsaYellow.setText(player.getKnowledgeLevelsByCategory(KnowledgeLevel.QuestionCategories.YELLOW).getLvl() + " von 4");
    }

    private void loadPlayersFiguresInfo() {
        String kek = "";
        for (Spieler player : this.model.allPlayers()) {
            for (Figur fg : player.getFiguren()) {
                kek += String.format("F#%d: po = %d\t", fg.getFigurNummer(), fg.getPosition());
            }
            kek += "\n";

        }
        playersFigures.setText(kek);
    }

}


