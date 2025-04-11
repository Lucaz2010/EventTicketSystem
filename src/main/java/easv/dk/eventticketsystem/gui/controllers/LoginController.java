package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.bll.UsersManager;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import easv.dk.eventticketsystem.security.PasswordUtil;
import easv.dk.eventticketsystem.security.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button btnLogin;
    @FXML
    private TextField loginEmail;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField visiblePassword;

    @FXML
    private Button btnTogglePassword;

    @FXML
    private FontIcon eyeIcon;

    private boolean passwordVisible = false;
    private List<Users> allUsers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onLoginBtnClick(ActionEvent actionEvent) throws IOException {
        String email = loginEmail.getText().trim();
        String password = loginPassword.getText();

        UsersManager usersManager = new UsersManager();
        Users user = usersManager.getUserByEmail(email);
        System.out.println("Retrieved user: " + user);

        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            // Optionally print some debug information.
            System.out.println("Stored hash: " + user.getPassword());
            System.out.println("Password match: " + true);

            // Set the user in the session.
            UserSession.setCurrentUser(user);
            String userRole = user.getRole();

            // Close the login window.
            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            currentStage.close();

            // Load the correct dashboard based on the user's role.
            loadDashboardView(userRole, email);
        } else {
            // If user is null or password doesn't match.
            System.out.println("Stored hash (if any): " + (user != null ? user.getPassword() : "null"));
            System.out.println("Password match: " + false);
            AlertUtil.showErrorAlert("Login Failed", "Invalid email or password. Please try again.");
        }
    }

    private void loadDashboardView(String role, String email) {
        try {
            FXMLLoader fxmlLoader;
            // Choose FXML based on role—adjust file paths as needed.
            if ("Admin".equalsIgnoreCase(role)) {
                fxmlLoader = new FXMLLoader(MainApplication.class.getResource("DashboardView.fxml"));
            } else if ("Coordinator".equalsIgnoreCase(role)) {
                fxmlLoader = new FXMLLoader(MainApplication.class.getResource("DashboardView.fxml"));
            } else {
                // Fall back to a generic dashboard or display an error.
                fxmlLoader = new FXMLLoader(MainApplication.class.getResource("DashboardView.fxml"));
            }
            Scene scene = new Scene(fxmlLoader.load());
            // Optionally get and set user data in the dashboard controller:
            // DashboardController controller = fxmlLoader.getController();
            // controller.setUser(user);  // if you wish to pass the user object
            Stage stage = new Stage();
            stage.setTitle("Event Ticket System");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUser(String email, String password) {
        // Replace with actual authentication logic (e.g., check from database)
        return "admin@gmail.com".equals(email) && "123456".equals(password);
    }

    private String authenticateUser(String email, String password) {
        if ("admin@gmail.com".equals(email) && "123456".equals(password)) {
            return "Admin";
        } else if ("coordinator@gmail.com".equals(email) && "123456".equals(password)) {
            return "Coordinator";
        } else if ("customer@gmail.com".equals(email) && "123456".equals(password)) {
            return "Customer";
        }
        return null; // Invalid credentials
    }


    public void onTogglePasswordVisibility(ActionEvent actionEvent) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            // Show plain text field
            visiblePassword.setText(loginPassword.getText());
            visiblePassword.setVisible(true);
            visiblePassword.setManaged(true);
            loginPassword.setVisible(false);
            loginPassword.setManaged(false);
            // Change icon to indicate visibility (for example, an "eye-slash")
            eyeIcon.setIconLiteral("bi-eye-slash");
        } else {
            // Hide plain text field, show PasswordField again
            loginPassword.setText(visiblePassword.getText());
            loginPassword.setVisible(true);
            loginPassword.setManaged(true);
            visiblePassword.setVisible(false);
            visiblePassword.setManaged(false);
            // Change icon back to "eye"
            eyeIcon.setIconLiteral("bi-eye");
        }
    }

    }

