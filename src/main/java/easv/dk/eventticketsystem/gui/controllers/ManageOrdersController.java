package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.be.TicketOnOrder;
import easv.dk.eventticketsystem.bll.QRBarcodeManager;
import easv.dk.eventticketsystem.gui.controllers.componentsControllers.OrderCardController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ManageOrdersController implements Initializable {


    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane orderCardContainer;
    @FXML private TicketOnOrder selectedOrder;
    @FXML private Parent selectedCardNode;


    private final EventTicketSystemModel eventTicketSystemModel = new EventTicketSystemModel();


    public void initialize(URL location, ResourceBundle resources) {


        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            orderCardContainer.setPrefWidth(newVal.doubleValue() - 20);
        });
        displayOrders();
    }

    /**
     * Loads all pending orders from the system and displays them as cards.
     * Each order is grouped by its order ID and shown in its own FXML component.
     */

    public void displayOrders() {
        orderCardContainer.getChildren().clear();
        // Dynamically adjust wrap length to match current width
        orderCardContainer.setPrefWrapLength(orderCardContainer.getWidth());
        orderCardContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            orderCardContainer.setPrefWrapLength(newVal.doubleValue());
        });

        List<TicketOnOrder> tickets = eventTicketSystemModel.getAllOrderDetails();
/// Groups tickets by their orderId
        Map<Integer, List<TicketOnOrder>> groupedOrders = new HashMap<>();
        for (TicketOnOrder ticket : tickets) {
            int orderId = ticket.getOrderId();
            groupedOrders.computeIfAbsent(orderId, k -> new ArrayList<>()).add(ticket);
        }
// Load and display each order as a separate card component in the UI
        for (Map.Entry<Integer, List<TicketOnOrder>> entry : groupedOrders.entrySet()) {
            List<TicketOnOrder> ticketList = entry.getValue();
            for (TicketOnOrder t : ticketList) {
                System.out.println("🔍 Email for ticket in order " + t.getOrderId() + ": " + t.getCustomerEmail());
            }
            TicketOnOrder baseTicket = null;
            for (TicketOnOrder t : ticketList) {
                if (t.getTicketId() != -1 && t.getTicketType() != null && !t.getTicketType().isBlank()) {
                    baseTicket = t;
                    break;
                }
            }

        // If still null, just use the first one (probably ticketless but we still need name/email/orderId)
            if (baseTicket == null && !ticketList.isEmpty()) {
                baseTicket = ticketList.get(0);
            }
            System.out.println("📦 Creating card for Order #" + baseTicket.getOrderId() + ", total tickets: " + ticketList.size());

            try {
                URL fxmlPath = getClass().getResource("/easv/dk/eventticketsystem/components/OrderCard.fxml");
                System.out.println("📄 Loading OrderCard.fxml from: " + fxmlPath);

                FXMLLoader loader = new FXMLLoader(fxmlPath);
                Parent card = loader.load();

                OrderCardController controller = loader.getController();
                System.out.println("👀 Loaded controller: " + controller);
                controller.setParentController(this);
                controller.setModel(eventTicketSystemModel);

                controller.setData(baseTicket, ticketList);
                System.out.println("✅ Finished setData() for Order #" + baseTicket.getOrderId());

                orderCardContainer.getChildren().add(card);

            } catch (IOException e) {
                System.err.println("❌ Error loading OrderCard.fxml for Order #" + baseTicket.getOrderId());
                e.printStackTrace();
            }
        }
    }

    /**
     * Highlights the selected order visually and stores a reference.
     */

    public void setSelectedOrder(TicketOnOrder order, Parent cardNode) {

        // Deselect previously selected card

        if (selectedCardNode != null) {
            selectedCardNode.getStyleClass().remove("order-card-selected");
            selectedCardNode.getStyleClass().add("order-card"); // return it to base style
        }
        // Apply highlight selected card
        selectedCardNode = cardNode;
        selectedCardNode.getStyleClass().remove("order-card"); // remove base style
        selectedCardNode.getStyleClass().add("order-card-selected"); // add selected

        this.selectedOrder = order;
        System.out.println("📌 Selected order #" + order.getOrderId());
    }


    /**
     * Creates a new empty order card in the UI.
     */

    @FXML
    private void onClickAddOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/OrderCard.fxml"));
            Parent card = loader.load();

            OrderCardController controller = loader.getController();

            controller.setParentController(this);
            controller.setModel(eventTicketSystemModel);
            controller.setDataPlaceholder();

            orderCardContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Confirms an order by marking it as "Confirmed" and deleting QR/barcode files.
     */

    @FXML
    private void onClickConfirmOrder() {
        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Order Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an order to confirm.");
            alert.showAndWait();
            return;
        }
        if(!eventTicketSystemModel.orderHasTickets(selectedOrder.getOrderId())){

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Can´t confirm order without any tickets");
            alert.setHeaderText(null);
            alert.setContentText("Not able to confirm order without any tickets");
            alert.showAndWait();
            return;
        }


        // Changes status "Pending" to "Confirmed"
        eventTicketSystemModel.confirmOrder(selectedOrder.getOrderId());
        /// Deletes the files of QRBarcode
        List<TicketOnOrder> tickets = eventTicketSystemModel.getTicketByOrderId(selectedOrder.getOrderId());
        for (TicketOnOrder ticket : tickets) {
            QRBarcodeManager.deleteUUIDfiles(ticket.getCode());
        }

        displayOrders();
        // Reset selected order
        selectedOrder = null;
        selectedCardNode = null;

        // Confirmation message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmed");
        alert.setHeaderText(null);
        alert.setContentText("Order has been confirmed and moved to history.");
        alert.showAndWait();
    }


    /**
     * Deletes a selected order and all associated ticket files.
     */

    @FXML
    private void onClickDeleteOrder() {

        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Order Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an order to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this order?");
        confirmAlert.setContentText("Order ID: " + selectedOrder.getOrderId());


        confirmAlert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    List<TicketOnOrder> tickets =eventTicketSystemModel.getTicketByOrderId(selectedOrder.getOrderId());
                    /// Deletes the ticket QR/barcode files
                    for (TicketOnOrder ticket : tickets) {
                        QRBarcodeManager.deleteUUIDfiles(ticket.getCode());
                    }
                    // Delete from database
                    eventTicketSystemModel.deleteOrder(selectedOrder.getOrderId());
                    // Remove UI card
                    orderCardContainer.getChildren().remove(selectedCardNode);

                    System.out.println("🗑️ Deleted order ID: " + selectedOrder.getOrderId());

                    selectedOrder = null;
                    selectedCardNode = null;

                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to delete order.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

    }


}



