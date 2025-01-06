package student;

import database.Database;
import adminstudent.Student; // Pastikan untuk mengimpor kelas Student dengan benar

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StudentExtendBook {

    @FXML
    private TableView<Student> studentInfoTable;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> nimColumn;

    @FXML
    private TableColumn<Student, String> picColumn;

    @FXML
    private TableColumn<Student, String> facultyColumn;

    @FXML
    private TableColumn<Student, String> majorColumn;

    @FXML
    private TableColumn<Student, String> emailColumn;

    @FXML
    private TableView<Loan> borrowingHistoryTable;

    @FXML
    private TableColumn<Loan, String> bookIdColumn;

    @FXML
    private TableColumn<Loan, String> titleColumn;

    @FXML
    private TableColumn<Loan, String> dateBorrowedColumn;

    @FXML
    private TableColumn<Loan, String> dueDateColumn;

    @FXML
    private TableColumn<Loan, String> statusColumn;

    @FXML
    private TextField bookIdField;

    private String nimStudent;

    // Method to set the NIM of the logged-in student
    public void setNimStudent(String nimStudent) {
        this.nimStudent = nimStudent;
        loadStudentInfo();
        loadBookBorrowingHistory();
    }

    @FXML
    private void initialize() {
        //student info table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        picColumn.setCellValueFactory(new PropertyValueFactory<>("pic"));
        facultyColumn.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        //book history table
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadStudentInfo() {
        studentInfoTable.getItems().clear();

        String query = "SELECT name, nim, pic, faculty, major, email FROM student WHERE nim = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nimStudent);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String nim = rs.getString("nim");
                String pic = rs.getString("pic");
                String faculty = rs.getString("faculty");
                String major = rs.getString("major");
                String email = rs.getString("email");
                studentInfoTable.getItems().add(new Student(name, nim, pic, faculty, major, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBookBorrowingHistory() {
        borrowingHistoryTable.getItems().clear();

        String query = "SELECT b.bookid, bk.title, b.loandate, b.duedate, b.returndate " +
                "FROM borrowing b " +
                "JOIN book bk ON b.bookid = bk.id " +
                "WHERE b.nimstudent = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nimStudent);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookId = rs.getString("bookid");
                String title = rs.getString("title");
                LocalDate loanDate = rs.getObject("loandate", LocalDate.class);
                LocalDate dueDate = rs.getObject("duedate", LocalDate.class);
                LocalDate returnDate = rs.getObject("returndate", LocalDate.class);
                Loan loan = new Loan(nimStudent, bookId, title, loanDate, dueDate, returnDate);
                borrowingHistoryTable.getItems().add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOkButtonClick() {
        String bookId = bookIdField.getText().trim();
        if (bookId.isEmpty()) {
            showAlert("Error", "Book ID is empty", "Please enter a Book ID.", Alert.AlertType.ERROR);
            return;
        }

        String query = "SELECT duedate, returndate FROM borrowing WHERE nimstudent = ? AND bookid = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nimStudent);
            stmt.setString(2, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LocalDate dueDate = rs.getObject("duedate", LocalDate.class);
                LocalDate returnDate = rs.getObject("returndate", LocalDate.class);

                if (returnDate != null) {
                    showAlert("Error", "Cannot Extend Book", "The book with ID " + bookId + " has already been returned on " + returnDate + ".", Alert.AlertType.ERROR);
                    return;
                }

                LocalDate extendedDate = dueDate.plusDays(3);

                // Update the due date in the database
                String updateQuery = "UPDATE borrowing SET duedate = ? WHERE nimstudent = ? AND bookid = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setObject(1, extendedDate);
                    updateStmt.setString(2, nimStudent);
                    updateStmt.setString(3, bookId);
                    updateStmt.executeUpdate();
                }

                showAlert("Success", "Book Extended", "The book with ID " + bookId + " has been extended from " + dueDate + " to " + extendedDate + ".", Alert.AlertType.INFORMATION);
                loadBookBorrowingHistory();
            } else {
                showAlert("Error", "Book ID Not Found", "No such book ID is currently borrowed by the student.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String headerText, String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
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
            controller.setNimStudent(nimStudent);

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
            controller.setNimStudent(nimStudent);

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
            controller.setNimStudent(nimStudent);

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(borrowBookRoot, 1400, 800));
            stage.setTitle("History Activities Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
