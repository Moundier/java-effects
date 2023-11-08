package com.example.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.example.component.StatusComp;
import com.example.model.Message;
import com.example.model.User;
import com.example.utils.Defer;
import com.example.utils.JsonMessage;
import com.example.utils.Console.PATH;
import com.example.utils.Console.WARN;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class MenuView extends Application {

  private User user;
  private VBox leftMenu = new VBox(10);
  private TabPane tabPane = new TabPane();
  private Set<Button> buttons = new HashSet<>();
  private Set<SpeakView> speakViews = new HashSet<>();
  private Thread thread = new Thread(server());
  private StatusComp statusComp;

  public MenuView(User user) {
    this.user = user;
    this.thread.start();
    this.statusComp = new StatusComp(user);
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Tabbed Menu Example");

    BorderPane borderPane = new BorderPane();
    Scene scene = new Scene(borderPane, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();

    VBox leftMenu = initSideMenu(); // Create a VBox for the left-side menu
    leftMenu.setPrefWidth(200); // Set the preferred width (e.g., 200 pixels)

    // VBox and TabPane positions
    borderPane.setLeft(leftMenu);
    borderPane.setCenter(tabPane);

    primaryStage.setOnCloseRequest((event) -> {
      Runnable runnable = () -> this.thread.interrupt();
      Defer.platform(runnable);
    });
  }

  private VBox initSideMenu() {
    leftMenu.setPadding(new Insets(10));

    leftMenu.setStyle(
      "-fx-background-color: lightgray; " +
      "-fx-padding: 10; " +
      "-fx-border-style: solid; " +
      "-fx-border-width: 1; " +
      "-fx-border-insets: 5; " +
      "-fx-border-radius: 5; " +
      "-fx-border-color: lightblue;"
    );

    Label titleLabel = new Label("Welcome " + this.user.getUsername());
    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;"); // Set font size to 16

    leftMenu.getChildren().add(titleLabel);

    leftMenu.getChildren().addAll(buttons);
    leftMenu.getChildren().add(this.statusComp.chooseStatus());

    return leftMenu;
  }

  public void addUsers(Set<User> users) {

    Runnable runnable = () -> {

      for (User user : users) {

        Button button = new Button(user.getUsername());

        button.setOnMouseClicked((event) -> {
          if (event.getClickCount() == 2) {
            SpeakView speak = new SpeakView(user);
            this.speakViews.add(speak);
            this.tabPane.getTabs().add(speak.getTab());
          }
        });

        try {
          this.buttons.add(button);
          this.leftMenu.getChildren().addAll(buttons);
        } catch (Exception e) {
          WARN.log("Expected behavior (" + e.getMessage() + ")");
        }
      }
    };

    Defer.platform(runnable);
  }


  public Runnable server() {

    return () -> {
      ServerSocket serverSocket;
      try {
        serverSocket = new ServerSocket(8085);
        System.out.println("Server: session start on " + serverSocket.getLocalPort());
        while (true) {
          new Thread(this.session(serverSocket.accept())).start();
        }
      } 
      catch (Exception e) {
        PATH.log("public Runnable server()");
      }
    };
  }

  public Runnable session(Socket socket) {

    System.out.println("Session started from " + socket.getInetAddress());
    
    return () -> {
      try {

        if (socket.isClosed()) {
          System.out.println("Socket is closed");
          try {
            this.stop();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        while (true) {

          byte[] buffer = new byte[2048];
          int bytesRead = socket.getInputStream().read(buffer);

          if (bytesRead <= 0) break;

          String incoming = new String(buffer, 0, bytesRead);
          Message message = JsonMessage.deserializeMessage(incoming);
          System.out.println("[Socket] Received: " + message);

          for (SpeakView speak : this.speakViews) {

            boolean self = speak.getUser().getUsername().equals(message.getSender());
            boolean localhost = speak.getUser().getInetAddress().equals(this.user.getInetAddress()); 
            if (self && localhost) {
              try {
                Thread.sleep(500); // Todo: deferring the daemong service helps
                String current = speak.getTextArea().getText();
                String text = current + "\n" + "Other: " + message.getText() + "\n";
                Defer.platform(() -> speak.getTextArea().setText(text)); // Todo: seconday thread
              } 
              catch (Exception e) {
                e.getMessage();
                PATH.log("contained in -> for (SpeakView speak : this.speakViews)");
              }
            }
          }
        }
      } 
      catch (IOException e) {
        PATH.log("contained in -> public Runnable session(Socket socket)");
      }
    };
  }

}