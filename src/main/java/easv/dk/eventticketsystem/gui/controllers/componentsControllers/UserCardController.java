package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.controllers.ManageUsersController;
import easv.dk.eventticketsystem.gui.controllers.UserEditorController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import easv.dk.eventticketsystem.gui.util.NotificationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class UserCardController {
    @FXML
    private FontIcon btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private ImageView avatar;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserEmail;
    @FXML
    private Label lblUserPhone;
    @FXML
    private Label lblRole;

    private TextField editingTextField;
    private String newUserImagePath;
    private Users user;
    private final EventTicketSystemModel model = new EventTicketSystemModel();
    private ManageUsersController manageUsersController;

    public void setUserData(Users user) {
        this.user = user;
        lblUserName.setText(user.getUserName());
        lblUserEmail.setText(user.getUserEmail());
        lblRole.setText(user.getRole());
        lblUserPhone.setText(user.getUserPhone());

        String imagePath = user.getUserImagePath();  // e.g., "shared/userImages/newPerson.JPG" or "/userImg/Charlie.JPG"
        if (imagePath != null && !imagePath.isEmpty()) {
            // First try loading as a classpath resource.
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is != null) {
                System.out.println("DEBUG: Loaded image from classpath resource: " + imagePath);
                avatar.setImage(new Image(is));
            } else {
                // Fallback: load from file system.
                System.out.println("DEBUG: Resource not found in classpath: " + imagePath);
                String workingDir = System.getProperty("user.dir");
                System.out.println("DEBUG: Working directory: " + workingDir);

                // Use the File constructor that takes a parent directory and a child path.
                File imageFile = new File(workingDir, imagePath);
                System.out.println("DEBUG: Constructed absolute image path: " + imageFile.getAbsolutePath());

                if (imageFile.exists()) {
                    System.out.println("DEBUG: Found file on disk: " + imageFile.getAbsolutePath());
                    avatar.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.err.println("DEBUG: File not found on disk: " + imageFile.getAbsolutePath());
                }
            }
        }
    }

    public void handleDoubleClickTxt(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Label && mouseEvent.getClickCount() == 2) {
            Label label = (Label) mouseEvent.getSource();

            TextField textField = new TextField(label.getText());
            textField.setStyle(label.getStyle());

            // Commit changes when Enter is pressed.
            textField.setOnAction(e -> finishEditing(label, textField));

            // Also commit when focus is lost
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    finishEditing(label, textField);
                }
            });

            // Replace the Label with the TextField in its parent container.
            if (label.getParent() instanceof Pane) {
                Pane parent = (Pane) label.getParent();
                int index = parent.getChildren().indexOf(label);
                parent.getChildren().set(index, textField);
                textField.requestFocus();
            }
        }
    }
    private void finishEditing(Label label, TextField textField) {
        // Get the new text from the TextField.
        String newText = textField.getText();
        label.setText(newText);

        // Replace the TextField with the Label in the same parent.
        if (textField.getParent() instanceof Pane) {
            Pane parent = (Pane) textField.getParent();
            int index = parent.getChildren().indexOf(textField);
            parent.getChildren().set(index, label);
            // Optionally, trigger additional actions (e.g., saving the change).
        }
        textField = null;
    }

    public void onClickEditUser(ActionEvent actionEvent) throws IOException {
        user.setUserName(lblUserName.getText());
        user.setUserEmail(lblUserEmail.getText());
        user.setUserPhone(lblUserPhone.getText());
        user.setRole(lblRole.getText());

        // If a new image has been chosen, copy it to the shared folder and update the path.
        if (newUserImagePath != null && !newUserImagePath.isEmpty()) {
            // Define the shared folder path (adjust as needed)
            Path sharedDir = Paths.get(System.getProperty("user.dir"), "shared", "userImages");
            if (!Files.exists(sharedDir)) {
                Files.createDirectories(sharedDir);
            }
            // Create a File object from the newUserImagePath (this is the local absolute path)
            File imageFile = new File(newUserImagePath);
            if (imageFile.exists()) {
                // Determine the target path in the shared folder using the original file name
                Path targetPath = sharedDir.resolve(imageFile.getName());
                // Copy the file to the shared folder, replacing any existing file
                Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Build a shared relative path (e.g., "shared/userImages/filename.jpg")
                String sharedPath = "shared" + File.separator + "userImages" + File.separator + imageFile.getName();
                // Update the user's image path
                user.setUserImagePath(sharedPath);

                // Update the avatar ImageView immediately using the new shared path
                avatar.setImage(new Image(targetPath.toUri().toString()));
            } else {
                System.err.println("Image file not found: " + newUserImagePath);
            }
        }
        // Update the user record in the database
        model.updateUsers(user);

        // Refresh the Manage Users view
        if (manageUsersController != null) {
            manageUsersController.loadAllUsers();
            AlertUtil.showSuccessAlert("User Updated", "User information has been updated successfully.");
        } else {
            System.err.println("Parent controller is not set!");
        }
    }

    public void onClickDeleteUser(ActionEvent actionEvent) throws IOException {
        NotificationUtil.showWarningNotification(btnEdit.getScene().getWindow(),
                "User Updated",
                "User information has been updated successfully.");
        boolean confirmed = AlertUtil.showConfirmationAlert("Delete User Confirmation",
                "Are you sure you want to delete this user?");
        if (confirmed) {
            model.deleteUsers(user);
            System.out.println("User deleted: " + user.getUserName());
        } else {
            System.out.println("User deletion canceled");
        }
        // Refresh the list of users using the parent controller, if available
        if (manageUsersController != null) {
            manageUsersController.loadAllUsers();
        } else {
            System.err.println("Parent controller is not set!");
        }
    }

    public void setParentController(ManageUsersController manageUsersController) {
        this.manageUsersController = manageUsersController;
    }

    public void handleDoubleClickRoleAndImg(MouseEvent event) {if (event.getClickCount() == 2) {
        Object source = event.getSource();
        // For role label, switch to a ComboBox
        if (source instanceof Label) {
            Label label = (Label) source;
            if ("lblRole".equals(label.getId())) {
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.getItems().addAll("Admin", "Event Coordinator");
                // Set the current role as the selected value.
                comboBox.setValue(label.getText());

                // Commit the change when user selects an option.
                comboBox.setOnAction(e -> finishEditingRole(label, comboBox));
                // Also, commit when the combo box loses focus.
                comboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        finishEditingRole(label, comboBox);
                    }
                });

                // Replace the label with the ComboBox in its parent container.
                if (label.getParent() instanceof Pane) {
                    Pane parent = (Pane) label.getParent();
                    int index = parent.getChildren().indexOf(label);
                    parent.getChildren().set(index, comboBox);
                    comboBox.requestFocus();
                }
            }
        }
        // For avatar image, open a file chooser to select a new image.
        else if (source instanceof ImageView) {
            ImageView imageView = (ImageView) source;
            if ("avatar".equals(imageView.getId())) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose a new image");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
                File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
                if (file != null) {
                    newUserImagePath = file.getAbsolutePath();
                    Image newImage = new Image(file.toURI().toString());
                    avatar.setImage(newImage);

                }else {
                    System.err.println("Failed to copy the new image file.");
                }
            }
        }
    }
    }

    // Helper method to finish editing the role.
    private void finishEditingRole(Label label, ComboBox<String> comboBox) {
        if (comboBox.getParent() instanceof Pane) {
            Pane parent = (Pane) comboBox.getParent();
            int index = parent.getChildren().indexOf(comboBox);
            label.setText(comboBox.getValue());
            parent.getChildren().set(index, label);
            // Optionally, sync the role change to the database here.
        }
    }
}
