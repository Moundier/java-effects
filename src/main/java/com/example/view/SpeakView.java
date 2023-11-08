package com.example.view;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.example.model.Message;
import com.example.model.User;
import com.example.utils.Defer;
import com.example.utils.JsonMessage;
import com.example.utils.Console.PATH;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

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
    this.messageArea.setStyle("-fx-font-size: 16;"); // Set font size to 16
    this.messageArea.setPrefRowCount(25); // Size of textArea

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
        PATH.log("public void configuringButtonAction()");
      }
    });
  }

  public void sendToSocket(User user) throws IOException {
    String text = this.inputBox.getText();
    String current = this.messageArea.getText();

    Defer.platform(() -> this.messageArea.setText(current + "You: " + text));

    try {
      Message message = new Message(text, this.user.getUsername());
      String serialized = JsonMessage.serializeMessage(message);
      System.out.println("[Socket] Sending: " + serialized);
      byte[] send = serialized.getBytes(StandardCharsets.UTF_8);
      socket.getOutputStream().write(send, 0, send.length);
    } catch (Exception e) {
      PATH.log("public void sendToSocket(User user) throws IOException");
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
      System.out.println("[Socket] Close: connection interrupted!");
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