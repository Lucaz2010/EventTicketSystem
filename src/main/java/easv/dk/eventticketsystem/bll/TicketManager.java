package easv.dk.eventticketsystem.bll;



import easv.dk.eventticketsystem.dal.ITicketDAO;
import easv.dk.eventticketsystem.dal.db.TicketDAODB;

import java.io.IOException;
import java.sql.SQLException;

public class TicketManager {

    private final ITicketDAO ticketDAO = new TicketDAODB();


    private final int QRwidth=300;
    private int QRheight = QRwidth;
    private final int barcodeWidth = 600;
    private int barcodeHeight = 100 ; //

    public void createTicket(int orderId, int ticketTypeId, int eventId, int quantity) throws IOException, SQLException {
        String uniqueCode = QRBarcodeManager.generateAndSaveQRCodeAndBarcode(QRwidth, QRheight,barcodeWidth ,barcodeHeight );
        ticketDAO.createTicket(orderId, ticketTypeId, eventId, quantity, uniqueCode);
    }


    public void regenerateTicket(String uniqueCode) throws IOException {
        QRBarcodeManager.regenerateQRCodeAndBarcode(uniqueCode, QRwidth, QRheight , barcodeWidth, barcodeHeight);
    }
    public void deleteTicket(String uniqueCode) throws SQLException {

        /// Deletes uniqueCode from database
        ticketDAO.deleteTicket(uniqueCode);
        /// Deletes the barcode and qr file
        QRBarcodeManager.deleteUUIDfiles(uniqueCode);
    }

}