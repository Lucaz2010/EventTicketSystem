package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;

public class ToolbarController {
    @FXML
    private TextField txtQuery;


    private ManageUsersController parentController;
    private ManageEventsController eventParentController;

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
