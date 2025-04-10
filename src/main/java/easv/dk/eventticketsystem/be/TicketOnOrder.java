package easv.dk.eventticketsystem.be;

public class TicketOnOrder {
    private int orderId;
    private String customerName;
    private String customerEmail;
    private String eventName;
    private int ticketId;
    private String ticketType;
    private String code;
    private String eventDate;
    private String eventTime;
    private String location;
    private int quantity;
    private double price;


    public TicketOnOrder(int orderId, String customerName, String customerEmail, String eventName, int ticketId, String ticketType, String code,String eventDate, String eventTime, String location, int quantity, double price) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.eventName = eventName;
        this.ticketId = ticketId;
        this.ticketType = ticketType;
        this.code = code;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.quantity = quantity;
        this.price = price;

    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity(){return quantity;}

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getEventName() {
        return eventName;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public String getCode() {
        return code;
    }
    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getLocation() {
        return location;
    }


    @Override
    public String toString() {
        return "TicketOnOrder{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", eventName='" + eventName + '\'' +
                ", ticketId=" + ticketId +
                ", ticketType='" + ticketType + '\'' +
                ", code='" + code + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}