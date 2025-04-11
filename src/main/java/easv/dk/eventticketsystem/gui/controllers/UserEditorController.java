package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.controllers.componentsControllers.UserCardController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class UserEditorController implements Initializable {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private StackPane avatarUploadBox;
    @FXML
    private Label lblUploadAvatar;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private ComboBox<String> comboRole;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    private Users user;
    private final EventTicketSystemModel model = new EventTicketSystemModel();
    private String userImagePath;
    private ManageUsersController manageUsersController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onClickCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void onClickCreateUser(ActionEvent actionEvent) throws IOException {
        String userName = txtUsername.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String role = comboRole.getValue();
        //String imgPath = (userImagePath != null) ? userImagePath : "";

        if (userName.isEmpty() || email.isEmpty() || phone.isEmpty() || role == null || role.isEmpty()) {
            AlertUtil.showErrorAlert("Fail to create a new user", "Username, email, phone or role is empty");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            AlertUtil.showErrorAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }
        if (!phone.matches("^\\+?[0-9]{7,15}$")) {
            AlertUtil.showErrorAlert("Invalid Phone", "Please enter a valid phone number (7 to 15 digits, optional +).");
            return;
        }

        // Check whether we are updating an existing user or creating a new one.
        if (user != null && user.getUserId() > 0) {
            // Editing an existing user: update its properties and call an update method.
            System.out.println("Updating existing user with ID: " + user.getUserId());
            user.setUserName(userName);
            user.setUserEmail(email);
            user.setUserPhone(phone);
            user.setRole(role);
            // Only update image if a new image path is provided
            if (userImagePath != null && !userImagePath.isEmpty()) {
                user.setUserImagePath(userImagePath);
            }
            model.updateUsers(user);
        } else {
            // Creating a new user
            System.out.println("Creating a new user.");
            // Use the provided image path if available, otherwise set it to empty or a default placeholder
            String finalImgPath = (userImagePath != null && !userImagePath.isEmpty()) ? userImagePath : "";
            String defaultPassword = "";
            Users newUser = new Users(0, userName, finalImgPath, role, email, phone,defaultPassword);
            model.createNewUsers(newUser);
        }

        // Refresh the Manage Users view.
        if (manageUsersController != null) {
            manageUsersController.loadAllUsers();
        } else {
            System.err.println("manageUsersController is null, cannot refresh user list.");
        }

        // Close the current window.
        Stage stage = (Stage) btnCreate.getScene().getWindow();
        stage.close();
    }

    public void onClickBrowseAvatar(MouseEvent actionEvent) {
        // Open a FileChooser to select an image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select User Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        // Show the chooser
        File selectedFile = fileChooser.showOpenDialog(avatarUploadBox.getScene().getWindow());
        if (selectedFile != null) {
            try {
                File destDir = new File("userImg");  // Folder at the project root (make sure it's added to your repository)
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                // Create destination file with the same name
                File destFile = new File(destDir, selectedFile.getName());
                // Copy file (replace if already exists)
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Set the relative path (so all team members refer to the same resource)
                userImagePath = "/userImg/" + selectedFile.getName();
                lblUploadAvatar.setText("Selected: " + selectedFile.getName());
                System.out.println("DEBUG: Copied user img to: " + destFile.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                AlertUtil.showErrorAlert("Error", "Failed to copy avatar image.");
            }
        }
    }

    public void setParentController(ManageUsersController manageUsersController) {
        this.manageUsersController = manageUsersController;
    }

}
