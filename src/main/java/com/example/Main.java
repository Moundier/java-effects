package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);

    new Menu();
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Login Page");

    // Create a GridPane for layout
    GridPane grid = new GridPane();
    grid.setAlignment(javafx.geometry.Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(50, 50, 50, 50)); // Increase padding

    // Increase font size for labels, inputs, and buttons
    Font labelFont = new Font(16);
    Font fieldFont = new Font(16);
    Font buttonFont = new Font(16);

    // Create UI elements
    Label usernameLabel = new Label("Username:");
    usernameLabel.setFont(labelFont); // Set font size
    TextField usernameField = new TextField();
    usernameField.setFont(fieldFont); // Set font size

    Button openMenuBtn = new Button("Login");
    openMenuBtn.setFont(buttonFont); // Set font size
    Button closeButton = new Button("Close");
    closeButton.setFont(buttonFont); // Set font size

    // Add elements to the GridPane
    grid.add(usernameLabel, 0, 0);
    grid.add(usernameField, 1, 0);
    grid.add(openMenuBtn, 1, 2);
    grid.add(closeButton, 0, 2);

    // Create an event handler for the login button
    openMenuBtn.setOnAction((e) -> {
      String username = usernameField.getText();

      boolean usernameNotNull = (username != null);
      boolean usernameNotEmpty = (!username.isEmpty());
      boolean usernameIsValid = (username.length() >= 5);

      int errorCode = 0; // Initialize with a default error code

      if (!usernameNotNull) 
        errorCode = 1; // Error code for username is null
      else if (!usernameNotEmpty) 
        errorCode = 2; // Error code for username is empty
      else if (!usernameIsValid)
        errorCode = 3; // Error code for username length < 5

      // Switch statement to handle different error cases
      switch (errorCode) {
        case 0:
          new Menu().start(new Stage()); // No errors, navigate to the main application
          primaryStage.close();
          break;
        case 1:
          showErrorMessage("Login Failed", "Username is null.");
          break;
        case 2:
          showErrorMessage("Login Failed", "Username is empty.");
          break;
        case 3:
          showErrorMessage("Login Failed", "Username length should be at least 5 characters.");
          break;
        default:
          // Handle any other unexpected cases
          break;
      }
    });

    closeButton.setOnAction(e -> primaryStage.close());

    // Create the scene and set it on the stage
    Scene scene = new Scene(grid, 400, 250); // Increase the scene size
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void showErrorMessage(String title, String message) {
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
