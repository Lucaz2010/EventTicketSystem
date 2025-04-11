package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import easv.dk.eventticketsystem.security.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarController implements Initializable {

    @FXML
    private TextField txtQuery;
    @FXML
    private Label lblUserName;

    private ManageUsersController parentController;
    private ManageEventsController eventParentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Users loggedUser = UserSession.getCurrentUser();
        if (loggedUser != null) {
            lblUserName.setText("Hi " + loggedUser.getUserName() + "!");
        } else {
            lblUserName.setText("Welcome, Guest!");
        }
    }

    public void setParentController(ManageUsersController parentController) {
        this.parentController = parentController;
    }
    public void setEventParentController(ManageEventsController eventParentController) {
        this.eventParentController = eventParentController;
    }

    public void handleSearch(ActionEvent actionEvent) {
        String query = txtQuery.getText();
        if (query.isEmpty()) {
            AlertUtil.showWarningAlert("Error", "The query is empty");
            return;
        }
        System.out.println("Searching for: " + query);
        if (parentController != null) {
            parentController.searchUsers(query);
        }
        if (eventParentController != null) {
            eventParentController.searchEvent(query);
        }
    }

}
