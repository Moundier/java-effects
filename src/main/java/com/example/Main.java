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

    Label passwordLabel = new Label("Password:");
    passwordLabel.setFont(labelFont); // Set font size
    PasswordField passwordField = new PasswordField();
    passwordField.setFont(fieldFont); // Set font size

    Button loginButton = new Button("Login");
    loginButton.setFont(buttonFont); // Set font size
    Button closeButton = new Button("Close");
    closeButton.setFont(buttonFont); // Set font size

    // Add elements to the GridPane
    grid.add(usernameLabel, 0, 0);
    grid.add(usernameField, 1, 0);
    grid.add(passwordLabel, 0, 1);
    grid.add(passwordField, 1, 1);
    grid.add(loginButton, 1, 2);
    grid.add(closeButton, 0, 2);

    // Create an event handler for the login button
    loginButton.setOnAction(e -> {
      String username = usernameField.getText();
      String password = passwordField.getText();

      // Add your login validation logic here
      if (isValidLogin(username, password)) {
        showResizableDialog("Login Successful", "Welcome, " + username + "!");
        // You can navigate to the main application here
      } else {
        showResizableDialog("Login Failed", "Invalid username or password.");
      }
    });

    closeButton.setOnAction(e -> primaryStage.close());

    // Create the scene and set it on the stage
    Scene scene = new Scene(grid, 400, 250); // Increase the scene size
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private boolean isValidLogin(String username, String password) {
    // Implement your own logic to validate the login credentials here
    // For a demo, let's assume a simple hardcoded username and password
    return username.equals("user") && password.equals("password");
  }

  private void showResizableDialog(String title, String message) {
    Stage dialog = new Stage(StageStyle.UTILITY);
    dialog.setTitle(title);

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(20, 20, 20, 20));

    Label label = new Label(message);
    label.setWrapText(true); // Allow text wrapping

    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> dialog.close());

    grid.add(label, 0, 0);
    grid.add(closeButton, 0, 1);

    Scene dialogScene = new Scene(grid, 400, 200);
    dialog.setScene(dialogScene);
    dialog.setResizable(true); // Allow resizing

    dialog.showAndWait();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
