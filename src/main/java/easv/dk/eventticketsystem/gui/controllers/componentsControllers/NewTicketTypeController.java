package easv.dk.eventticketsystem.gui.controllers.componentsControllers;


import easv.dk.eventticketsystem.be.TicketType;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewTicketTypeController {


    @FXML private TextField txtTicketTypeName;
    @FXML private ComboBox<String> comboAssign; // for "Normal", "Voucher"
    @FXML private TextField txtPrice;

    @FXML
    private Button btnCancel;

    private AddTicketController parentController;

    public void setParentController(AddTicketController controller) {
        this.parentController = controller;
    }

    private TicketType createdTicketType;

    public TicketType getCreatedTicketType() {
        return createdTicketType;
    }



    @FXML
    public void initialize() {
        comboAssign.getItems().addAll("Normal", "Voucher");
    }


    /// Use of system model
    private EventTicketSystemModel model;

    public void setModel(EventTicketSystemModel model) {
        this.model = model;
    }

    @FXML
    private void onClickSaveChanges(ActionEvent actionEvent) {
        String name = txtTicketTypeName.getText().trim();
        String category = comboAssign.getValue();
        String priceText = txtPrice.getText().trim();

        if (name.isEmpty() || category == null || priceText.isEmpty()) {
            AlertUtil.showErrorAlert("Missing Fields", "Please fill in all fields.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Invalid Price", "Please enter a valid non-negative number for the price.");
            return;
        }

        try {
            createdTicketType = new TicketType(0, name, category, price);
            model.createTicketType(createdTicketType);
            AlertUtil.showSuccessAlert("Success", "New ticket type created!");

            // ✅ Call refresh on parent
            if (parentController != null) {
                parentController.refreshTicketTypes();
            }

            closeWindow();
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Error", "Could not save ticket type.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickCancel(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }



}
