package menuorderingapp.project.util.dev;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Development utility to generate BCrypt password hashes for SQL migrations.
 * This is NOT a production class and should only be used during development.
 *
 * Usage:
 * 1. Run this class directly (right-click -> Run in IDE)
 * 2. Copy the generated hash from console output
 * 3. Paste into your SQL migration file
 *
 * Note: BCrypt generates a different hash each time you run this (by design).
 * This is normal and secure - each hash will still validate the same password.
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        System.out.println("=".repeat(70));
        System.out.println("BCrypt Password Hash Generator");
        System.out.println("=".repeat(70));
        System.out.println();

        String[] passwordsToHash = {
            "kasir123",
            "kasir123",
            "admin123"
        };

        for (String password : passwordsToHash) {
            String hash = encoder.encode(password);

            System.out.println("Password:  " + password);
            System.out.println("Hash:      " + hash);
            System.out.println();
            System.out.println("SQL Examples:");
            System.out.println("  -- Insert new user:");
            System.out.println("  INSERT INTO cashiers (username, password_hash, display_name, role, is_active)");
            System.out.println("  VALUES ('username', '" + hash + "', 'Display Name', 'CASHIER', TRUE);");
            System.out.println();
            System.out.println("  -- Update existing user:");
            System.out.println("  UPDATE cashiers SET password_hash = '" + hash + "' WHERE username = 'username';");
            System.out.println();
            System.out.println("-".repeat(70));
            System.out.println();
        }

        System.out.println("IMPORTANT NOTES:");
        System.out.println("1. Each time you run this, you'll get a different hash (this is normal!)");
        System.out.println("2. All hashes will still validate the same password");
        System.out.println("3. Copy the hash you need into your SQL migration file");
        System.out.println("4. Never expose password generation in production endpoints");
        System.out.println();
        System.out.println("=".repeat(70));
    }

    /**
     * Generate a single hash for a specific password.
     * Call this method from other code if needed.
     */
    public static String generateHash(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        return encoder.encode(password);
    }

    /**
     * Verify if a password matches a hash.
     * Useful for testing.
     */
    public static boolean verifyPassword(String password, String hash) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, hash);
    }
}
