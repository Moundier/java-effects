package com.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.example.model.Message;
import com.example.model.User;
import com.example.model.User.Status;
import com.example.utils.JsonMessage;
import com.example.utils.Console.FAIL;
import com.example.utils.Console.WARN;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class MenuView extends Application {

  private User user;
  private VBox leftMenu = new VBox(10);
  private TabPane tabPane = new TabPane();
  private Set<Button> buttons = new HashSet<>();
  private Set<SpeakView> speakViews = new HashSet<>();
  private Thread thread = new Thread(server());

  public MenuView(User user) {
    this.user = user;
    this.thread.start();
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
      platform(runnable);
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
            "-fx-border-color: lightblue;");

    Label titleLabel = new Label("Welcome " + this.user.getUsername());
    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;"); // Set font size to 16

    leftMenu.getChildren().add(titleLabel);

    leftMenu.getChildren().addAll(buttons);
    leftMenu.getChildren().add(chooseStatus());

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

    this.platform(runnable);
  }

  private void platform(Runnable runnable) {
    // Make a runnable and use here
    Platform.runLater(runnable); 
  }

  private ComboBox<String> chooseStatus() {

    ComboBox<String> selectionBox = new ComboBox<>();
    selectionBox.setPromptText("ONLINE");
    selectionBox.getItems().addAll("ONLINE", "BE_BACK_SOON", "DO_NOT_DISTURB");
    selectionBox.setStyle("-fx-font-size: 14px; -fx-background-color: #f0f0f0; -fx-text-fill: #333333;");

    selectionBox.setOnAction(event -> {
      String option = selectionBox.getValue();
      changeStatus(option);
    });

    return selectionBox;
  }

  private void changeStatus(String option) {
    switch (option) {
      case "ONLINE":
        this.user.setStatus(Status.ONLINE);
        break;
      case "BE_BACK_SOON":
        this.user.setStatus(Status.BE_BACK_SOON);
        break;
      case "DO_NOT_DISTURB":
        this.user.setStatus(Status.DO_NOT_DISTURB);
        break;
      default:
        break;
    }
  }

  // Server never closes
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
        FAIL.log(e.getMessage());
        FAIL.log("Server");
      }
    };
  }

  // Session is temporary
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

          assert (bytesRead <= 0);

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
                this.platform(() -> speak.getTextArea().setText(text)); // Todo: seconday thread
              } 
              catch (Exception e) {
                e.getMessage();
                FAIL.log("on inserting more text");
              }
            }

            // TODO: ELSE, because, not only host should change messages
          }
        }
      } 
      catch (IOException e) {
        FAIL.log(e.getMessage());
        FAIL.log("Session");
      }
    };
  }

  public class SpeakView {

    private User user;
    private Socket socket;
    private Tab tab;
    private BorderPane borderPane;
    private GridPane gridPane;
    private TextArea messageArea;
    private ScrollPane scrollPane;
    private TextField inputBox;
    private Button sendButton;

    public void instantiateElements() {
      this.messageArea = new TextArea();
      this.scrollPane = new ScrollPane(this.messageArea);
      this.inputBox = new TextField();
      this.sendButton = new Button();
      this.gridPane = new GridPane();
      this.borderPane = new BorderPane(this.gridPane);
    }

    public void configuringPanel() {
      this.gridPane.setHgap(10);
      this.gridPane.setVgap(10);
      this.gridPane.setPadding(new Insets(10));

      this.gridPane.add(scrollPane, 0, 0, 1, 1);
      this.gridPane.add(inputBox, 0, 1);
      this.gridPane.add(sendButton, 1, 1);
    }

    public void configuringElements() {

      this.messageArea.setEditable(false);
      this.messageArea.setWrapText(true);
      // this.messageArea.setLayoutX(1000);
      this.messageArea.setStyle("-fx-font-size: 16;"); // Set font size to 16
      this.messageArea.setPrefRowCount(50); 

      this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
      this.scrollPane.isFitToWidth();

      this.inputBox.setPromptText("Type your message...");
      this.inputBox.setStyle("-fx-font-size: 16;"); // Set font size to 16

      this.sendButton.setText("Send");
    }

    public void configuringTab(User user) {
      this.tab = new Tab(user.getUsername());
      this.tab.setContent(this.borderPane);
    }

    public SpeakView(User user) {
      this.user = user;
      this.openSocket(socket);
      this.instantiateElements(); // elements instance
      this.configuringElements(); // elements settings
      this.configuringPanel(); // panel settings
      this.configuringTab(this.user);
      this.configuringButtonAction();
      this.configuringTabOnClose();
    }

    public void configuringTabOnClose() {
      this.tab.setOnClosed((e) -> {
        String message = "Connection with" +  this.user + " got closed.";
        ErrorView.showErrorMessage("Status", message);
        this.closeSocket(this.socket);
      });
    }

    public void configuringButtonAction() {
      this.sendButton.setOnAction((event) -> {
        try {
          this.sendToSocket(this.user);
        } catch (Exception e) {
          FAIL.log(e.getMessage());
          FAIL.log("configuringButtonAction");
        }
      });
    }

    public void sendToSocket(User user) throws IOException {
      String text = this.inputBox.getText();
      String current = this.messageArea.getText();

      // TODO: thread specific update of JavaFX
      platform(() -> this.messageArea.setText(current + "You: " + text));

      try {
        Message message = new Message(text, this.user.getUsername());
        String serialized = JsonMessage.serializeMessage(message);
        System.out.println("[Socket] Sending: " + serialized);
        byte[] send = serialized.getBytes(StandardCharsets.UTF_8);
        socket.getOutputStream().write(send, 0, send.length);
      } catch (Exception e) {
        FAIL.log(e.getMessage());
        FAIL.log("sendToSocket");
      } finally {
        inputBox.clear();
        // this.closeSocket(socket);
      }
    }

    public void openSocket(Socket socket) {
      try {
        this.socket = new Socket(user.getInetAddress(), 8085);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("HELLOW");
      }
    }

    public void closeSocket(Socket socket) {
      try {
        System.out.println("[Socket] Close: connection interrupet");
        socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public Tab getTab() {
      return this.tab;
    }

    public User getUser() {
      return this.user;
    }

    public TextArea getTextArea() {
      return this.messageArea;
    }
  }
}