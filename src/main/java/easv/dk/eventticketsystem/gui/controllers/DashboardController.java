package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.security.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label lblUserName;
    @FXML
    private StackPane carouselPane;
    @FXML
    private ImageView carouselImageView;
    @FXML
    private ImageView eventImageView;
    @FXML
    private Label dashboardLabel;
    @FXML
    private LineChart salesChart;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnManageUsers;    // Admin only
    @FXML
    private Button btnManageEvents;   // Event Coordinator
    @FXML
    private Button btnPrintTickets;   // Event Coordinator
    @FXML
    private Button btnMyTickets;      // Customer
    @FXML
    private StackPane contentPane;

    private String userRole; // Set this dynamically (Admin, Coordinator)
    private String userEmail;

    private LoginController loginController;
    @FXML
    private SidebarController sidebarController;

    private List<Image> eventImages = new ArrayList<>();
    private int currentIndex = 0;
    private Users authenticatedUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardView();
        Users loggedUser = UserSession.getCurrentUser();
        if (loggedUser != null) {
            lblUserName.setText("Hi, " + loggedUser.getUserName() + "!");
            sidebarController.setAuthenticatedUser(loggedUser);
        } else {
            lblUserName.setText("Welcome, Guest!");
        }
    }

    private void loadDashboardView() {
        // Load event images
        eventImages.add(new Image(getClass().getResourceAsStream("/dashboardImg/concert.jpg")));
        eventImages.add(new Image(getClass().getResourceAsStream("/dashboardImg/schoolParty.jpg")));
        eventImages.add(new Image(getClass().getResourceAsStream("/dashboardImg/wineTasting.jpg")));

        // Check that the list is not empty to avoid division by zero
        if (eventImages.isEmpty()) {
//            System.err.println("No event images loaded. Please check the resource paths.");
            return;
        }

        // Set the first image
        carouselImageView.setImage(eventImages.get(0));

        // Create a timeline to change the image every 6 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> {
            // Fade out current image
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), carouselImageView);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                // Cycle to the next image safely
                currentIndex = (currentIndex + 1) % eventImages.size();
                carouselImageView.setImage(eventImages.get(currentIndex));

                // Fade in new image
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), carouselImageView);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
}

    /*private void loadDashboardView() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ticket Sales");

        // Use month names for the x-axis categories
        series.getData().add(new XYChart.Data<>("January", 100));
        series.getData().add(new XYChart.Data<>("February", 120));
        series.getData().add(new XYChart.Data<>("March", 80));
        series.getData().add(new XYChart.Data<>("April", 150));
        series.getData().add(new XYChart.Data<>("May", 200));
        series.getData().add(new XYChart.Data<>("June", 180));
        series.getData().add(new XYChart.Data<>("July", 220));
        series.getData().add(new XYChart.Data<>("August", 210));
        series.getData().add(new XYChart.Data<>("September", 190));
        series.getData().add(new XYChart.Data<>("October", 230));
        series.getData().add(new XYChart.Data<>("November", 170));
        series.getData().add(new XYChart.Data<>("December", 250));
        salesChart.getData().add(series);
    }*/


    public void setAuthenticatedUser(Users user) {
        this.authenticatedUser = user;
        // Optionally, update dashboard UI based on the user (e.g., display name, photo, etc.)
        setUserRole(user.getRole(), user.getUserEmail());
        // Now that authenticatedUser is set, update the sidebar controller.
        if (sidebarController != null) {
            sidebarController.setAuthenticatedUser(user);
        }
    }
    public void setParentController(LoginController parentController) {
        this.loginController = parentController;
    }
    public void setUserRole(String role, String email) {
        this.userRole = role;
        this.userEmail = email;
        checkUserRole();
    }

    private void checkUserRole() {
        if ("Admin".equals(userRole) && "admin@gmail.com".equals(userEmail)) {
            btnDashboard.setVisible(true);
            btnManageUsers.setVisible(true);
            btnManageEvents.setVisible(false);
            btnPrintTickets.setVisible(false);
            btnMyTickets.setVisible(false);
        } else if ("Coordinator".equals(userRole)) {
            btnDashboard.setVisible(true);
            btnManageUsers.setVisible(false);
            btnManageEvents.setVisible(true);
            btnPrintTickets.setVisible(true);
            btnMyTickets.setVisible(false);
        } else if ("Customer".equals(userRole)) {
            btnDashboard.setVisible(true);
            btnManageUsers.setVisible(false);
            btnManageEvents.setVisible(false);
            btnPrintTickets.setVisible(false);
            btnMyTickets.setVisible(true);
        }
    }

}
