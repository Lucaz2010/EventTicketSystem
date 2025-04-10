package easv.dk.eventticketsystem.bll;

import easv.dk.eventticketsystem.be.TicketOnOrder;
import easv.dk.eventticketsystem.dal.ITicketOnOrderDAO;
import easv.dk.eventticketsystem.dal.db.TicketOnOrderDAODB;

import java.util.List;


/**
 * Business logic manager for retrieving ticket information related to orders.
 */

public class TicketOnOrderManager {
    private final ITicketOnOrderDAO ticketOnOrderDAO = new TicketOnOrderDAODB();


    /**
     * Retrieves all ticket details for all orders.
     * @return A list of TicketOnOrder objects.
     */

    public List<TicketOnOrder> getAllOrderDetails() {
        return ticketOnOrderDAO.getAllOrderDetails();
    }

    /**
     * Retrieves all tickets associated with a specific order.
     * @return A list of TicketOnOrder objects tied to the given order.
     */

    public List<TicketOnOrder> getTicketByOrderId(int orderId) {
        return ticketOnOrderDAO.getTicketByOrderId(orderId);
    }

    /**
     * Checks if a specific order contains any tickets.
     * Useful for validating whether an order is empty before actions like confirmation.
     * @return true if the order has one or more tickets, false otherwise.
     */

    public boolean orderHasTickets (int orderId){
        return ticketOnOrderDAO.orderHasTickets(orderId);
    }
}
