package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.controllers.componentsControllers.EventCardController;

import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageEventsController implements Initializable {
    @FXML
    private Button btnCreateNewEvent;
    @FXML
    private FlowPane eventCardPane;
    @FXML
    private AnchorPane toolbarContainer;
    @FXML
    private BorderPane eventPane;

    private ToolbarController toolbarController;
    private static final EventTicketSystemModel model = new EventTicketSystemModel();
    private Users currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Manually load the toolbar FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/Toolbar.fxml"));
            AnchorPane toolbar = loader.load();
            toolbarController = loader.getController();
            // Set the parent controller reference in the toolbar controller
            toolbarController.setEventParentController(this);
            // Place the loaded toolbar into the placeholder container
            toolbarContainer.getChildren().setAll(toolbar);
            loadAllEvents();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /// use for loop to add all events by cards.
    public void loadAllEvents() throws IOException {
        eventCardPane.getChildren().clear();
        List<Event> eventList = model.getAllEvents();
        for (Event event : eventList) {
            addEventCard(event);
        }
    }

    // load a single user card.
    private void addEventCard(Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/EventCard.fxml"));
        AnchorPane eventCard = loader.load();
        EventCardController cardController = loader.getController();

        cardController.setEventData(event);
        cardController.setAuthenticatedUser(currentUser);
        eventCardPane.getChildren().add(eventCard);
    }

    //Opens window for create new event
    @FXML
    public void onClickAddEvent(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/easv/dk/eventticketsystem/CreateNewEventView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        //pass itself to the new controller
        CreateNewEventController createController = fxmlLoader.getController();
        createController.setParentController(this);

        Stage loginStage = new Stage();
        loginStage.setTitle("Create A New Event");
        loginStage.setScene(scene);
        loginStage.show();
    }

    /// Call from ToolbarController to update the view based on the search query.
    public void searchEvent(String query) {
        eventCardPane.getChildren().clear();
        try {
            ObservableList<Event> searchedEvent = model.getSearchedEvent(query);
            for (Event event : searchedEvent) {
                addEventCard(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSortEvents(ActionEvent actionEvent) {
    }

    public void setCurrentUser(Users user) {
        this.currentUser = user;

        if ("Admin".equalsIgnoreCase(user.getRole().trim())) {
            btnCreateNewEvent.setVisible(false);
        } else {
            btnCreateNewEvent.setVisible(true);
        }
        try {
            loadAllEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}