package adminbook;

import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.text.Text;
import java.util.Map;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminBookController {
    private Map<String, Integer> bookCounts = new HashMap<>();

    @FXML
    private Button addButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextField IDfield;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private TextField author;

    @FXML
    private Text totalBookText;
    @FXML
    private Text Fiction;
    @FXML
    private Text languangeCulture;
    @FXML
    private Text nonfiction;
    @FXML
    private Text socialscience;
    @FXML
    private Text science;
    @FXML
    private Text technology;
    @FXML
    private Text health;
    @FXML
    private Text business;
    @FXML
    private Text philosophy;
    @FXML
    private Text art;

    public void initialize() {
        updateBookCountsFromDatabase();
        updateTotalBookCount();
    }

    private void updateBookCountsFromDatabase() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String query = "SELECT genre, SUM(stock) AS book_count FROM book GROUP BY genre";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String genre = rs.getString("genre");
                int count = rs.getInt("book_count");
                bookCounts.put(genre, count);
            }

            updateUIWithCounts();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUIWithCounts() {
        Fiction.setText(String.valueOf(bookCounts.getOrDefault("Fiction", 0)));
        languangeCulture.setText(String.valueOf(bookCounts.getOrDefault("Languange and Culture", 0)));
        nonfiction.setText(String.valueOf(bookCounts.getOrDefault("Non-Fiction", 0)));
        socialscience.setText(String.valueOf(bookCounts.getOrDefault("Social Science", 0)));
        science.setText(String.valueOf(bookCounts.getOrDefault("Science", 0)));
        technology.setText(String.valueOf(bookCounts.getOrDefault("Technology", 0)));
        health.setText(String.valueOf(bookCounts.getOrDefault("Health and Medicine", 0)));
        business.setText(String.valueOf(bookCounts.getOrDefault("Business and Economy", 0)));
        philosophy.setText(String.valueOf(bookCounts.getOrDefault("Philosophy and Religion", 0)));
        art.setText(String.valueOf(bookCounts.getOrDefault("Art", 0)));
        updateTotalBookCount();
    }

    private void updateTotalBookCount() {
        int total = bookCounts.values().stream().mapToInt(Integer::intValue).sum();
        totalBookText.setText(String.valueOf(total));
    }

    @FXML
    public void handleAddBookClick (MouseEvent mouseEvent) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/example/demo/createbook.fxml"));
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
    public void handleDisplayBookClick (MouseEvent mouseEvent) {
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
    public void handleEditBookClick (MouseEvent mouseEvent) {
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
    public void handleDeleteBookClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/deletebook.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Delete Book Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGoToAdminMenu (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin menu Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogOutClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/mainmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Main menu Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
