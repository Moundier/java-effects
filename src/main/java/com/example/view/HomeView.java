package com.example.view;

import com.example.model.User;
import com.example.model.User.Status;
import com.example.service.Broadcaster;
import com.example.utils.Host;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static javafx.geometry.Pos.*;

import java.util.List;

public class HomeView extends Application {

  private MenuView menuView;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Login Page");

    // Create a GridPane for layout
    GridPane grid = new GridPane();
    grid.setAlignment(CENTER);
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

      // All are true by default
      List<Boolean> conditions = List.of(
        (username != null),
        (!username.isEmpty()),
        (username.length() >= 5)
      );

      int errorCode = 0; // Initialize with a default error code

      // Check for Errors
      for (int i = 0; i < conditions.size(); i++) {
        if (!conditions.get(i)) {
          errorCode = i + 1;
          break;         
        }
      }

      // Switch statement to handle different error cases
      switch (errorCode) {
        case 0:
          primaryStage.close();
          routeToMenu(username); // Closes Primary Stage and Set User
          break;
        case 1:
          ErrorView.showErrorMessage("Login Failed", "Username is null.");
          break;
        case 2:
          ErrorView.showErrorMessage("Login Failed", "Username is empty.");
          break;
        case 3:
          ErrorView.showErrorMessage("Login Failed", "Username length should be at least 5 characters.");
          break;
        default:
          break; // Handle any other unexpected cases
      }
    });

    closeButton.setOnAction((e) -> primaryStage.close());

    // Create the scene and set it on the stage
    Scene scene = new Scene(grid, 400, 250); // Increase the scene size
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void routeToMenu(String username) {
    User user = User.builder()
        .username(username)
        .status(Status.ONLINE)
        .timestamp(System.currentTimeMillis())
        .inetAddress(Host.fetchLocalIP())
        .build();

    // Start all of them
    this.menuView = new MenuView(user);
    this.menuView.start(new Stage());
    new Broadcaster(user, menuView);
  }

}