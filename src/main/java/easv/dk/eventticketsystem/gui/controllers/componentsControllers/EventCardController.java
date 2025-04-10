package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.gui.controllers.ManageEventsController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventCardController {
    public AnchorPane eventPane;
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

            EditWindowController editWindowController =  fxmlLoader.getController();
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

    public void onClickDelete(ActionEvent actionEvent)throws IOException {
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
}