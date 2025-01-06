package com.example.demo;
import visitor.VisitorMenu;
import student.StudentMenuController;

import database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginMenu {
    @FXML
    private TextField adminUsername;
    @FXML
    private PasswordField adminPassword;
    @FXML
    private TextField visitorUsername;
    @FXML
    private PasswordField visitorPassword;
    @FXML
    private TextField studentNIM;
    @FXML
    private PasswordField studentPIC;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        String username = adminUsername.getText();
        String password = adminPassword.getText();

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, Admin!");
            openAdminMenu(event);
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void openAdminMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmenu.fxml"));
            Parent adminMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(adminMenuRoot, 1400, 800));
            stage.setTitle("Admin Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginAsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/loginadmin.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Login Admin");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginAsStudent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/loginstudent.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Login Student");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLoginAsVisitor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/loginvisitors.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Login Visitor");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVisitorLogin(ActionEvent event) {
        String username = visitorUsername.getText();
        String password = visitorPassword.getText();

        try {
            Connection connect = Database.getConnection();
            String sql = "SELECT fullname, email FROM visitor WHERE username = ? AND password = ?";
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String fullName = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + fullName + "!");
                openVisitorMenu(event, fullName, username, email);
            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }

            resultSet.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    @FXML
    private void openVisitorMenu(ActionEvent event, String fullName, String username, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/visitormenu.fxml"));
            Parent visitorMenuRoot = loader.load();

            VisitorMenu controller = loader.getController();
            controller.setUserDetails(fullName, username, email);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(visitorMenuRoot, 1400, 800));
            stage.setTitle("Visitor Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStudentLogin(ActionEvent event) {
        String nim = studentNIM.getText();
        String pic = studentPIC.getText();

        try {
            Connection connect = Database.getConnection();
            String sql = "SELECT name, nim, faculty, major FROM student WHERE nim = ? AND pic = ?";
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, nim);
            statement.setString(2, pic);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String faculty = resultSet.getString("faculty");
                String major = resultSet.getString("major");
                showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + name + "!");
                openStudentMenu(event, name, nim, faculty, major);
            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Invalid NIM or PIC.");
            }

            resultSet.close();
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    @FXML
    private void openStudentMenu(ActionEvent event, String name, String nim, String faculty, String major) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/studentmenu.fxml"));
            Parent studentMenuRoot = loader.load();

            StudentMenuController controller = loader.getController();
            controller.initData(name, nim, faculty, major);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(studentMenuRoot, 1400, 800));
            stage.setTitle("Student Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHyperlinkRegulations(ActionEvent event) {
        String url = "https://lib.umm.ac.id/profile/rules/";

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to open regulations URL.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
