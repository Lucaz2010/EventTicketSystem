package easv.dk.eventticketsystem.dal;

import easv.dk.eventticketsystem.be.TicketOnOrder;

import java.util.List;

public interface ITicketOnOrderDAO {
    List<TicketOnOrder> getAllOrderDetails();
    List<TicketOnOrder> getTicketByOrderId(int orderId);
}
