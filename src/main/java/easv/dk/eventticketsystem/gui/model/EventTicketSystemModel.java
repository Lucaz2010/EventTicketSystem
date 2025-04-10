package easv.dk.eventticketsystem.gui.model;

import easv.dk.eventticketsystem.be.*;
import easv.dk.eventticketsystem.bll.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

import java.io.IOException;

import java.util.List;

/**
 * The main model class used by the GUI to interact with the business logic layer.
 *
 * Acts as a facade between controllers and the underlying managers (BLL),
 * providing observable lists for UI binding and methods to perform system actions.
 */

public class EventTicketSystemModel {


    /// ---------------------- TicketOnOrder ----------------------///
    private final TicketOnOrderManager ticketOnOrderManager = new TicketOnOrderManager();

    private final ObservableList<TicketOnOrder> ticketOnOrders = FXCollections.observableArrayList();

    public ObservableList<TicketOnOrder> getAllOrderDetails() {
        List<TicketOnOrder> orderDetails = ticketOnOrderManager.getAllOrderDetails();
        ticketOnOrders.setAll(orderDetails);
        return ticketOnOrders;
    }

    public List<TicketOnOrder> getTicketByOrderId(int orderId){

        return ticketOnOrderManager.getTicketByOrderId(orderId);

    }

    public boolean orderHasTickets (int orderId){
        return ticketOnOrderManager.orderHasTickets(orderId);
    }

    /// ---------------------- Order Management ----------------------///
    private final OrderManager orderManager = new OrderManager();

    public int createOrder(int customerId) throws Exception {
        return orderManager.createOrder(customerId);
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

    public void updateOrderCustomer(int orderId, int customerId) {
        try {
            orderManager.updateOrderCustomer(orderId, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /// ---------------------- Ticket Management ----------------------///
    private final TicketManager ticketManager = new TicketManager();


    public void deleteTicket(String code) throws SQLException {
        ticketManager.deleteTicket(code);
    }
    public void regenerateTicket(String code) throws SQLException, IOException {
        ticketManager.regenerateTicket(code);
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    /// ---------------------- Ticket Type Management ----------------------///

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




    /// ---------------------- Customer Management ----------------------///
    private final CustomerManager customerManager = new CustomerManager();

    public int getOrCreateCustomerId(String name, String email) throws Exception {
        return customerManager.getOrCreateCustomerId(name, email);
    }





    public Customer getCustomerByEmail(String email) throws Exception {
        return customerManager.getCustomerByEmail(email);
    }

    /// ---------------------- User Management ----------------------///


    private final UsersManager usersManager = new UsersManager();
    private final ObservableList<Users> allUsers = FXCollections.observableArrayList();
    private final ObservableList<Users> searchedUsers = FXCollections.observableArrayList();


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

    /// ---------------------- Event Management ---------------------- ///
    private final EventManager eventManager = new EventManager();
    private final ObservableList<Event> allEvents = FXCollections.observableArrayList();
    private final ObservableList<Event> searchedEvent = FXCollections.observableArrayList();

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

    // Get observableList of searched users
    public ObservableList<Users> getSearchedUsers(String query) throws IOException {
        List<Users> searchResults = usersManager.searchUsers(query);
        searchedUsers.setAll(searchResults);
        return searchedUsers;
    }

    // Get observableList of searched event
    public ObservableList<Event> getSearchedEvent(String query) throws IOException {
        List<Event> searchResults = eventManager.searchEvent(query);
        searchedEvent.setAll(searchResults);
        return searchedEvent;
    }
}