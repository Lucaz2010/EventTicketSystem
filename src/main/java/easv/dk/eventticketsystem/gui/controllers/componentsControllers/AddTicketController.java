package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.be.TicketType;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;


/**
 * Controller for the "Add Ticket" window.
 * Allows the user to select an event, choose a ticket type, enter quantity,
 * and create one or more tickets associated with a specific order.
 */

public class AddTicketController {

    @FXML private ComboBox<Event> comboEvent;
    @FXML private ComboBox<TicketType> comboTicketType;
    @FXML private TextField txtQuantity;


    private EventTicketSystemModel model;
    private int orderId;
    private OrderCardController parentController;

    /**
     * Sets the shared model instance and populates combo boxes.
     */

    public void setModel(EventTicketSystemModel model) {
        this.model = model;
        loadTicketTypes();
        // Events are loaded in combo box here

        try {
            comboEvent.setItems(model.getAllEvents());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setOrderId(int orderId) {

        this.orderId = orderId;
    }

    /**
     * Sets a reference to the parent controller to allow refreshing after ticket creation.
     */

    public void setParentController(OrderCardController controller) {
        this.parentController = controller;
    }


    /**
     * Applies a custom display format for TicketType items in the combo box.
     */

    @FXML
    public void initialize() {
        formatTicketType();
        formatEventName();

    }


    @FXML
    public void formatTicketType() {

        comboTicketType.setConverter(new StringConverter<TicketType>() {
            @Override
            public String toString(TicketType ticketType) {
                if (ticketType == null) return "";
                String priceText = ticketType.getPrice() < 0.01 ? "Free" : String.format("%.2f DKK", ticketType.getPrice());
                return ticketType.getTypeName() + " - " + priceText;
            }

            @Override
            public TicketType fromString(String string) {
                return null; // Not needed for display purposes
            }
        });

    }
    /**
     * Applies a custom display format for Event items in the combo box.
     */
    @FXML
    public void formatEventName() {

        comboEvent.setConverter(new StringConverter<Event>() {
            @Override
            public String toString(Event event) {
                return event == null ? "" : event.getEventName();
            }

            @Override
            public Event fromString(String string) {
                return null; // Not used
            }
        });

    }




    /**
     * Loads all available ticket types into the combo box.
     */

    @FXML
    private void loadTicketTypes() {
        try {
            comboTicketType.setItems(model.getAllTicketTypes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTicketTypes() {
        loadTicketTypes();
    }

    /**
     * Opens the "Add New Ticket Type" window and links it to this controller.
     */
    @FXML
    public void onClickNewType(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/AddNewTicketType.fxml"));
            Parent root = loader.load();

            NewTicketTypeController controller = loader.getController();
            controller.setModel(model);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Ticket Type");
            stage.show();

            // When the window closes, check if a new ticket type was created and select it

            stage.setOnHiding(e -> {
                TicketType newType = controller.getCreatedTicketType();
                if (newType != null) {
                    refreshTicketTypes();
                    comboTicketType.getSelectionModel().select(newType);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates inputs, creates tickets, refreshes parent view, and closes the window.
     */

    @FXML
    private void onClickSaveChanges(ActionEvent event) {
        Event selectedEvent = comboEvent.getValue();
        TicketType selectedType = comboTicketType.getValue();
        String quantityStr = txtQuantity.getText();

        if (selectedEvent == null || selectedType == null || quantityStr.isEmpty()) {
            AlertUtil.showErrorAlert("Missing Fields", "Please fill in all fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Invalid Quantity", "Please enter a valid number greater than 0.");
            return;
        }
        if (model.getTicketManager() == null) {
            AlertUtil.showErrorAlert("System Error", "TicketManager not initialized.");
            return;
        }


        try {
            // Create quantity of tickets using the provided orderId
            model.getTicketManager().createTicket(orderId, selectedType.getTicketTypeId(), selectedEvent.getEventId(), quantity);

            AlertUtil.showSuccessAlert("Success", quantity + " ticket(s) created!");

            //  Refresh ticket list in the order card
            if (parentController != null) {
                parentController.refreshTickets(); // method used in OrderCardController
            }

            // Close the window

            ((Stage) comboEvent.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert("Error", "Failed to create ticket(s).");
        }
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}