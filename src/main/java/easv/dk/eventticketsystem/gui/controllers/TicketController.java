package easv.dk.eventticketsystem.gui.controllers;

import com.itextpdf.text.Rectangle;
import easv.dk.eventticketsystem.be.TicketOnOrder;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;


public class TicketController {


    @FXML private ImageView barCodeImageView;
    @FXML private Label lblEventName;
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblPrice;
    @FXML private Label lblLocation;
    @FXML private Label lblQuantity;
    @FXML
    private ImageView qrCodeImageView;
    @FXML Button btnPrintPDF;
    @FXML private AnchorPane ticketpane;



    @FXML
    public void initialize() {

        System.out.println("Super duper cool ticket displaying!");
    }
    public void setTicketData(TicketOnOrder ticket, String qrFilePath, String barcodePath) {

        lblEventName.setText(ticket.getEventName());

        lblDate.setText(ticket.getEventDate());
        lblTime.setText(ticket.getEventTime());

        lblQuantity.setText("Qty: " + ticket.getQuantity());
        lblPrice.setText("DKK " + String.format("%.0f", ticket.getPrice()));
        lblLocation.setText(ticket.getLocation());


        // Set QR image
        File qrFile = new File(qrFilePath);
        if (qrFile.exists()) {
            Image qrImage = new Image(qrFile.toURI().toString());
            qrCodeImageView.setImage(qrImage);
            System.out.println("QR sucessfully loaded from: " + qrFilePath);

        } else {
            System.out.println("QR code image not found at: " + qrFilePath);
        }

        // Set barcode image
        File barcodeFile  = new File(barcodePath);
        if (barcodeFile .exists()) {
            Image barcodeImage = new Image(barcodeFile .toURI().toString());
            barCodeImageView.setImage(barcodeImage);

            System.out.println("Barcode loaded from: " + barcodePath);

        } else {
            System.out.println("Barcode image not found at: " + barcodePath);
        }
    }

    public void hidePrintButton() {
        btnPrintPDF.setVisible(false);
    }

    @FXML
    private void onPrintToPDFClick() {
        try {
            btnPrintPDF.setVisible(false); // Hide button before snapshot

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Ticket PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);
            if (file == null) return;

            // Snapshot the AnchorPane using its current size
            double paneWidth = ticketpane.getWidth();
            double paneHeight = ticketpane.getHeight();

            WritableImage snapshot = new WritableImage((int) paneWidth, (int) paneHeight);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.TRANSPARENT); // use transparent or Color.WHITE if needed
            ticketpane.snapshot(params, snapshot);

            // Convert JavaFX image to BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            File tempImage = File.createTempFile("ticket", ".png");
            ImageIO.write(bufferedImage, "png", tempImage);

            // Use float cast for iText Rectangle
            Rectangle pdfSize = new Rectangle((float) paneWidth, (float) paneHeight);
            Document document = new Document(pdfSize);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(tempImage.getAbsolutePath());
            pdfImg.setAbsolutePosition(0f, 0f);
            pdfImg.scaleToFit((float) paneWidth, (float) paneHeight); // iText needs float
            document.add(pdfImg);
            document.close();

            tempImage.delete(); // cleanup

            System.out.println("✅ Ticket exported to PDF: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to export PDF: " + e.getMessage());
        } finally {
            btnPrintPDF.setVisible(true); // Show button again
        }

            }


}
