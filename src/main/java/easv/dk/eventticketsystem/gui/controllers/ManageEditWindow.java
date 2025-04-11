package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.gui.controllers.componentsControllers.EventCardController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageEditWindow implements Initializable {
    @FXML
    private FlowPane eventCardPane;
    private static final EventTicketSystemModel model = new EventTicketSystemModel();
    public static ManageEditWindow manageEditWindowInstance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (eventCardPane == null) {
            System.out.println("DEBUG: eventCardPane is NULL in initialize()");
        } else {
            System.out.println("DEBUG: eventCardPane is initialized");
        }
    }

    public void setEventCardPane(FlowPane eventCardPane) {
        this.eventCardPane = eventCardPane;
    }

    public FlowPane getEventCardPane() {
        return eventCardPane;
    }

    private ManageEditWindow getManageEditWindowInstance() {
        if (manageEditWindowInstance == null) {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("ManageEventView.fxml"));
            try {
                AnchorPane root = loader.load();
                manageEditWindowInstance = (ManageEditWindow) loader.getController();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return manageEditWindowInstance;
    }

    public void loadAllEvents() throws IOException {
        if (eventCardPane == null) {
            System.out.println("ERROR: eventCardPane is NULL when trying to load events!");
            return;  // Prevents crashing
        }
        eventCardPane.getChildren().clear();

        List<Event> eventList = model.getAllEvents();
        if (eventList == null || eventList.isEmpty()) {
            System.out.println("ERROR: No event found in load.");
            return;
        }
        for (Event event : eventList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/EventCard.fxml"));
                AnchorPane card = loader.load();

                EventCardController cardController = loader.getController();
                if (cardController != null) {
                    cardController.setEventData(event);
                } else {
                    System.out.println("ERROR: EventCard2Controller is NULL!");
                }
                eventCardPane.getChildren().add(card);
            } catch (IOException ex) {
                System.out.println("ERROR: Failed to load EventCard2Controller!.fxml");
                ex.printStackTrace();
            }
        }
    }
}