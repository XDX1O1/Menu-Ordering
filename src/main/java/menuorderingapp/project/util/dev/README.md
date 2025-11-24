# Development Utilities

This package contains development-only utilities that should **NOT** be used in production code.

## PasswordHashGenerator

Utility to generate BCrypt password hashes for SQL migrations.

### Usage

1. Open `PasswordHashGenerator.java` in your IDE
2. Run the main method (right-click → Run)
3. Copy the generated hash from the console output
4. Paste it into your SQL migration file

### Example

```java
// Run this class to get output like:
Password:  password123
Hash:      $2a$10$N9qo8uLOickgx2ZMRZoMye.J8I..sJ9HXp6cSYwQD6o6e7F7QY8W2

SQL Examples:
  -- Insert new user:
  INSERT INTO cashiers (username, password_hash, display_name, role, is_active)
  VALUES ('username', '$2a$10$...', 'Display Name', 'CASHIER', TRUE);
```

### Important Notes

- **Each run generates a different hash** - This is normal and secure!
- All hashes will validate the same password
- Never expose password generation in production endpoints
- This is for **development only** before getting uploaded into **production**