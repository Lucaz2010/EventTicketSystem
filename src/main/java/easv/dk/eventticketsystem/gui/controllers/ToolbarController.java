package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ToolbarController {
    @FXML
    private TextField txtQuery;
    @FXML
    private Label lblUserName;

    private ManageUsersController parentController;

    public void setParentController(ManageUsersController parentController) {
        this.parentController = parentController;
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
    }

}
