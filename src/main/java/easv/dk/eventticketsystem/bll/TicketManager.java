package easv.dk.eventticketsystem.bll;



import easv.dk.eventticketsystem.dal.ITicketDAO;
import easv.dk.eventticketsystem.dal.db.TicketDAODB;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles ticket-related business logic.
 * Responsible for creating, deleting, and regenerating tickets.
 * Delegates database access to ITicketDAO and uses QRBarcodeManager
 * to handle QR code and barcode image files.
 */

public class TicketManager {

    private final ITicketDAO ticketDAO = new TicketDAODB();

    // Set size for generated QR codes and barcodes

    private final int QRwidth=300;
    private int QRheight = QRwidth;
    private final int barcodeWidth = 600;
    private int barcodeHeight = 100 ; //


    /**
     * Creates a new ticket:
     * - Generates a unique code
     * - Creates corresponding QR and barcode image files
     * - Saves ticket data to the database
     *
     * @param orderId      ID of the order to attach the ticket to
     * @param ticketTypeId The selected ticket type
     * @param eventId      The event this ticket is for
     * @param quantity     Quantity (e.g., for group bookings or vouchers)
     */

    public void createTicket(int orderId, int ticketTypeId, int eventId, int quantity) throws IOException, SQLException {
        String uniqueCode = QRBarcodeManager.generateAndSaveQRCodeAndBarcode(QRwidth, QRheight,barcodeWidth ,barcodeHeight );
        ticketDAO.createTicket(orderId, ticketTypeId, eventId, quantity, uniqueCode);
    }


    /**
     * Re-generates the QR code and barcode images for a ticket using its unique code.
     * Useful if the original image files were deleted or corrupted.
     */

    public void regenerateTicket(String uniqueCode) throws IOException {
        QRBarcodeManager.regenerateQRCodeAndBarcode(uniqueCode, QRwidth, QRheight , barcodeWidth, barcodeHeight);
    }


    /**
     * Deletes a ticket:
     * - Removes the record from the database
     * - Deletes the associated QR code and barcode image files
     *
     * @param uniqueCode The unique string identifier used for both images and the database
     */

    public void deleteTicket(String uniqueCode) throws SQLException {

        /// Deletes uniqueCode from database
        ticketDAO.deleteTicket(uniqueCode);
        /// Deletes the barcode and qr file
        QRBarcodeManager.deleteUUIDfiles(uniqueCode);
    }

}