package com.example.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ErrorModal {
    
    public static void showErrorMessage(String title, String message) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle(title);
    
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
    
        Label label = new Label(message);
        label.setWrapText(true); // Allow text wrapping
    
        Button closeButton = new Button("Close");
        closeButton.setOnAction((e) -> {
          dialog.close();
        });
    
        grid.add(label, 0, 0);
        grid.add(closeButton, 0, 1);
    
        Scene dialogScene = new Scene(grid, 400, 200);
        dialog.setScene(dialogScene);
        dialog.setResizable(true); // Allow resizing
    
        dialog.showAndWait();
      }
}
