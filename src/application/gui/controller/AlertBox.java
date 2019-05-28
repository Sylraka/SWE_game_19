package application.gui.controller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static void display(String title, String message) {

        Stage playerAlertWindow = new Stage();

        playerAlertWindow.initModality(Modality.APPLICATION_MODAL);
        playerAlertWindow.setTitle(title);
        playerAlertWindow.setMinHeight(100);
        playerAlertWindow.setMinWidth(200);
        playerAlertWindow.setResizable(false);

        Label label = new Label();
        label.setText(message);

        Button okButton = new Button("OK");
        okButton.setOnAction(event -> playerAlertWindow.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        playerAlertWindow.setScene(scene);
        playerAlertWindow.showAndWait();
    }
}
