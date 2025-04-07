package easv.dk.eventticketsystem.gui.controllers;


import com.itextpdf.text.PageSize;
import easv.dk.eventticketsystem.be.TicketOnOrder;

import javafx.fxml.FXML;
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
import javafx.scene.image.Image;

import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;


public class TicketController {


    public ImageView barCodeImageView;
    @FXML private Label lblEventName;
//    @FXML private Label lblCustomerName;
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
//        lblCustomerName.setText(ticket.getCustomerName());  Uncomment if adding the customer name is needed

        lblDate.setText(ticket.getEventDate());
        lblTime.setText(ticket.getEventTime());

        lblQuantity.setText("qty: " + ticket.getQuantity());
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
    @FXML
    private void onPrintToPDFClick() {
        try {
            btnPrintPDF.setVisible(false); // Hide print button

            //Chooses where to save the PDF = "Save as" function
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Ticket PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File file = fileChooser.showSaveDialog(null);

            System.out.println("✅ Ticket PDF generated!");

            if (file == null) return;

            // takes "screenshot of the FXML <Achorpane>"
            WritableImage snapshot = ticketpane.getScene().snapshot(null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);


            // Save snapshot to a temp file (for PDF use)
            File tempImage = File.createTempFile("ticket", ".png");
            ImageIO.write(bufferedImage, "png", tempImage);

            //Create  the PDF using iText
            Document document = new Document(PageSize.A4.rotate()); //newdoc + rotated
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();


            com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(tempImage.getAbsolutePath());
            pdfImg.scaleToFit(800, 600); // Set size of image inside pdf

            // Centering logic
            float x = (PageSize.A4.getHeight() - pdfImg.getScaledWidth()) / 2;
            float y = (PageSize.A4.getWidth() - pdfImg.getScaledHeight()) / 2;
            pdfImg.setAbsolutePosition(x, y);
            document.add(pdfImg);
            document.close();

            System.out.println("✅ Ticket exported to PDF: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to export PDF: " + e.getMessage());
        }
        finally {
            btnPrintPDF.setVisible(true);
        }

    }

}
