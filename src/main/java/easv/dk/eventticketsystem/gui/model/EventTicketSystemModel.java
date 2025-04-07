package easv.dk.eventticketsystem.gui.model;

import easv.dk.eventticketsystem.be.*;
import easv.dk.eventticketsystem.bll.*;
import easv.dk.eventticketsystem.gui.controllers.ManageEditWindow;
import easv.dk.eventticketsystem.dal.db.OrderDAODB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventTicketSystemModel {
    private final TicketOnOrderManager ticketOnOrderManager = new TicketOnOrderManager();
    private final UsersManager usersManager = new UsersManager();
    private final EventManager eventManager = new EventManager();
    private final ObservableList<TicketOnOrder> ticketOnOrders = FXCollections.observableArrayList();
    private final ObservableList<Users> allUsers = FXCollections.observableArrayList();
    private final ObservableList<Event> allEvents = FXCollections.observableArrayList();
    private final ObservableList<Users> searchedUsers = FXCollections.observableArrayList();

    /// Ticket functions
    private final TicketManager ticketManager = new TicketManager();
    public TicketManager getTicketManager() {
        return ticketManager;
    }

    /// Ticket Type functions
    private final TicketTypeManager ticketTypeManager = new TicketTypeManager();
    private final ObservableList<TicketType> allTicketTypes = FXCollections.observableArrayList();

    public void createTicketType(TicketType ticketType) {
        try {
            ticketTypeManager.createTicketType(ticketType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<TicketType> getAllTicketTypes() {
        try {
            List<TicketType> types = ticketTypeManager.getAllTicketTypes();
            allTicketTypes.setAll(types);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allTicketTypes;
    }

    /// Orders functions
    private final OrderManager orderManager = new OrderManager();

    public ObservableList<TicketOnOrder> getAllOrderDetails() {
        List<TicketOnOrder> orderDetails = ticketOnOrderManager.getAllOrderDetails();
        ticketOnOrders.setAll(orderDetails);
        return ticketOnOrders;
    }
    public boolean orderHasTickets (int orderId){
        List<TicketOnOrder> allTickets = getAllOrderDetails();
        for (TicketOnOrder ticket : allTickets){
            if (ticket.getOrderId() == orderId){
                return  true;
            }
        }
        return false;
    }

    public int getNextOrderId() {
        return orderManager.getNextOrderId();
    }

    public void confirmOrder(int orderId) {
        try {
            orderManager.updateOrderStatus(orderId, "Confirmed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int orderId) {
        try {
            orderManager.deleteOrder(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /// Customer functions
    private final CustomerManager customerManager = new CustomerManager();

    public int getOrCreateCustomerId(String name, String email) throws Exception {
        return customerManager.getOrCreateCustomerId(name, email);
    }

    public int createOrder(int customerId) throws Exception {
        return orderManager.createOrder(customerId);
    }

    public void updateOrderCustomer(int orderId, int customerId) {
        try {
            orderManager.updateOrderCustomer(orderId, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Customer getCustomerByEmail(String email) throws Exception {
        return customerManager.getCustomerByEmail(email);
    }

    /// User functions
    public ObservableList<Users> getAllUsers() throws IOException {
        List<Users> usersList = usersManager.getAllUsers();
        allUsers.setAll(usersList);
        return allUsers;
    }

    public void createNewUsers(Users users) throws IOException {
        usersManager.createNewUsers(users);
    }

    public void deleteUsers(Users users) throws IOException {
        usersManager.deleteUsers(users);
    }

    public void updateUsers(Users users) throws IOException {
        usersManager.updateUsers(users);
    }

    /// Event functions
    public ObservableList<Event> getAllEvents() throws IOException {
        List<Event> eventList = eventManager.getAllEvents();
        allEvents.setAll(eventList);
        return allEvents;
    }
    public void createNewEvent(Event newEvent) throws IOException {
        eventManager.createNewEvent(newEvent);
    }

    public void deleteEvent(Event event) throws IOException {
        eventManager.deleteEvent(event);

    }

    public void updateEvent(Event selectedEvent) throws IOException {
        eventManager.updateEvent(selectedEvent);
    }

    /// Get ovservableList of searched users
    public ObservableList<Users> getSearchedUsers(String query) throws IOException {
        List<Users> searchResults = usersManager.searchUsers(query);
        searchedUsers.setAll(searchResults);
        return searchedUsers;
    }
}