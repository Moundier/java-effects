package com.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.example.model.Message;
import com.example.model.User;
import com.example.model.User.Status;
import com.example.utils.JsonMessage;
import com.example.utils.Console.DONE;
import com.example.utils.Console.FAIL;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class MenuView extends Application {

    private User user;
    private Set<Button> buttons = new HashSet<>();
    private VBox leftMenu = new VBox(10);

    public MenuView(User user) {
        this.user = user;
        new Thread(server()).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // It was inside of start method, right under the TabPane

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

    private TabPane tabPane = new TabPane();

    private TabPane openConnection(User user) throws IOException {
        Tab tab = new Tab("Private with " + user.getUsername());
        BorderPane messagePanel = conversationOpens(user);
        tab.setContent(messagePanel);
        tabPane.getTabs().add(tab);
        return tabPane;
    }

    // When conversation opens, we open the chat

    private BorderPane conversationOpens(User user) throws IOException {

        BorderPane tabPanel = new BorderPane();

        // Text area Config
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-size: 16;"); // Set font size to 16

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);

        // Input Config
        TextField inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setStyle("-fx-font-size: 16;"); // Set font size to 16

        // Button Config
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-font-size: 16;"); // Set font size to 16

        // Button Listener
        sendButton.setOnAction((e) -> {
        
            String text = inputField.getText();
            textArea.appendText("You: " + text + "\n");

            try {
                Socket socket = new Socket("localhost", 8085);
                Message message = new Message(text, this.user.getUsername());
                String serialized = JsonMessage.serializeMessage(message);
                System.out.println("Sending: " + serialized);
                byte[] bytes = serialized.getBytes(StandardCharsets.UTF_8);
        
                socket.getOutputStream().write(bytes, 0, bytes.length);
            } catch (IOException ex) {
                ex.printStackTrace(); // Handle the IOException, e.g., log the error or show an alert
            } finally {
                inputField.clear();
                // socket.close();
            }
        });

        // Positioning Pane, Input & Send
        GridPane mainPanel = new GridPane();
        mainPanel.setHgap(10);
        mainPanel.setVgap(10);
        mainPanel.setPadding(new Insets(10));

        mainPanel.add(scrollPane, 0, 0, 1, 1);
        mainPanel.add(inputField, 0, 1);
        mainPanel.add(sendButton, 1, 1);

        tabPanel.setCenter(mainPanel); // Creates a tab with a chat at Center

        return tabPanel;
    }

    public void addUsers(Set<User> users) {

        Runnable runnable = () -> {

            for (User user : users) {
                
                Button button = new Button(user.getUsername());

                button.setOnMouseClicked((e) -> {
                    if ((e.getClickCount() == 2)) {
                        try {
                            DONE.log("double click button " + button.hashCode());
                            this.openConnection(user);
                        } catch (Exception conn) {
                            conn.getMessage();
                        }
                    }
                });

                buttons.add(button);

                try {
                    this.leftMenu.getChildren().addAll(buttons);
                    // System.out.println("[MenuView.java]: new button added");
                } catch (Exception e) {
                    // HINT.log(e.toString());
                    // HINT.log("Send user, add button, avoid duplicates.");
                }
            }
        };

        this.deferedThread(runnable);
    }

    public void deferedThread(Runnable runnable) {
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

    /* Server that accepts connections never closes */
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
            }
        };
    }

    /* Session keep opened, but may close */
    public Runnable session(Socket socket) {

        System.out.println("Session started from " + socket.getInetAddress());

        return () -> {
            try {
                while (true) {
                    
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];

                    String incoming = new String(buffer, 0, inputStream.read(buffer));

                    Message message = JsonMessage.deserializeMessage(incoming);
                    System.out.println("Received: " + message);
                    
                    if (!this.user.getUsername().equals(message.getSender())) {

                        // THIS SOCKET CLOSES, CATCH IN THE OTHER SOCKET

                        // this.conversationOpens(message.getSender()); // conversationOpens
                        // Add tab is done above
                        // Append message into chat
                    }
                    
                    // Append message into chat
                }
            } 
            catch (IOException e) {
                FAIL.log(e.getMessage());
            }
        };
    }

}

// Comm = Communication

// Action -> Trigger
// (double_click_on_user) -> openComm
// openComm = (openTab) + (openGrid) + (openSocket)
// (send_message) -> socket.send()
// closeComm = (closeTab) + (closeGrid) + (closeSocket)

/* double click user button */
/* open tab with conversation */
/* send messages through socket */
/* if close, catch and send print message back */
/* close click, close tab and conversation */