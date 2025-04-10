package easv.dk.eventticketsystem.bll;
import easv.dk.eventticketsystem.dal.db.OrderDAODB;

import java.sql.SQLException;


/**
 * Business logic class responsible for handling order-related operations.

 * This class is part of the BLL (Business Logic Layer) and is used by the
 * EventTicketSystemModel to interact with orders in a clean and centralized way.

 * It delegates data operations to OrderDAODB (DAL) while exposing simple methods
 * for use by the UI/controller layer.
 */
public class OrderManager {

    private final OrderDAODB orderDAO = new OrderDAODB();



    /**
     * Updates the status of a specific order (e.g., "Pending", "Confirmed").
     * @param orderId The ID of the order to update
     * @param status  The new status to set
     */

    public void updateOrderStatus(int orderId, String status) throws SQLException {
        orderDAO.updateOrderStatus(orderId, status);
    }

    /**
     * Creates a new order in the database for a given customer.
     * @param customerId The ID of the customer placing the order
     * @return The ID of the newly created order
     */

    public int createOrder(int customerId) throws Exception{
        return orderDAO.createOrder(customerId);
    }

    /**
     * Deletes an order from the database based on its ID.
     * @param orderId The ID of the order to delete
     */

    public void deleteOrder(int orderId) throws SQLException {
        orderDAO.deleteOrder(orderId);
    }

    /**
     * Updates the customer associated with a specific order.
     * @param orderId    The order to modify
     * @param customerId The new customer ID to assign
     */

    public void updateOrderCustomer(int orderId, int customerId) throws SQLException {
        orderDAO.updateCustomerId(orderId, customerId);
    }


}
