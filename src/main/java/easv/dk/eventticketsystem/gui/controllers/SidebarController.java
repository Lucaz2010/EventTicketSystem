package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML
    private Button btnLogout;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ManageUsersView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Users Management");
        loginStage.setScene(scene);
        loginStage.show();
    }

    public void onManageEventsClick(ActionEvent actionEvent) throws IOException {
        Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ManageEventsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
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
