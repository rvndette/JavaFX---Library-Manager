package student;

import database.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentMenuController {

    @FXML
    private Text nameText;

    @FXML
    private Text nimText;

    @FXML
    private Text facultyText;

    @FXML
    private Text majorText;

    @FXML
    private Text totalBookText;

    private String loggedInNIM;

    public void initData(String name, String nim, String faculty, String major) {
        nameText.setText("Name: " + (name));
        nimText.setText("NIM: " + (nim));
        facultyText.setText("Faculty: " + (faculty));
        majorText.setText("Major: " + (major));
        loggedInNIM = nim;

        loadTotalBorrowedBooks();
    }

    private void loadTotalBorrowedBooks() {
        String query = "SELECT COUNT(*) FROM borrowing WHERE nimstudent = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, loggedInNIM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalBorrowedBooks = rs.getInt(1);
                totalBookText.setText(String.valueOf(totalBorrowedBooks));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearchBook (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/searchbookstudent.fxml"));
            Parent manageStudentRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(manageStudentRoot, 1400, 800));
            stage.setTitle("Search Book Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBorrowBook(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/borrowbookstudent.fxml"));
            Parent borrowBookRoot = loader.load();

            StudentBorrowBook controller = loader.getController();
            controller.setNimStudent(loggedInNIM);

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(borrowBookRoot, 1400, 800));
            stage.setTitle("Borrow Book Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleReturnBook(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/returnbookstudent.fxml"));
            Parent borrowBookRoot = loader.load();

            StudentReturnBook controller = loader.getController();
            controller.setNimStudent(loggedInNIM);

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(borrowBookRoot, 1400, 800));
            stage.setTitle("Return Book Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHistoryActivitiesStudent(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/historyactivitiesstudent.fxml"));
            Parent borrowBookRoot = loader.load();

            StudentHistoryActivities controller = loader.getController();
            controller.setNimStudent(loggedInNIM);

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(borrowBookRoot, 1400, 800));
            stage.setTitle("History Activities Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExtendBookStudent(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/extendbookstudent.fxml"));
            Parent borrowBookRoot = loader.load();

            StudentExtendBook controller = loader.getController();
            controller.setNimStudent(loggedInNIM);

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(borrowBookRoot, 1400, 800));
            stage.setTitle("Extend Book Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLoggedInNIM() {
        return loggedInNIM;
    }
}
