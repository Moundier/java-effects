package com.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.example.model.User;
import com.example.utils.Console.HINT;
import com.example.utils.Console.INFO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuView extends Application {

    private User user;
    Set<Button> buttons = new HashSet<>();
    VBox leftMenu = new VBox(10);

    public MenuView(User user) {
        this.user = user;
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

        TabPane tabPane = initTabsMenu(); // Create a TabPane for the top tabbed menu

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
        "-fx-border-color: lightblue;"
        );

        Label titleLabel = new Label("Welcome " + this.user.getUsername());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;"); // Set font size to 16

        leftMenu.getChildren().add(titleLabel);

        // TODO: remove testing buttons
        List<Button> TEST_BUTTONS = List.of(
                new Button("Menu Item 1"),
                new Button("Menu Item 2"),
                new Button("Menu Item 3"));

        // TODO: remove testing buttons
        for (Button button : TEST_BUTTONS) {
            button.setOnMouseClicked((e) -> {
                int index = TEST_BUTTONS.indexOf(button);
                boolean twiceClick = e.getClickCount() == 2;
                if (twiceClick)
                    System.out.println("Double Click Happened in button " + index);
            });
        }

        leftMenu.getChildren().addAll(buttons);
        leftMenu.getChildren().addAll(TEST_BUTTONS);
        leftMenu.getChildren().add(createStatusSelector());

        return leftMenu;
    }

    private TabPane initTabsMenu() {
        TabPane tabPane = new TabPane();

        // TODO: testing tabs
        List<Tab> tabList = List.of(
                new Tab("Tab 1"),
                new Tab("Tab 2"),
                new Tab("Tab 3"));

        // TODO: testing tabs
        for (Tab tab : tabList) {

            int index = tabList.indexOf(tab) + 1;
            tab.setContent(conversationOpens("Tab " + index));
            tab.setStyle("-fx-font-size: 16;");
            tabPane.getTabs().add(tab);
        }

        return tabPane;
    }

    // When conversation opens, we open the chat

    private BorderPane conversationOpens(String tabTitle) {

        BorderPane tabContent = new BorderPane();

        // Text area Config
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-size: 16;"); // Set font size to 16

        ScrollPane journal = new ScrollPane(textArea);
        journal.setHbarPolicy(ScrollBarPolicy.NEVER);

        // Input Config
        TextField chatInput = new TextField();
        chatInput.setPromptText("Type your message...");
        chatInput.setStyle("-fx-font-size: 16;"); // Set font size to 16

        // Button Config
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-font-size: 16;"); // Set font size to 16

        // Button Listener
        sendButton.setOnAction((e) -> {
            String message = chatInput.getText();
            textArea.appendText("You: " + message + "\n");
            chatInput.clear();
        });

        // Positioning Pane, Input & Send
        GridPane chatGrid = new GridPane();
        chatGrid.setHgap(10);
        chatGrid.setVgap(10);
        chatGrid.setPadding(new Insets(10));

        chatGrid.add(journal, 0, 0, 1, 1);
        chatGrid.add(chatInput, 0, 1);
        chatGrid.add(sendButton, 1, 1);

        // Add the GridPane to the tab content
        tabContent.setCenter(chatGrid);

        return tabContent;
    }

    public void addUsers(Set<User> users) {

        Runnable runnable = () -> {

            for (User user : users) {

                System.out.println("USER IS ADDED");
                buttons.add(new Button(user.getUsername()));

                try {
                    this.leftMenu.getChildren().addAll(buttons);
                    System.out.println("[MenuView.java]: new button added");
                } catch (Exception e) {
                    HINT.log(e.toString());
                    HINT.log("Intentionally adding and readding duplicates to Set of buttons!");
                }
            }

            for (Button btn : this.buttons) {
                btn.setOnMouseClicked((e) -> {
                    int i = btn.hashCode();
                    if (e.getClickCount() == 2) {
                        INFO.log("double click button " + i);
                    }
                });
            }
        };

        Platform.runLater(runnable);
    }

    // Future
    public User.Status selection = User.Status.AVOID;

    private ComboBox<String> createStatusSelector() {

        ComboBox<String> selectionBox = new ComboBox<>();
        selectionBox.setPromptText("Select an option");
        selectionBox.getItems().addAll("Option 1", "Option 2", "Option 3");
        selectionBox.setStyle("-fx-font-size: 14px; -fx-background-color: #f0f0f0; -fx-text-fill: #333333;");

        selectionBox.setOnAction(event -> {
            String selectedOption = selectionBox.getValue();
            handleSelectedOption(selectedOption);
        });

        return selectionBox;
    }

    private void handleSelectedOption(String selectedOption) {
        switch (selectedOption) {
            case "Option 1":
                System.out.println("Option 1 selected"); // Add Action Here
                break;
            case "Option 2":
                System.out.println("Option 2 selected"); // Add Action Here
                break;
            case "Option 3":
                System.out.println("Option 3 selected"); // Add Action Here
                break;
            default:
                break;
        }
    }


    // TODO

    // Double-click user button handler
    private void handleUserButtonDoubleClick(Button userButton) {
        // Get the user related to this button
        // User selectedUser = getUserFromButton(userButton); Implement this func

        // Open a new tab with the conversation
        // openConversationTab(selectedUser);

        // You can also initiate a socket connection for this user here
    }

    // Open a tab with the conversation
    private void openConversationTab(User user) {
        Tab tab = new Tab(user.getUsername());
        BorderPane conversationPane = createConversationPane(user);

        tab.setContent(conversationPane);
        // tabPane.getTabs().add(tab); To implement this tabs should be global
    }

    // Create a conversation interface for a user
    private BorderPane createConversationPane(User user) {
        BorderPane conversationPane = new BorderPane();

        // Add text area, input field, and send button
        // Configure these components similar to what you did in your existing code

        return conversationPane;
    }

    // Handle tab closing (e.g., when the user closes the tab)
    private void handleTabClose(Tab tab) {
        // You can implement logic here to notify the other end about the tab closure

        // tabPane.getTabs().remove(tab); To implement this tabs should be global
    }

}

/* double click user button */
/* open tab with conversation */
/* send messages through socket */
/* if close, catch and send print message back */
/* close click, close tab and conversation */