package com.example.view;

import java.util.List;

import com.example.model.User;
import com.example.service.Broadcaster;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MenuView extends Application {

	private final Label titleLabel;
	private static User user;

	public MenuView(User user) {
		MenuView.user = user; 
		this.titleLabel = new Label("Welcome " + MenuView.user.getUsername());
	}

	public static void main(String[] args) {
		launch(args);
		
		Broadcaster.initProbe(user.getUsername());
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tabbed Menu Example");
		
		VBox leftMenu = initSideMenu(); // Create a VBox for the left-side menu
		leftMenu.setPrefWidth(200); // Set the preferred width (e.g., 200 pixels)

		TabPane tabPane = initTabsMenu(); // Create a TabPane for the top tabbed menu

		// Create a BorderPane to combine the left menu and tabbed menu
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(leftMenu);
		borderPane.setCenter(tabPane);

		Scene scene = new Scene(borderPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private VBox initSideMenu() {
		// Create VBox
		VBox leftMenu = new VBox(10);
		leftMenu.setPadding(new Insets(10));
	
		// Testing Styles
		leftMenu.setStyle(
			"-fx-background-color: lightgray; " + // Background color
			"-fx-padding: 10; " +                  // Padding
			"-fx-border-style: solid inside; " +  // Border style
			"-fx-border-width: 1; " +             // Border width
			"-fx-border-insets: 5; " +            // Border insets
			"-fx-border-radius: 5;"               // Border radius
		);
	
		// Set Style for Label
		titleLabel.setStyle("-fx-font-weight: bold;"); // Optional: Apply bold font
	
		// Add the title label to the VBox
		leftMenu.getChildren().add(titleLabel);
	
		// Create menu items or buttons for the left menu
		List<Button> btn_list = List.of(
			new Button("Menu Item 1"),
			new Button("Menu Item 2"),
			new Button("Menu Item 3")
		);
	
		// Add Buttons to Menu
		leftMenu.getChildren().addAll(btn_list);
	
		return leftMenu;
	}	

	private TabPane initTabsMenu() {

		TabPane tabPane = new TabPane();
		Label label;
		StackPane pane;

		List<Tab> tab_list = List.of(
				new Tab("Tab 1"),
				new Tab("Tab 2"),
				new Tab("Tab 3"));

		for (Tab tab : tab_list) {

			int index = (tab_list.indexOf(tab) + 1); // Index of Tab in TabList

			// Initialize Label and StackPane
			label = new Label("Content for Tab " + index);
			pane = new StackPane();

			// Add Pane to Tab
			pane.setPadding(new Insets(20));
			pane.getChildren().add(label);
			tab.setContent(pane);

			// Add Tab to TabPane
			tabPane.getTabs().add(tab);
		}

		return tabPane;
	}
}
