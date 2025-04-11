package easv.dk.eventticketsystem.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        // The gensalt() method uses a default log_rounds value,
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Method to check a plaintext password against the hashed password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
