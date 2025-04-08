package easv.dk.eventticketsystem.gui.controllers.componentsControllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import easv.dk.eventticketsystem.be.Customer;
import easv.dk.eventticketsystem.be.TicketOnOrder;
import easv.dk.eventticketsystem.bll.QRBarcodeManager;
import easv.dk.eventticketsystem.bll.TicketManager;
import easv.dk.eventticketsystem.gui.controllers.ManageOrdersController;
import easv.dk.eventticketsystem.gui.controllers.TicketController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import easv.dk.eventticketsystem.gui.util.AlertUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderCardController {


    @FXML
    private Button deleteTicketButton;
    @FXML
    private Button addTicketButton;
    @FXML
    private VBox cardRoot;
    @FXML
    private AnchorPane containerRoot;
    @FXML
    private Label lblOrderNumber;

    @FXML
    private Label lblCustomerName;
    @FXML
    public Label lblCustomerEmail;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtCustomerEmail;

    @FXML
    private TableView<TicketOnOrder> ticketsTable;
    @FXML
    private TableColumn<TicketOnOrder, String> actionColumn;
    @FXML
    private TableColumn<TicketOnOrder, String> ticketTypeColumn;
    @FXML
    private TableColumn<TicketOnOrder, Integer> quantityColumn;

    @FXML
    private Button printOrderButton;
    @FXML
    private Button emailTicketsButton;

    @FXML
    private Button editOrderButton;

    @FXML
    private Button deleteOrderButton;
    private boolean isEditing = false;

    private ManageOrdersController parentController;

    private List<TicketOnOrder> ticketList;
    private TicketOnOrder baseTicket;

    private EventTicketSystemModel model;

    @FXML
    public void initialize() {
        containerRoot.getStyleClass().add("order-card");
        System.out.println("✅ OrderCardController initialized: " + this);

        ticketsTable.setRowFactory(tv -> {
            TableRow<TicketOnOrder> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    TicketOnOrder clickedTicket = row.getItem();
                    openTicket(clickedTicket);
                }
            });
            return row;
        });
    }

    public void setData(TicketOnOrder baseTicket, List<TicketOnOrder> allTickets) {
        System.out.println("✅ setData called on OrderCardController - Order #" + baseTicket.getOrderId());
        this.baseTicket = baseTicket;
        this.ticketList = allTickets;
        System.out.println("Tickets for order #" + baseTicket.getOrderId() + ": " + allTickets.size());

        lblOrderNumber.setText("Order #" + baseTicket.getOrderId());
        txtCustomerName.setText(baseTicket.getCustomerName());
        txtCustomerEmail.setText(baseTicket.getCustomerEmail());
/// gets all the ticktes and avoids adding a "fake ticket"
        List<TicketOnOrder> realTickets = new ArrayList<>();
        for (TicketOnOrder t : allTickets) {
            if (t.getTicketId() != -1 && t.getCode() != null && !t.getCode().isBlank()) {
                realTickets.add(t);
            }
        }
        ticketsTable.getItems().setAll(realTickets);

        configureTicketTableSizes();


        actionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEventName()));
        ticketTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTicketType()));
        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

    }

    //    //Method for Configuring the ticket table columns to automatically resize
    // * proportionally based on the table's total width.
    private void configureTicketTableSizes() {

        ticketsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ticketsTable.setFixedCellSize(30);
        ticketsTable.setPrefHeight(30 * 3 + 28); // 3 visible rows + header

        // Prevent extra invisible column from showing up
        ticketsTable.getColumns().forEach(col -> {
            col.setResizable(false);
            col.setReorderable(false);
        });

        ticketsTable.setPlaceholder(new Label("")); // prevents weird layout shift when empty

        actionColumn.prefWidthProperty().bind(ticketsTable.widthProperty().multiply(0.5));
        ticketTypeColumn.prefWidthProperty().bind(ticketsTable.widthProperty().multiply(0.3));
        quantityColumn.prefWidthProperty().bind(ticketsTable.widthProperty().multiply(0.2));
        ticketsTable.getItems().forEach(ticket -> {
            System.out.println("Ticket for Order #" + ticket.getOrderId() + " - " + ticket.getEventName());
        });

    }

    public void refreshTickets() {
        if (model != null && baseTicket != null) {
            List<TicketOnOrder> updatedTickets = model.getAllOrderDetails().stream()
                    .filter(ticket -> ticket.getOrderId() == baseTicket.getOrderId())
                    .toList();

            ticketList = updatedTickets;
            ticketsTable.getItems().setAll(updatedTickets);
        }
    }

    public void setDataPlaceholder() {
        int fakeOrderId = -1; // Mark it as "not saved yet"
        baseTicket = new TicketOnOrder(
                fakeOrderId,
                "", "", // name and email left blank
                "", 0, "", "", "", "", "", 0, 0.0
        );
        lblOrderNumber.setText("New Order");
        txtCustomerName.setText("[insert customer name]");
        txtCustomerEmail.setText("[insert email]");
        ticketsTable.getItems().clear();
        configureTicketTableSizes();
    }

    private void openTicket(TicketOnOrder ticket) {
        try {


            String uniqueCode = ticket.getCode();
            String qrPath = "qr_codes/" + uniqueCode + ".png";
            String barcodePath = "barcodes/" + uniqueCode + ".png";

            /// If order was moved from history to orders (status = "Confirmed" to "Pending") RegeneateQRBarcode file:
            File qrFile = new File(qrPath);
            File barcodeFile = new File(barcodePath);

            if(!qrFile.exists()||!barcodeFile.exists()) {
                System.out.println(" Regenerating missing QR/Barcode for ticket: " + uniqueCode);
                model.regenerateTicket(ticket.getCode());

            }

            //Load fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/StandardTicket.fxml"));
            Parent root = loader.load();

            TicketController ticketController = loader.getController();
//            ticketController.setTicketData(ticket, qrPath);
            ticketController.setTicketData(ticket, qrPath,barcodePath);

            Stage stage = new Stage();
            stage.setTitle("Print Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParentController(ManageOrdersController controller) {
        this.parentController = controller;
    }

    public void setModel(EventTicketSystemModel model) {
        this.model = model;
    }

    private void deleteFile(){


    }


    @FXML
    private void onCardClicked() {
        if (parentController != null && baseTicket != null) {
            parentController.setSelectedOrder(baseTicket, containerRoot);
        }
    }

    @FXML
    private void onDeleteClicked() {
        TicketOnOrder selectedTicket = ticketsTable.getSelectionModel().getSelectedItem();

        if (selectedTicket == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Ticket Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a ticket to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this ticket?");
        confirmAlert.setContentText("Ticket: " + selectedTicket.getTicketType());

        confirmAlert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {

                    // Should probably change the Ticket manager to model - lucas
                    TicketManager ticketManager = new TicketManager();

                    /// Deletes from database and files
                    model.deleteTicket(selectedTicket.getCode());
                    /// Removes from UI
                    ticketsTable.getItems().remove(selectedTicket); //
                    System.out.println("🗑️ Deleted ticket with code: " + selectedTicket.getCode());

//                    String qrPath = System.getProperty("user.dir") + "/qr_codes/" + selectedTicket.getCode()+ ".png";
//                    String barcodePath = System.getProperty("user.dir") + "/barcodes/" + selectedTicket.getCode()+ ".png";
//
//                    File qrFile = new File(qrPath);
//                    File barcodeFile = new File (barcodePath);
//                    if(qrFile.exists()){
//                        qrFile.delete();
//                    }
//                    if(barcodeFile.exists()){
//                        barcodeFile.delete();
//                    }




                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to delete ticket.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }



    @FXML
    private void onAddTicketClicked() {

        if (baseTicket.getOrderId() == -1) {
            AlertUtil.showErrorAlert("Order Not Saved", "Please enter name and email and click the pencil to save the order before adding tickets.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/AddTicket.fxml"));
            Parent root = loader.load();

            AddTicketController controller = loader.getController();
            controller.setModel(model);
            controller.setOrderId(baseTicket.getOrderId()); // 👈 Pass current order ID
            controller.setParentController(this);           // 👈 So we can refresh tickets

            Stage stage = new Stage();
            stage.setTitle("Add Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not open Add Ticket window.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onEditOrderClicked() {
        if (!isEditing) {
            /// Makes the button to Edit
            isEditing = true;

            txtCustomerName.setEditable(true);
            txtCustomerEmail.setEditable(true);

            ((FontIcon) editOrderButton.getGraphic()).setIconLiteral("bi-save-fill");
        } else {
            /// Makes the button to save
            isEditing = false;

            String newName = txtCustomerName.getText().trim();
            String newEmail = txtCustomerEmail.getText().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Name and email cannot be empty");
                alert.showAndWait();
                return;
            }

            try {
                Customer existingCustomer = model.getCustomerByEmail(newEmail);
                int customerId;
                if (existingCustomer != null) {
                    /// Here checks if the email exists, so it doesnt duplicate a customer
                    if (!existingCustomer.getCustomerName().equals(newName)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Name Mismatch");
                        alert.setHeaderText("Email already exists");
                        alert.setContentText("A customer with this email already exists.\nThe name will remain as \"" + existingCustomer.getCustomerName() + "\".");
                        alert.showAndWait();
                    }
                    customerId = existingCustomer.getCustomerId();
                } else {
                    /// If customer doesnt exist, it creates a new one
                    customerId = model.getOrCreateCustomerId(newName, newEmail);
                }

                if (baseTicket.getOrderId() == -1) {
                    // NEW order: create it in the DB and get the new ID
                    int newOrderId = model.createOrder(customerId);
                    baseTicket = new TicketOnOrder(
                            newOrderId,
                            newName,
                            newEmail,
                            "", 0, "", "", "", "", "Location", 0, 0.0
                    );
                    /// removes annoying "fake" ticket placeholder
                    if (ticketList != null && !ticketList.isEmpty()) {
                        ticketList.clear();
                        ticketsTable.getItems().clear();
                    }
                } else {
                    // EXISTING order: update its customer (if edited)
                    model.updateOrderCustomer(baseTicket.getOrderId(), customerId);
                }
                baseTicket.setCustomerName(newName);
                baseTicket.setCustomerEmail(newEmail);
                parentController.displayOrders();
                System.out.println("🔁 Refreshed orders after update!");


            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update order: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onPrintOrderClicked() {

        System.out.println("✅ Ticket PDF generated!");
        generatePDF();


    }

    private void generatePDF() {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save all tickets to PDF from Order " + baseTicket.getOrderId());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try {
            Rectangle ticketSize = new Rectangle(950, 350);
            Document document = new Document(ticketSize);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            for (TicketOnOrder ticket : ticketList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/StandardTicket.fxml"));
                Parent root = loader.load();

                TicketController controller = loader.getController();
                controller.hidePrintButton();
                String qrPath = "qr_codes/" + ticket.getCode() + ".png";
                String barcodePath = "barcodes/" + ticket.getCode() + ".png";
                controller.setTicketData(ticket, qrPath, barcodePath);

                // Force background color to white
                Scene tempScene = new Scene(root, 950, 350);
                tempScene.setFill(javafx.scene.paint.Color.WHITE); // <-- 💡 force white background

                WritableImage snapshot = tempScene.snapshot(null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

                File tempImage = File.createTempFile("ticket", ".png");
                ImageIO.write(bufferedImage, "png", tempImage);

                com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(tempImage.getAbsolutePath());
                pdfImg.setAbsolutePosition(0, 0);
                pdfImg.scaleToFit(ticketSize.getWidth(), ticketSize.getHeight());
                document.newPage();
                document.add(pdfImg);

                tempImage.delete();
            }

            document.close();
            System.out.println("✅ All tickets exported to PDF: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to export all tickets: " + e.getMessage());
        }
    }






}

