package com.ecom.productcatalog.config;

import com.ecom.productcatalog.model.User;
import com.ecom.productcatalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Initializes a default admin user on application startup if one does not already exist.
 * This is useful for ensuring the application has an administrator from the beginning.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user with this email already exists to prevent duplicates.
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = new User();
            adminUser.setName(adminName);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword)); // Set a default password

            // Assign both ROLE_USER and ROLE_ADMIN for full access.
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_USER");
            roles.add("ROLE_ADMIN");
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            System.out.println(">>> Created default admin user: " + adminEmail + " <<<");
        }
    }
}