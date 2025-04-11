package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.bll.UsersManager;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class EditWindowController implements Initializable {

    @FXML
    public Button btnCancel;
    @FXML
    public Button btnSaveChanges;
    @FXML
    public ImageView avatar;
    @FXML
    public TextField txtLocation;
    @FXML
    public ComboBox<String> comboAssign;
    @FXML
    public TextArea txtAreaDescription;
    @FXML
    public Label lblUploadAvatar;
    @FXML
    public TextField txtEventName;
    @FXML
    public TextField txtStartTime;
    @FXML
    public TextField txtEndTime;
    @FXML
    private DatePicker editDatePicker;
    @FXML
    private StackPane avatarUploadBox;

    private final EventTicketSystemModel model = new EventTicketSystemModel();

    private EventCardController parentController;

    private String eventImagePath;

    private Event currentEvent;

    public void setEvent(Event event) {
        this.currentEvent = event;
    }

    private UsersManager usersManager = new UsersManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Users> usersList = usersManager.getAllUsers(); // Get all users
            List<String> usernames = new ArrayList<>();
            for (Users user : usersList) {
                usernames.add(user.getUserName());
            }
            comboAssign.getItems().clear();
            comboAssign.getItems().addAll(usernames); // Add usernames to ComboBox

            if (!usernames.isEmpty()) {
                comboAssign.getSelectionModel().clearSelection();
            }

            if (currentEvent != null) {
                loadEventData(currentEvent);  // Ensure assigned user is displayed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickBrowseAvatar(MouseEvent mouseEvent) {
        String imgPath = (eventImagePath != null) ? eventImagePath : "";
        // Open a FileChooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Avatar Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        // Show the chooser
        File selectedFile = fileChooser.showOpenDialog(avatarUploadBox.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File destDir = new File("eventImg");  // Folder at the project root (make sure it's added to your repository)
                if (!destDir.exists()) {

                    destDir.mkdirs();
                }
                // Create destination file with the same name
                File destFile = new File(destDir, selectedFile.getName());
                // Copy file (replace if already exists)
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Set the relative path (so all team members refer to the same resource)
                String eventImagePath = "/eventImg/" + selectedFile.getName();
                lblUploadAvatar.setText("Selected: " + selectedFile.getName());
                System.out.println("DEBUG: Copied avatar to: " + destFile.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                AlertUtil.showErrorAlert("Error", "Failed to copy avatar image.");
            }
        }
    }

    public void onClickCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onClickSaveChanges(ActionEvent actionEvent) throws IOException {
        try {
            // Retrieve values from form fields
            String eventName = txtEventName.getText();
            LocalDate selectedDate = editDatePicker.getValue(); // Get date from DatePicker
            LocalTime startTime = LocalTime.parse(txtStartTime.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(txtEndTime.getText(), DateTimeFormatter.ofPattern("HH:mm"));

            LocalDateTime startDatetime = LocalDateTime.of(selectedDate, startTime);
            LocalDateTime endDatetime = LocalDateTime.of(selectedDate, endTime);

            String location = txtLocation.getText();
            String notes = txtAreaDescription.getText();
            String imgPath = lblUploadAvatar.getText();
            String assignUser = comboAssign.getSelectionModel().getSelectedItem().toString();
            if (currentEvent == null) {
                AlertUtil.showErrorAlert("Error", "No event selected.");
                return;
            }
            // Validation
            if (eventName.isEmpty() || location.isEmpty() || notes.isEmpty()) {
                AlertUtil.showErrorAlert("Failed to edit event", "All fields are required.");
                return;
            } //time and date cannot be empty, but .isEmpty(); does not work
            // Get the selected event ID (if it's an edit)
            Event selectedEvent = currentEvent; // Store current event when opening edit form
            if (selectedEvent == null) {
                AlertUtil.showErrorAlert("Failed to edit event", "No event selected.");
                return;
            }
            // Update event details
            selectedEvent.setEventName(eventName);
            selectedEvent.setStartDatetime(startDatetime);
            selectedEvent.setEndDatetime(endDatetime);
            selectedEvent.setLocation(location);
            selectedEvent.setNotes(notes);
            selectedEvent.setEventImagePath(imgPath);
            selectedEvent.setAssignedUser(assignUser);
            // Call database update method
            model.updateEvent(selectedEvent);

            if (parentController != null) {
                parentController.refreshEventData(currentEvent);
            }

            // Close the edit window
            Stage stage = (Stage) btnSaveChanges.getScene().getWindow();
            stage.close();
            AlertUtil.showInfoAlert("Success", "Event updated successfully.");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Error", "Failed to update event: " + e.getMessage());
        }
    }

    public void loadEventData(Event event) {
        if (event != null) {
            currentEvent = event;// Store current event for saving
            txtEventName.setText(event.getEventName());
            txtLocation.setText(event.getLocation());
            txtAreaDescription.setText(event.getNotes());
            lblUploadAvatar.setText(event.getEventImagePath());

            editDatePicker.setValue(event.getStartDatetime().toLocalDate());
            txtStartTime.setText(event.getStartDatetime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            txtEndTime.setText(event.getEndDatetime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

            comboAssign.getSelectionModel().clearSelection();

            if (event.getAssignedUser() != null) {
                comboAssign.getSelectionModel().select(event.getAssignedUser());
            }
        }
    }
    public void setParentController(EventCardController parentController) {
        this.parentController = parentController;
    }
}