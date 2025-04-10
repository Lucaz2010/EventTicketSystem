package easv.dk.eventticketsystem.dal.db;

import easv.dk.eventticketsystem.be.TicketType;
import easv.dk.eventticketsystem.dal.ITicketTypeDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for TicketType.
 *
 * Responsible for low-level database operations related to ticket types.
 * Used by the TicketTypeManager (BLL) to create and retrieve ticket types.
 */

public class TicketTypeDAODB implements ITicketTypeDAO {

    private final DBConnection con = new DBConnection();

    /**
     * Inserts a new ticket type into the database.
     * Sets the generated ID back into the TicketType object.
     *
     * @param ticketType The TicketType to insert
     * @return The same TicketType object with its ID populated
     */

    @Override
    public TicketType createTicketType(TicketType ticketType) throws Exception {
        String sql = "INSERT INTO Ticket_Type (type_name, type_category, price) VALUES (?, ?, ?)";
        try (Connection conn = con.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticketType.getTypeName());
            ps.setString(2, ticketType.getCategory());
            ps.setDouble(3, ticketType.getPrice());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                ticketType.setTicketTypeId(id); // 🟢 Set the ID back
            }
        }
        return ticketType;
    }

    /**
     * Retrieves all ticket types from the database. */

    @Override
    public List<TicketType> getAllTicketTypes() throws Exception {
        List<TicketType> list = new ArrayList<>();
        String sql = "SELECT * FROM Ticket_Type";

        try (Connection conn = con.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ticket_type_id");
                String name = rs.getString("type_name");
                String category = rs.getString("type_category");
                double price = rs.getDouble("price");

                list.add(new TicketType(id, name, category, price));
            }
        }
        return list;
    }
}