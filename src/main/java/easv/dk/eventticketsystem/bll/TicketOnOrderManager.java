package easv.dk.eventticketsystem.bll;

import easv.dk.eventticketsystem.be.TicketOnOrder;
import easv.dk.eventticketsystem.dal.ITicketOnOrderDAO;
import easv.dk.eventticketsystem.dal.db.TicketOnOrderDAODB;

import java.util.List;

public class TicketOnOrderManager {
    private final ITicketOnOrderDAO ticketOnOrderDAO = new TicketOnOrderDAODB();

    public List<TicketOnOrder> getAllOrderDetails() {
        return ticketOnOrderDAO.getAllOrderDetails();
    }

    public List<TicketOnOrder> getTicketByOrderId(int orderId) {
        return ticketOnOrderDAO.getTicketByOrderId(orderId);
    }
}
