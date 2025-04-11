package easv.dk.eventticketsystem.security;

import easv.dk.eventticketsystem.be.Users;

public class UserSession {
    private static Users currentUser;
    public static void setCurrentUser(Users user) {
        currentUser = user;
    }
    public static Users getCurrentUser() {
        return currentUser;
    }
}