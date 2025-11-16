package menuorderingapp.project.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup")
public class SetupController {

    // this controller is only for generating hashes for the cashiers table
    // not for production use
    private final PasswordEncoder passwordEncoder;

    public SetupController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/hash")
    public String generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        return String.format("Password: %s<br>Hash: %s<br><br>SQL:<br>UPDATE cashiers SET password_hash = '%s';",
                password, hash, hash);
    }
}
