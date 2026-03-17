package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootApplication
@RestController
public class DemoApplication {

    // VULNERABILITY 1: Hardcoded credentials (SonarQube will flag this)
    private static final String DB_PASSWORD = "super_secret_admin_password!";

    @RequestMapping("/")
    public String home() {
        return "Welcome to the VULNERABLE Spring Boot App deployed via Jenkins!";
    }

    // VULNERABILITY 2: SQL Injection (SonarQube will flag this)
    @RequestMapping("/users")
    public String getUser(@RequestParam("id") String userId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", DB_PASSWORD);
            Statement stmt = conn.createStatement();
            // This is a classic SQL Injection vulnerability!
            stmt.executeQuery("SELECT * FROM users WHERE id = " + userId);
        } catch (Exception e) {
            // VULNERABILITY 3: Poor error handling / printing stack trace
            e.printStackTrace();
        }
        return "Fetched user: " + userId;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
