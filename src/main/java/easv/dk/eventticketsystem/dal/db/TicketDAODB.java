package easv.dk.eventticketsystem.dal.db;

import easv.dk.eventticketsystem.dal.ITicketDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO implementation for Ticket-related database operations.

 * This class handles raw SQL queries related to ticket creation and deletion.
 * It is used by the business logic layer (TicketManager) to interact with the Ticket table.
 */

public class TicketDAODB implements ITicketDAO {
    private final DBConnection con = new DBConnection();


    /**
     * Inserts a new ticket into the database.
     *
     * @param orderId      The ID of the associated order
     * @param ticketTypeId The ID of the selected ticket type
     * @param eventId      The ID of the event the ticket belongs to
     * @param quantity     How many tickets were purchased
     * @param uniqueCode   A unique string (UUID) used to generate QR/barcode
     */
    @Override
    public void createTicket(int orderId, int ticketTypeId,int eventId,int quantity, String uniqueCode) {

        try {


            String sql = "INSERT INTO Ticket (order_id, ticket_type_id, event_id, unique_code , quantity) VALUES (?, ?, ?, ?,?)";
            try (Connection conn = con.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, ticketTypeId);
                ps.setInt(3, eventId);
                ps.setString(4, uniqueCode);
                ps.setInt(5, quantity);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * Deletes a ticket from the database using its unique code.
     */
    public void deleteTicket(String uniqueCode) {

        String sql = "DELETE FROM Ticket WHERE unique_code = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uniqueCode);
            int rows = ps.executeUpdate();
            System.out.println("🗑️ Deleted ticket with code: " + uniqueCode);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}