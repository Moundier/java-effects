package com.example.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.example.model.User;
import com.example.service.Broadcaster;

import java.util.List;

/*
 * @author Casanova
 * @date 2023-11-01
 * @time 15:29
 */

public class MenuView extends Application {

    private User user;
    private Broadcaster broadcaster; // Keep for future
    
    public MenuView(User user) {
        this.user = user;
        this.broadcaster = new Broadcaster(this.user);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tabbed Menu Example");

        VBox leftMenu = initSideMenu(); // Create a VBox for the left-side menu
        leftMenu.setPrefWidth(200); // Set the preferred width (e.g., 200 pixels)

        TabPane tabPane = initTabsMenu(); // Create a TabPane for the top tabbed menu

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(leftMenu);
        borderPane.setCenter(tabPane);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox initSideMenu() {
        VBox leftMenu = new VBox(10);
        leftMenu.setPadding(new Insets(10));

        leftMenu.setStyle(
            "-fx-background-color: lightgray; " +
            "-fx-padding: 10; " +
            "-fx-border-style: solid inside; " +
            "-fx-border-width: 1; " +
            "-fx-border-insets: 5; " +
            "-fx-border-radius: 5;"
        );

        Label titleLabel = new Label("Welcome " + this.user.getUsername());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;"); // Set font size to 16

        leftMenu.getChildren().add(titleLabel);

        // List<Button> buttons = new ArrayList<>();
        
        // TODO: Add buttons based on User
        // for (User user : this.broadcaster.getUsersOnline()) {
        //     buttons.add(new Button(user.getUsername()));
        // }
        
        // TODO: remove testing buttons
        List<Button>  TEST_BUTTONS = List.of(
            new Button("Menu Item 1"),
            new Button("Menu Item 2"),
            new Button("Menu Item 3")
        );

        // TODO: remove testing buttons
        for (Button button : TEST_BUTTONS) {
            button.setOnMouseClicked((e) -> {
                int index = TEST_BUTTONS.indexOf(button);
                boolean twiceClick = e.getClickCount() == 2;
                if (twiceClick) System.out.println("Double Click Happened in button " + index);
            });
        }

        // for (Button button : buttons) {
        //     button.setStyle("-fx-font-size: 16;"); // Set font size to 16 for buttons
        // }

        // leftMenu.getChildren().addAll(buttons);
        leftMenu.getChildren().addAll(TEST_BUTTONS);

        // Dropdown

        ComboBox<Button> dropdown = new ComboBox<>();
        dropdown.setPromptText("Select a status");

        dropdown.getItems().addAll(
            new Button("ONLINE"),
            new Button("IDLE"),
            new Button("AVOID")
        );

        leftMenu.getChildren().add(dropdown);

        return leftMenu;
    }

    private TabPane initTabsMenu() {
        TabPane tabPane = new TabPane();

        // TODO: testing tabs
        List<Tab> tabList = List.of(
            new Tab("Tab 1"),
            new Tab("Tab 2"),
            new Tab("Tab 3")
        );

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

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

        // Input Config
        TextField chatInput = new TextField();
        chatInput.setPromptText("Type your message...");
        chatInput.setStyle("-fx-font-size: 16;"); // Set font size to 16

        // Button Config
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setStyle("-fx-font-size: 16;"); // Set font size to 16

        // Create Input Listener
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

        chatGrid.add(scrollPane, 0, 0, 1, 1);
        chatGrid.add(chatInput, 0, 1);
        chatGrid.add(sendButton, 1, 1);

        // Add the GridPane to the tab content
        tabContent.setCenter(chatGrid);

        return tabContent;
    }
}
