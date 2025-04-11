package easv.dk.eventticketsystem.gui.controllers;

import easv.dk.eventticketsystem.MainApplication;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.gui.controllers.componentsControllers.UserCardController;
import easv.dk.eventticketsystem.gui.model.EventTicketSystemModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageUsersController implements Initializable {
    @FXML
    private Button btnCreateNewUser;
    @FXML
    private FlowPane userCardPane;
    @FXML
    private BorderPane usersPane;
    @FXML
    private AnchorPane toolbarContainer;

    private ToolbarController toolbarController;
    private Users currentUser;

    private static final EventTicketSystemModel model = new EventTicketSystemModel();
    private List<Users> usersList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Manually load the toolbar FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/Toolbar.fxml"));
            AnchorPane toolbar = loader.load();
            toolbarController = loader.getController();
            // Set the parent controller reference in the toolbar controller
            toolbarController.setParentController(this);
            // Place the loaded toolbar into the placeholder container
            toolbarContainer.getChildren().setAll(toolbar);

            // Load all users
            loadAllUsers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Setter for currentUser; call this from your login code after authenticating the user.
    public void setCurrentUser(Users user) {
        this.currentUser = user;
        String role = user.getRole(); // e.g. "Event Coordinator"
        //System.out.println("User role: '" + role + "'");
        if ("Event Coordinator".equalsIgnoreCase(user.getRole().trim())) {
            btnCreateNewUser.setVisible(false);
        } else {
            btnCreateNewUser.setVisible(true);
        }
    }

    /// use for loop to add all users by cards.
    public void loadAllUsers() throws IOException {
        userCardPane.getChildren().clear();
        // Load all users from the model and display them.
        List<Users> allUsers = model.getAllUsers();
        for (Users user : allUsers) {
            addUserCard(user);
        }
    }

    // load a single user card.
    private void addUserCard(Users user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/eventticketsystem/components/UserCard.fxml"));
        AnchorPane userCard = loader.load();
        UserCardController cardController = loader.getController();
        cardController.setParentController(this);
        cardController.setUserData(user);
        userCardPane.getChildren().add(userCard);
    }

    public void onClickCreateNewUser(ActionEvent actionEvent) throws IOException {
        //System.out.println("Create clicked for user: ");
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/easv/dk/eventticketsystem/UserEditorView.fxml"));
        Parent root = fxmlLoader.load();
        UserEditorController editorController = fxmlLoader.getController();
        editorController.setParentController(this); // 'this' is ManageUsersController instance
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /// Call from ToolbarController to update the view based on the search query.
    public void searchUsers(String query) {
        userCardPane.getChildren().clear();
        try {
            ObservableList<Users> searchedUsers = model.getSearchedUsers(query);
            for (Users user : searchedUsers) {
                addUserCard(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}