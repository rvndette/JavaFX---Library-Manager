package adminbook;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import database.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminDeleteBook {

    @FXML
    private TextField IDfield;

    @FXML
    private void handleConfirmButtonClick(ActionEvent event) {
        String id = IDfield.getText();
        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a valid ID");
            return;
        }

        String sql = "DELETE FROM book WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Book not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while trying to delete the book.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAddBookClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/createbook.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Add Book Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDisplayBookClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/readbook.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Display Book Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditBookClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/updatebook.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Edit Book Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHomepageClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmanagebook.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGoToAdminMenu(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Menu Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogOut(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/mainmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Main Menu Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
