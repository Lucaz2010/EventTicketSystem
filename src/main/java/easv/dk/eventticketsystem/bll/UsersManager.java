package easv.dk.eventticketsystem.bll;

import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.bll.util.UserSearcher;
import easv.dk.eventticketsystem.dal.IUsersDAO;
import easv.dk.eventticketsystem.dal.db.UsersDAODB;

import java.io.IOException;
import java.util.List;

public class UsersManager {
    private final IUsersDAO usersDAO = new UsersDAODB();
    private final UserSearcher userSearcher = new UserSearcher();

    public List<Users> getAllUsers() throws IOException {
        return usersDAO.getAllUsers();
    }

    public void createNewUsers(Users users) throws IOException {
        usersDAO.createNewUsers(users);
    }

    public void deleteUsers(Users users) throws IOException {
        usersDAO.deleteUsers(users);
    }

    public void updateUsers(Users users) throws IOException {
        usersDAO.updateUsers(users);
    }

    public List<Users> searchUsers(String query) throws IOException {
        List<Users> allUsers = usersDAO.getAllUsers();
        return userSearcher.searchUsers(allUsers, query);
    }

    public Users getUserByEmail(String email) throws IOException {
        List<Users> allUsers = usersDAO.getAllUsers();
        for (Users user : allUsers) {
            if (user.getUserEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }
}
