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
import com.example.utils.Console.DONE;
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
    private Set<Button> buttons = new HashSet<>();
    private VBox leftMenu = new VBox(10);
    private TabPane tabPane = new TabPane();
    private Set<SpeakView> speakViews = new HashSet<>();

    public MenuView(User user) {
        this.user = user;
        new Thread(server()).start();
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

                button.setOnMouseClicked((e) -> {
                    if ((e.getClickCount() == 2)) {
                        try {
                            DONE.log("double click button " + button.hashCode());
                            SpeakView speakView = new SpeakView(user);
                            this.speakViews.add(speakView);
                            this.tabPane.getTabs().add(speakView.getTab());
                        } catch (Exception ee) {
                            FAIL.log(ee.getMessage());
                            FAIL.log("AddUser1");
                        }
                    }
                });

                buttons.add(button);

                try {
                    this.leftMenu.getChildren().addAll(buttons);
                    // System.out.println("[MenuView.java]: new button added");
                } catch (Exception e) {
                    // HINT.log(e.toString());
                    WARN.log("Expected behavior (" + e.getMessage() + ")");
                }
            }
        };

        this.SET_DAEMON_THREAD(runnable);
    }

    private void SET_DAEMON_THREAD(Runnable runnable) {
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
                FAIL.log("Server");
            }
        };
    }

    /* Session keep opened, but may close */
    public Runnable session(Socket socket) {

        System.out.println("Session started from " + socket.getInetAddress());

        return () -> {
            try {
                while (true) {
                    
                    byte[] buffer = new byte[1024];
                    String incoming = new String(buffer, 0, socket.getInputStream().read(buffer));
                    Message message = JsonMessage.deserializeMessage(incoming);
                    System.out.println("Received: " + message);

                    for (SpeakView speakView : this.speakViews) {
                        System.out.println(speakView.getUser());
                        System.out.println(speakView);
                        System.out.println(this.speakViews);
                        if (
                            speakView.getUser().getUsername().equals(message.getSender()) 
                            /* speakView.getUser().getInetAddress().equals(message.get) Add option to get Inet Here */
                        ) {
                            try {
                                // speakView.getTextArea().appendText("X: " + message.getText());
                                String previousMessages = speakView.getTextArea().getText(); 
                                speakView.getTextArea().setText(previousMessages + "\n" + "Other: " + message.getText() + "\n");

                                System.out.println("NULL? " + speakView.getTextArea().getLength());
                                System.out.println(speakView.getUser().getUsername().equals(message.getSender()));
                            } catch (Exception e) {
                                e.getMessage();
                                FAIL.log("on inserting more text");
                            }
                        }
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

        private User        user;
        private Tab         tab;
        private BorderPane  borderPane;
        private GridPane    gridPane;
        private TextArea    messageArea;
        private ScrollPane  scrollPane;
        private TextField   inputBox;
        private Button      sendButton;

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
            this.messageArea.setMaxHeight(Region.USE_COMPUTED_SIZE);
            this.messageArea.setLayoutX(1000);
            this.messageArea.setStyle("-fx-font-size: 16;"); // Set font size to 16

            this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            this.scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

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
            this.instantiateElements(); // elements instance
            this.configuringElements(); // elements settings
            this.configuringPanel(); // panel settings
            this.configuringTab(this.user);
            this.configuringButtonAction();
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

            // this.textArea.appendText("You: " + text + "\n");

            String current = this.messageArea.getText(); 

            // When we set without the previous here, it stopts overflowing
            this.messageArea.setText(current + "You: " + text);
            // this.messageArea.setText("You: " + text);

            Socket socket = new Socket(user.getInetAddress(), 8085);
            try {
                Message message = new Message(text, this.user.getUsername());
                String serialized = JsonMessage.serializeMessage(message);
                System.out.println("Sending: " + serialized);
                byte[] bytes = serialized.getBytes(StandardCharsets.UTF_8);
                socket.getOutputStream().write(bytes, 0, bytes.length);
            } catch (Exception e) {
                FAIL.log(e.getMessage());
                FAIL.log("sendToSocket");
            } finally {
                inputBox.clear();
                // socket.close();
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