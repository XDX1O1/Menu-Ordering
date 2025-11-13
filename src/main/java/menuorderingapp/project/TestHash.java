package menuorderingapp.project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash {
    public static void main(String[] args) {

        // this class is used to generate a hash for a password
        // not for prduction use
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate a NEW hash for "password123"
        String correctHash = encoder.encode("password123");
        System.out.println("Correct hash for 'password123': " + correctHash);

        // Verify it works
        boolean matches = encoder.matches("password123", correctHash);
        System.out.println("Verification: " + matches);
    }
}
