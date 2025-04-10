package easv.dk.eventticketsystem.bll;

import easv.dk.eventticketsystem.be.TicketType;
import easv.dk.eventticketsystem.dal.ITicketTypeDAO;
import easv.dk.eventticketsystem.dal.db.TicketTypeDAODB;

import java.util.List;


/**
 * Business logic manager for handling ticket types.
 *
 * Provides methods to create and retrieve ticket types,
 * while delegating database operations to the DAO layer.
 */

public class TicketTypeManager {

    private final ITicketTypeDAO ticketTypeDAO = new TicketTypeDAODB();


    /**
     * Creates a new ticket type in the database.
     *
     * @param ticketType The TicketType object to create (name, price, category, etc.)
     */

    public void createTicketType(TicketType ticketType) throws Exception {
        ticketTypeDAO.createTicketType(ticketType);
    }


    /**
     * Retrieves all ticket types from the database.
     */

    public List<TicketType> getAllTicketTypes() throws Exception {
        return ticketTypeDAO.getAllTicketTypes();
    }

}