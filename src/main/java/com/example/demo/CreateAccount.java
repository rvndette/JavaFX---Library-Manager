package com.example.demo;

import database.Database;
import email.EmailContent;
import email.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class CreateAccount {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private DatePicker birthdateField;
    @FXML
    private TextField visitorUsername;
    @FXML
    private PasswordField visitorPassword;

    private EmailContent emailVisitor = new EmailContent();

    @FXML
    private void handleHyperlinkRegulations(ActionEvent event) {
        System.out.println("Hyperlink regulations clicked!");
        String url = "https://lib.umm.ac.id/profile/rules/";

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open regulations URL.");
        }
    }

    @FXML
    private void handleAddAccount(ActionEvent event) {
        String fullName = fullNameField.getText();
        LocalDate birthdate = birthdateField.getValue();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Full Name: " + fullName);
        System.out.println("Birthdate: " + birthdate);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        try {
            System.out.println("Connecting to the database...");
            Connection connect = Database.getConnection();
            System.out.println("Connected to the database.");

            String sql = "INSERT INTO visitor (fullname, birthdate, username, email, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, fullName);
            statement.setDate(2, java.sql.Date.valueOf(birthdate));
            statement.setString(3, username);
            statement.setString(4, email);
            statement.setString(5, password);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Account has been added", "We already sent some message to your email!");
                EmailSender.sendEmail(email, "Account Created", emailVisitor.emailVisitor(username, email));

                //kalau berhasil login akan membuka login visitor
                LoginMenu loginMenu = new LoginMenu();
                loginMenu.handleLoginAsVisitor(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add account.");
            }

            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
