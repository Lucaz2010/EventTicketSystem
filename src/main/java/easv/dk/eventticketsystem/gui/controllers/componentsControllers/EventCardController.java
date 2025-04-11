package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.controllers.ManageEventsController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventCardController {
    @FXML
    private Label lblAssignedUser;
    @FXML
    private AnchorPane eventPane;
    @FXML
    private Button btnEditEvent;
    @FXML
    private ImageView eventImage;
    @FXML
    private Label lblEventName;
    @FXML
    private Label lblLocation;
    @FXML
    private Label lblStartTime;
    @FXML
    private Label lblEndTime;
    @FXML
    private Label lblPersonAssigned;
    @FXML
    private Label lblDate;

    private Event currentEvent;

    public void setEvent(Event event) {
        this.currentEvent = event;
    }

    private Event event;

    private final EventTicketSystemModel model = new EventTicketSystemModel();

    private ManageEventsController manageEventsController;

    public void setEventData(Event event) {
        this.event = event;
        if (event == null) {
            System.out.println("DEBUG: setEventData called with null event");
            return;
        }
        this.currentEvent = event; // Store the event properly

        lblEventName.setText(event.getEventName());
        lblLocation.setText(event.getLocation());

        LocalDate eventDate = event.getStartDatetime().toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        lblDate.setText(eventDate.format(dateFormatter)); // Display date

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        lblStartTime.setText("Start time: " + event.getStartDatetime().toLocalTime().format(timeFormatter));
        lblEndTime.setText("End time: " + event.getEndDatetime().toLocalTime().format(timeFormatter));


        lblPersonAssigned.setText(event.getAssignedUser());
        String imgPath = event.getEventImagePath();
        if (imgPath != null && !imgPath.isEmpty()) {
            Image image = new Image("file:" + System.getProperty("user.dir") + imgPath);
            eventImage.setImage(image);
        }
        System.out.println("DEBUG: Event stored successfully -> " + currentEvent.getEventName());
    }

    public void onClickEdit(ActionEvent actionEvent) {
        loadEditWindow();
    }

    private void loadEditWindow() {
        if (currentEvent == null) {
            AlertUtil.showErrorAlert("Error", "No event selected.");
            System.out.println("DEBUG: No event selected in loadEditWindow()");
            return;
        }
        try {
            System.out.println("DEBUG: Loading Edit Window...");

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("EditEventView.fxml"));
            Parent root = fxmlLoader.load();

            EditWindowController editWindowController = fxmlLoader.getController();
            if (editWindowController == null) {
                System.out.println("DEBUG: EditWindowController is NULL");
                return;
            }

            // Get the edit window controller
            editWindowController.setEvent(currentEvent);  // Pass selected event
            editWindowController.setParentController(this); // Pass parent controller
            editWindowController.loadEventData(currentEvent);

            Stage stage = new Stage();
            stage.setTitle("Edit Event");
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("DEBUG: Edit Window loaded successfully -> " + currentEvent.getEventName());

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert("Error", "Failed to load the edit window.");
        }
    }

    public void onClickDelete(ActionEvent actionEvent) throws IOException {
        boolean confirmed = AlertUtil.showConfirmationAlert("Delete Event Confirmation",
                "Are you sure you want to delete this event?");
        if (confirmed) {
            model.deleteEvent(event);

        } else {
            System.out.println("Event deletion canceled");
        }
        // Refresh the list of users using the parent controller, if available
        if (manageEventsController != null) {
            manageEventsController.loadAllEvents();

        } else {
            System.err.println("Parent controller is not set!");
        }
    }

    public void setParentController(ManageEventsController manageEventsController) {
        this.manageEventsController = manageEventsController;
        // System.out.println("Parent controller set to " + this.manageEventsController);
    }

    public void refreshEventData(Event currentEvent) {
        if (currentEvent == null) {
            System.out.println("ERROR: refreshEventData called with NULL event!");
            return;
        }
        this.currentEvent = currentEvent;

        // Update UI elements
        lblEventName.setText(currentEvent.getEventName());
        lblLocation.setText(currentEvent.getLocation());
        lblStartTime.setText(currentEvent.getStartDatetime().toString());
        lblEndTime.setText(currentEvent.getEndDatetime().toString());
        lblPersonAssigned.setText(currentEvent.getAssignedUser());
        lblDate.setText(currentEvent.getStartDatetime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        lblStartTime.setText("Start time: " + currentEvent.getStartDatetime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblEndTime.setText("End time: " + currentEvent.getEndDatetime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));


        // Update image if available
        String imgPath = currentEvent.getEventImagePath();
        if (imgPath != null && !imgPath.isEmpty()) {
            Image image = new Image("file:" + System.getProperty("user.dir") + imgPath);
            eventImage.setImage(image);
        }
        System.out.println("DEBUG: UI Updated with new event details: " + currentEvent.getEventName());
    }

    public void setAuthenticatedUser(Users user) {
        if (user != null && "Admin".equalsIgnoreCase(user.getRole().trim())) {
            btnEditEvent.setVisible(false);
            lblAssignedUser.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    replaceLabelWithCheckCombo();
                }
            });
        } else {
            lblAssignedUser.setOnMouseClicked(null);
            btnEditEvent.setVisible(true);
        }

    }

    private void replaceLabelWithCheckCombo() {
        // Retrieve coordinator names from model (as in your current code)
        EventTicketSystemModel model = new EventTicketSystemModel();
        List<Users> coordinatorList;
        try {
            coordinatorList = model.getAllCoordinators();
        } catch (IOException ex) {
            ex.printStackTrace();
            coordinatorList = new ArrayList<>();
        }

        ObservableList<String> coordinatorNames = FXCollections.observableArrayList();
        for (Users coord : coordinatorList) {
            coordinatorNames.add(coord.getUserName());
        }

        CheckComboBox<String> checkComboBox = new CheckComboBox<>(coordinatorNames);

        String currentText = lblAssignedUser.getText();
        if (currentText != null && !currentText.isEmpty()) {
            String[] selections = currentText.split(",\\s*");
            for (String sel : selections) {
                if (coordinatorNames.contains(sel)) {
                    checkComboBox.getCheckModel().check(sel);
                }
            }
        }

        // Get the parent container (cast as Pane)
        Pane parent = (Pane) lblAssignedUser.getParent();
        int index = parent.getChildren().indexOf(lblAssignedUser);

        checkComboBox.setLayoutX(lblAssignedUser.getLayoutX());
        checkComboBox.setLayoutY(lblAssignedUser.getLayoutY());
        checkComboBox.setPrefWidth(lblAssignedUser.getWidth());
        checkComboBox.setPrefHeight(lblAssignedUser.getHeight());

        // Remove the label and add the CheckComboBox.
        parent.getChildren().remove(lblAssignedUser);
        parent.getChildren().add(index, checkComboBox);

        // Create a Save button
        Button btnSave = new Button("Save");
        // Optionally, style and position the button appropriately
        btnSave.setLayoutX(checkComboBox.getLayoutX() + checkComboBox.getPrefWidth() + 5);
        btnSave.setLayoutY(checkComboBox.getLayoutY());

        // Add the button to the parent container.
        parent.getChildren().add(btnSave);

        btnSave.setOnAction(evt -> {
            // Get the selected items.
            String selectedValues = String.join(", ", checkComboBox.getCheckModel().getCheckedItems());
            System.out.println("Save clicked. Selected coordinator(s): " + selectedValues);

            // Update the Event object.
            if (currentEvent != null) {
                currentEvent.setAssignedUser(selectedValues);
                try {
                    model.updateEvent(currentEvent);
                    System.out.println("Database updated for event: " + currentEvent.getEventName());
                    // Now refresh the Manage Events view:
                    if (manageEventsController != null) {
                        manageEventsController.loadAllEvents();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AlertUtil.showErrorAlert("Error", "Failed to update assigned coordinators.");
                }
            }

            // Create a new Label with these selections.
            Label newLabel = new Label(selectedValues);
            newLabel.setLayoutX(checkComboBox.getLayoutX());
            newLabel.setLayoutY(checkComboBox.getLayoutY());
            newLabel.setPrefWidth(checkComboBox.getPrefWidth());
            newLabel.setPrefHeight(checkComboBox.getPrefHeight());
            newLabel.setOnMouseClicked(event2 -> {
                if (event2.getClickCount() == 2) {
                    replaceLabelWithCheckCombo();
                }
            });

            // Replace the CheckComboBox and the Save button with the new Label.
            parent.getChildren().set(index, newLabel); // Using set() to replace the node.
        });


    }



}