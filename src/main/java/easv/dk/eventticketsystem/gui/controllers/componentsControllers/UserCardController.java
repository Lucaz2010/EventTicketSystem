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
        String imagePath = user.getUserImagePath(); // e.g., "/userImg/admin.jpg"
        //System.out.println("DEBUG: Attempting to load image from resource: " + imagePath);

        InputStream is = getClass().getResourceAsStream(imagePath);
        if (is != null) {
            avatar.setImage(new Image(is));
            //System.out.println("DEBUG: Loaded image from resource: " + imagePath);
        } else {
            // Image is not found in classpath resources.
            // Check if the image exists as a local file.
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Define a shared folder for images (adjust this path as needed).
                Path sharedDir = Paths.get(System.getProperty("user.dir"), "shared", "userImages");
                try {
                    if (!Files.exists(sharedDir)) {
                        Files.createDirectories(sharedDir);
                    }
                    // Determine the target path in the shared folder using the original file name.
                    Path targetPath = sharedDir.resolve(imageFile.getName());
                    // Copy the file to the shared folder, replacing any existing file.
                    Files.copy(imageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    // Optionally, update the user object with the new shared image path.
                    // For example:
                    // user.setUserImagePath("/shared/userImages/" + imageFile.getName());

                    // Load the image from the shared directory.
                    avatar.setImage(new Image(targetPath.toUri().toString()));
                } catch (IOException e) {
                    System.err.println("Error copying image file: " + e.getMessage());
                }
            } else {
                System.err.println("DEBUG: Resource not found: " + imagePath);
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

        if (newUserImagePath != null && !newUserImagePath.isEmpty()) {
            user.setUserImagePath(newUserImagePath);

            // Load the image from the file system rather than via getResourceAsStream.
            File imageFile = new File(newUserImagePath);
            if (imageFile.exists()) {
                Image newImage = new Image(imageFile.toURI().toString());
                avatar.setImage(newImage);
            } else {
                System.err.println("Image file not found: " + newUserImagePath);
            }
        }
        model.updateUsers(user);

        if (manageUsersController != null) {
            manageUsersController.loadAllUsers();
            AlertUtil.showSuccessAlert("User Updated", "User information has been updated successfully.");
        } else {
            System.err.println("Parent controller is not set!");
        }

        // Load the UserEditorView FXML file
        /*FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/easv/dk/eventticketsystem/UserEditorView.fxml"));
        Parent root = fxmlLoader.load();

        // Get the UserEditorController instance from the loader
        UserEditorController editorController = fxmlLoader.getController();
        // Pass the selected user data to the editor controller
        editorController.setUserData(user);
        // pass the parent controller reference if needed
        editorController.setParentController(manageUsersController);

        // Create a new stage for the user editor
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Edit User");
        stage.show();

        if (manageUsersController != null) {
            manageUsersController.loadAllUsers();
        } else {
            System.err.println("Parent controller is not set!");
        }*/
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
