package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.security.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML
    private Button btnManageOrders;
    @FXML
    private Button btnLogout;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button eventsButton;
    @FXML
    private Button ticketsButton;
    @FXML
    private Button logoutButton;

    private Users authenticatedUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Users user = UserSession.getCurrentUser();
        if (user != null && "Admin".equalsIgnoreCase(user.getRole().trim())) {
            btnManageOrders.setVisible(false);
        } else {
            btnManageOrders.setVisible(true);
        }
    }

    public void setAuthenticatedUser(Users user) {
        this.authenticatedUser = user;
        if (user != null && "Admin".equalsIgnoreCase(user.getRole().trim())) {
            btnManageOrders.setVisible(false);
        } else {
            btnManageOrders.setVisible(true);
        }
    }

    public void onManageOrdersClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ManageOrdersView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Orders Management");
        loginStage.setScene(scene);
        loginStage.show();
    }

    public void onDashboardClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("DashboardView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Welcome to the Dashboard");
        loginStage.setScene(scene);
        loginStage.show();
    }

    public void onManageUsersClick(ActionEvent actionEvent) throws IOException {
        Users user = UserSession.getCurrentUser();
        if (user == null) {
            System.err.println("No authenticated user found. Please log in again.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/ManageUsersView.fxml"));
        Parent root = loader.load();
        ManageUsersController manageUsersController = loader.getController();
        manageUsersController.setCurrentUser(user);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Manage Users");
        stage.show();

    }

    public void onManageEventsClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
        Users user = UserSession.getCurrentUser();
        if (user == null) {
            System.err.println("No authenticated user found. Please log in again.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ManageEventsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the controller and pass the authenticated user
        ManageEventsController manageEventsController = fxmlLoader.getController();
        manageEventsController.setCurrentUser(user);

        Stage loginStage = new Stage();
        loginStage.setTitle("Events Management");
        loginStage.setScene(scene);
        loginStage.show();
    }

    public void onManageTicketsClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ManageTicketsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Tickets Management");
        loginStage.setScene(scene);
        loginStage.show();
    }

    public void onLogout(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }

    public void onManageCustomersClick(ActionEvent actionEvent) {

    }
}
