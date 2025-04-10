package easv.dk.eventticketsystem.be;

import java.time.LocalDate;
import java.util.List;

public class Order {
    private final int id;
    private int customerId;
    private int eventId;
    private LocalDate orderDate;
    private String status;
    private List<Ticket> tickets;

    public Order(int id, int customerId, int eventId, LocalDate orderDate, String status, List<Ticket> tickets) {
        this.id = id;
        this.customerId = customerId;
        this.eventId = eventId;
        this.orderDate = orderDate;
        this.status = status;
        this.tickets = tickets;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getEventId() { return eventId; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    @Override
    public String toString() {
        return id + "," + customerId + "," + eventId + "," + orderDate + "," + status + "," + tickets;
    }
}
