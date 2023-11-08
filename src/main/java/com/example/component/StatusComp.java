package com.example.component;

import com.example.model.User;
import com.example.model.User.Status;

import javafx.scene.control.ComboBox;

public class StatusComp {
  
  private User user;

  public StatusComp(User user) {
    this.user = user;
  }

  public ComboBox<String> chooseStatus() {

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
}
