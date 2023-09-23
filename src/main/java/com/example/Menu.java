package com.example;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Menu extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tabbed Menu Example");

		// Create a VBox for the left-side menu
		VBox leftMenu = sideMenu();

		// Create a TabPane for the top tabbed menu
		TabPane tabPane = tabsMenu();

		// Create a BorderPane to combine the left menu and tabbed menu
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(leftMenu);
		borderPane.setCenter(tabPane);

		Scene scene = new Scene(borderPane, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private VBox sideMenu() {

		// Create VBox
		VBox leftMenu = new VBox(10);
		leftMenu.setPadding(new Insets(10));
		leftMenu.setStyle("-fx-background-color: lightgray;");

		// Create menu items or buttons for the left menu
		Button menuItem1 = new Button("Menu Item 1");
		Button menuItem2 = new Button("Menu Item 2");
		Button menuItem3 = new Button("Menu Item 3");

		leftMenu.getChildren().addAll(menuItem1, menuItem2, menuItem3);

		return leftMenu;
	}

	private TabPane tabsMenu() {

		TabPane tabPane = new TabPane();
		Label label;
		StackPane pane;

		List<Tab> tab_list = List.of(
			new Tab("Tab 1"),
			new Tab("Tab 2"),
			new Tab("Tab 3")
		);

		for (Tab tab : tab_list) {

			// Index of Tab in TabList
			int index = (tab_list.indexOf(tab) + 1);

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
