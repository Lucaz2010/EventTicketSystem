package easv.dk.eventticketsystem.dal.db;

import easv.dk.eventticketsystem.be.TicketOnOrder;
import easv.dk.eventticketsystem.dal.ITicketOnOrderDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketOnOrderDAODB implements ITicketOnOrderDAO {
    private DBConnection con = new DBConnection();


    ///
    @Override
    //This method gets all order details with status = "pending"
    public List<TicketOnOrder> getAllOrderDetails() {
        List<TicketOnOrder> orderDetails = new ArrayList<>();
        String sql = """
                   SELECT
                    o.order_id,
                    c.customer_name,
                    c.customer_email,
                    e.event_name,
                    e.start_datetime,
                    e.location,
                    t.quantity,
                    t.ticket_id AS ticket_id,
                    tt.type_name AS ticket_type,
                    tt.price AS ticket_price,
                    t.unique_code AS code
                   
                FROM Orders o
                JOIN Customer c ON o.customer_id = c.customer_id
                LEFT JOIN Ticket t ON t.order_id = o.order_id
                LEFT JOIN Event e ON t.event_id = e.event_id
                LEFT JOIN Ticket_Type tt ON tt.ticket_type_id = t.ticket_type_id
                WHERE o.status = 'Pending'
                ORDER BY o.order_id DESC               
                """;
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String customerName = rs.getString("customer_name");
                String customerEmail = rs.getString("customer_email");
                String eventName = rs.getString("event_name");

                int ticketId = rs.getInt("ticket_id");
//                Integer ticketId = (Integer) rs.getObject("ticket_id");
                String ticketType = rs.getString("ticket_type");
                String code = rs.getString("code");
                String startDateTime = rs.getString("start_datetime");
                String location = rs.getString("location");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("ticket_price");

                System.out.println("Retrieving order id: " + orderId);

                LocalDateTime dateTime = null;
                String eventDate = "";
                String eventTime = "";

                if (startDateTime != null && startDateTime.contains(" ")) {
                    try {
                        dateTime = LocalDateTime.parse(startDateTime.replace(" ", "T"));
                        eventDate = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        eventTime = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    } catch (Exception e) {
                        System.err.println("⚠️ Failed to parse datetime for order ID " + orderId + ": " + startDateTime);
                        eventDate = "(Invalid Date)";
                        eventTime = "(Invalid Time)";
                    }
                } else {
                    eventDate = "(No Date)";
                    eventTime = "(No Time)";
                }


                TicketOnOrder ticketOnOrder = new TicketOnOrder(orderId, customerName, customerEmail, eventName, ticketId, ticketType, code, eventDate, eventTime, location,quantity,price);
                orderDetails.add(ticketOnOrder);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    ///
    public List<TicketOnOrder> getAllPendingOrdersWithoutTickets() {
        List<TicketOnOrder> orderList = new ArrayList<>();

        String sql = """
        SELECT o.order_id, c.customer_name, c.customer_email
        FROM Orders o
        JOIN Customer c ON o.customer_id = c.customer_id
        WHERE o.status = 'Pending'
        ORDER BY o.order_id DESC
        """;

        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String customerName = rs.getString("customer_name");
                String customerEmail = rs.getString("customer_email");

                TicketOnOrder baseOrder = new TicketOnOrder(
                        orderId,
                        customerName,
                        customerEmail,
                        "",      // eventName
                        -1,      // ticketId
                        "",      // ticketType
                        "",      // code
                        "", "",  // date + time
                        "",      // location
                        0,       // quantity
                        0.0      // price
                );

                orderList.add(baseOrder);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }


    public List<TicketOnOrder> getTicketByOrderId(int orderId){

        List<TicketOnOrder> tickets = new ArrayList<>();

        String sql = """
                SELECT o.order_id, 
                c.customer_name,
                c.customer_email,
                e.event_name,
                e.start_datetime,
                e.location,
                t.quantity,
                t.ticket_id AS ticket_id,
                tt.type_name AS ticket_type,
                tt.price AS ticket_price,
                t.unique_code AS code
                
                FROM Orders o
                JOIN Customer c ON o.customer_id = c.customer_id
                LEFT JOIN Ticket t ON t.order_id = o.order_id
                LEFT JOIN Event e ON t.event_id = e.event_id
                LEFT JOIN Ticket_Type tt ON tt.ticket_type_id = t.ticket_type_id
                WHERE o.order_id = ?
                ORDER BY t.ticket_id DESC
                """;


        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1,orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                int orderId_db = rs.getInt("order_id");
                String customerName = rs.getString("customer_name");
                String customerEmail = rs.getString("customer_email");
                String eventName = rs.getString("event_name");

                int ticketId = rs.getInt("ticket_id");
                String ticketType = rs.getString("ticket_type");
                String code = rs.getString("code");
                String startDateTime = rs.getString("start_datetime");
                String location = rs.getString("location");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("ticket_price");

                LocalDateTime dateTime = LocalDateTime.parse(startDateTime.replace(" ", "T"));
                String eventDate = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String eventTime = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

                TicketOnOrder ticketOnOrder = new TicketOnOrder(
                        orderId_db,
                        customerName,customerEmail,eventName,ticketId,ticketType,code,eventDate,eventTime,location,quantity,price
                        );



                tickets.add(ticketOnOrder);
            }


        } catch (SQLException e) {
        e.printStackTrace();
    }

        return tickets;
    }



}


    ///  Loads a TicketOnOrder even without a ticket = fixes the UI Problem of disapearring order name/email without tickets after saving
//                if(rs.getObject("ticket_id") == null){
//                    TicketOnOrder ticketOnOrder = new TicketOnOrder(orderId, customerName, customerEmail, "No Ticket Added - event", 0, "No Ticket Added - ticket Type", "No qr code ", "No event date",
//                            "No event time", "No event location",0,0);

//                ///  Loads a TicketOnOrder even without a ticket_id created
//                if(rs.getObject("ticket_id") == null){
//                    TicketOnOrder ticketOnOrder = new TicketOnOrder(orderId, customerName, customerEmail, eventName,  rs.getInt("ticket_id"), ticketType, code, eventDate, eventTime, location,quantity,price);
//
//                    orderDetails.add(ticketOnOrder);
//                } else{

    ///  PRoblem: this old method bugs the UI since loading Orders that have tickets, conflicts with creating order since we want to insert a new customer name + email
    /// When clicking save it checks if theres no ticket so it disappears.

