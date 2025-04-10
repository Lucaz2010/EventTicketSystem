package easv.dk.eventticketsystem.dal.db;

import java.sql.*;

/**
 * DAO class responsible for low-level database operations related to orders.

 * This class uses raw SQL
 */

public class OrderDAODB {
    private DBConnection con = new DBConnection();

    /**
     * Inserts a new order into the database for the given customer.
     * The order status is set to 'Pending' by default.
     *
     * @param customerId ID of the customer placing the order
     * @return The generated order ID, or -1 if the insert failed
     */


    public int createOrder(int customerId) throws SQLException {
        String sql = "INSERT INTO Orders (customer_id, status) VALUES (?, 'Pending')";
        try (Connection conn = con.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Deletes an order and its associated tickets from the database.
     *
     * @param orderId ID of the order to delete
     */


    public void deleteOrder(int orderId) throws SQLException {

        try (Connection conn = con.getConnection()){

            // First delete tickets related to the order

        String deleteTicketsSql = "DELETE FROM Ticket WHERE order_id = ?";
            try (PreparedStatement psTickets = conn.prepareStatement(deleteTicketsSql)) {
            psTickets.setInt(1, orderId);
            psTickets.executeUpdate();
        }
            // Then delete the order itself

        String deleteOrderSql = "DELETE FROM Orders WHERE order_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(deleteOrderSql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
        }
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId The order to update
     * @param status  The new status (e.g., "Confirmed", "Pending")
     */

    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        try (Connection conn = con.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }


    /**
     * Changes the customer assigned to an order.
     *
     * @param orderId    The order to update
     * @param customerId The new customer ID
     */

    public void updateCustomerId(int orderId, int customerId) throws SQLException {
        String sql = "UPDATE Orders SET customer_id = ? WHERE order_id = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

}
