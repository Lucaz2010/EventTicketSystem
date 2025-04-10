package easv.dk.eventticketsystem.be;

public class Users {
    private int userId;
    private String userName;
    private String userImagePath;
    private String role;
    private String userEmail;
    private String userPhone;

    public Users(int userId, String userName, String userImagePath, String role, String userEmail, String userPhone) {
        this.userId = userId;
        this.userName = userName;
        this.userImagePath = userImagePath;
        this.role = role;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @Override
    public String toString() {
        return userName + ',' + role + ',' + userEmail + ',' + userImagePath + ',' + userPhone;}
}

