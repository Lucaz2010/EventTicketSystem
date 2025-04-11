package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.bll.UsersManager;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

public class CreateNewEventController implements Initializable {
    public Label lblAvatarPrompt;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private StackPane avatarUploadBox;
    @FXML
    private Label lblUploadAvatar;
    @FXML
    private TextField txtEventName;
    @FXML
    private DatePicker txtDate;
    @FXML
    private TextField txtStartTime;
    @FXML
    private TextField txtEndTime;
    @FXML
    private TextField txtLocation;
    @FXML
    private TextArea txtDescription;
    @FXML
    private String avatarImagePath;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;
    @FXML
    private ComboBox<String> createComboAssign;

    private String price;
    private String locationG;
    private String eventImagePath;
    private ManageEventsController manageEventsController;
    private final EventTicketSystemModel model = new EventTicketSystemModel();
    private Event event;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");;

    private UsersManager usersManager = new UsersManager();

    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Users> usersList = usersManager.getAllUsers(); // Get all users
            List<String> usernames = new ArrayList<>();

            for (Users user : usersList) {
                usernames.add(user.getUserName());
            }
            createComboAssign.getItems().clear();
            createComboAssign.getItems().addAll(usernames); // Add usernames to ComboBox

            // Optional: Select the first user if list is not empty
            if (!usernames.isEmpty()) {
                createComboAssign.getSelectionModel().clearSelection();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickBrowseAvatar(MouseEvent mouseEvent) {
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
                File destDir = new File("eventImg");  // Folder at the project root
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                // Create destination file with the same name
                File destFile = new File(destDir, selectedFile.getName());
                // Copy file (replace if already exists)
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Set the relative path (so all team members refer to the same resource)
                eventImagePath = "/eventImg/" + selectedFile.getName();
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

    public void onClickCreateEvent(ActionEvent actionEvent) throws IOException {
        String eventName = txtEventName.getText();
        LocalDate date = txtDate.getValue();
        String startTimeStr = txtStartTime.getText();
        String endTimeStr = txtEndTime.getText();
        String location = txtLocation.getText();
        String eventImagePath = avatarImagePath != null ? avatarImagePath : "N/A";
        String notes = txtDescription.getText();
        String assignUser = createComboAssign.getSelectionModel().getSelectedItem().toString();

        // Step 1: Check for required fields
        if (eventName.isEmpty() || date == null || startTimeStr.isEmpty() || endTimeStr.isEmpty() || location.isEmpty()) {
            AlertUtil.showErrorAlert("Fail to create a new event", "Please fill in all required fields");
            return;
        }
        // ✅ Step 2: Validate time format (this is where you add it)
        if (!startTimeStr.matches("^\\d{2}:\\d{2}$") || !endTimeStr.matches("^\\d{2}:\\d{2}$")) {
            AlertUtil.showErrorAlert("Invalid Time Format", "Please enter time in HH:mm format (e.g. 14:30)");
            return;
        }
        String assignedUser = createComboAssign.getSelectionModel().getSelectedItem();
        if (assignedUser == null || assignedUser.isEmpty()) {
            AlertUtil.showErrorAlert("User Assignment", "No user assigned to this event. Please select a user.");
            return;
        }
        try {
            // Step 3: Safely parse the times
            LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
            LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            if (endDateTime.isBefore(startDateTime)) {
                AlertUtil.showErrorAlert("Invalid Time", "End time must be after start time.");
                return;
            }

            Event newEvent = new Event(
                    0,
                    eventName,
                    startDateTime,
                    endDateTime,
                    location,
                    notes,
                    eventImagePath,
                    assignUser
            );
            model.createNewEvent(newEvent);
            if (manageEventsController != null) {
                manageEventsController.loadAllEvents();
            }

            ((Stage) btnCreate.getScene().getWindow()).close();

        } catch (Exception e) {
            AlertUtil.showErrorAlert("Fail to create a new event", e.getMessage());
        }
    }

    public void setParentController(ManageEventsController manageEventsController) {
        this.manageEventsController = manageEventsController;
    }
}