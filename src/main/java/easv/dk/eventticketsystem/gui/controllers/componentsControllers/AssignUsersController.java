package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.bll.UsersManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AssignUsersController implements Initializable {
    @FXML
    private AnchorPane usersAnchorPane;
    @FXML
    private Button btnAssignUser;

    private EditWindowController parentController;

    private final Set<String> assignedUsers = new HashSet<>();

    private UsersManager usersManager = new UsersManager();

    public void setParentController(EditWindowController parentController) {
        this.parentController = parentController;
    }

    public Set<String> getAssignedUsers() {
        return assignedUsers;
    }

    public void onClickAssignUser(ActionEvent actionEvent) {
        System.out.println("Assign clicked");
        if (parentController != null) {
            parentController.setAssignedUsers(assignedUsers);
        }
        closeWindow(actionEvent);
    }

    public void onClickCancel(ActionEvent actionEvent) {
        System.out.println("Cancel clicked");
        closeWindow(actionEvent);
    }

    private void closeWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setEvent(Event currentEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Users> usersList = null; // Get all users
        try {
            usersList = usersManager.getAllUsers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Dynamically create buttons for each user and add them to the usersAnchorPane
        for (int i = 0; i < usersList.size(); i++) {
            Users user = usersList.get(i);
            String username = user.getUserName();

            // Create a new button for each user
            Button userButton = new Button(username);
            userButton.setStyle("-fx-background-color: transparent; -fx-border-color: #C00D0D; -fx-text-fill: #C00D0D; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 15;");

            // Set button layout (positioning)
            userButton.setLayoutX(20 + (i % 4) * 120);  // Position the buttons horizontally
            userButton.setLayoutY(56 + (i / 4) * 60);  // Position the buttons vertically (after 4 buttons, move to the next row)

            // Add the button action to toggle selection
            userButton.setOnAction(event -> toggleUserSelection(userButton, username));

            // Add the button to the anchor pane
            usersAnchorPane.getChildren().add(userButton);
        }
    }

    // Toggles the button color and adds/removes the user from the assignedUsers set
    private void toggleUserSelection(Button userButton, String username) {
        if (assignedUsers.contains(username)) {
            assignedUsers.remove(username);
            userButton.setStyle("-fx-background-color: transparent; -fx-border-color: #C00D0D; -fx-text-fill: #C00D0D; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 15;");
        } else {
            assignedUsers.add(username);
            userButton.setStyle("-fx-background-color: transparent; -fx-text-fill:  #009640;-fx-border-color: #009640; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 15;");
        }
    }
}