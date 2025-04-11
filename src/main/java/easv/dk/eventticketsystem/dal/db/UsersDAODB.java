package easv.dk.eventticketsystem.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.dk.eventticketsystem.be.Users;
import easv.dk.eventticketsystem.dal.IUsersDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAODB implements IUsersDAO {
    private DBConnection con = new DBConnection();

    @Override
    public List<Users> getAllUsers() throws IOException {
        List<Users> allUsers = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String userName = rs.getString("user_name");
                String role = rs.getString("role");
                String userEmail = rs.getString("user_email");
                String userPhone = rs.getString("user_phone");
                String userImagePath = rs.getString("user_image_path");
                String password = rs.getString("password");
                Users users = new Users(userId, userName, userImagePath, role, userEmail, userPhone, password);
                allUsers.add(users);
            }
        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allUsers;
    }

    @Override
    public void createNewUsers(Users users) throws IOException {
        String sql = "INSERT INTO Users (user_name, user_image_path, role, user_email, user_phone, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = con.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, users.getUserName());
            ps.setString(2, users.getUserImagePath());
            ps.setString(3, users.getRole());
            ps.setString(4, users.getUserEmail());
            ps.setString(5, users.getUserPhone());
            ps.setString(6, users.getPassword());
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error adding users to the database: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUsers(Users users) throws IOException {
        String sql = "DELETE FROM users where user_id = ?";
        try (Connection connection = con.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, users.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error deleting users and its dependencies: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateUsers(Users users) throws IOException {
        String sql = "UPDATE Users SET user_name = ?, user_image_path = ?, role = ?, user_email = ?, user_phone = ? WHERE user_id = ? ";
        try (Connection connection = con.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, users.getUserName());
            ps.setString(2, users.getUserImagePath());
            ps.setString(3, users.getRole());
            ps.setString(4, users.getUserEmail());
            ps.setString(5, users.getUserPhone());
            ps.setInt(6, users.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error updating users in the database: " + e.getMessage(), e);
        }
    }

}
