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

public class StudentReturnBook {

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
    private DatePicker returnDatePicker;

    @FXML
    private TextField bookIdField;

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

    private String nimStudent;

    // Method to set the NIM of the logged-in student
    public void setNimStudent(String nimStudent) {
        this.nimStudent = nimStudent;
        loadStudentInfo();
        loadBookBorrowingHistory();
    }

    @FXML
    private void initialize() {
        // Initialize table columns for borrowing history
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize table columns for student information
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        picColumn.setCellValueFactory(new PropertyValueFactory<>("pic"));
        facultyColumn.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    @FXML
    private void handleOkButton() {
        String bookId = bookIdField.getText();
        LocalDate returnDate = returnDatePicker.getValue();

        if (bookId.isEmpty()) {
            showAlert("Perhatian", "ID Buku Kosong", "Silakan masukkan ID buku.", Alert.AlertType.INFORMATION);
            return;
        }

        if (returnDate != null) {
            LocalDate dueDate = getDueDateFromDatabase(bookId);
            if (dueDate != null && returnDate.isAfter(dueDate)) {
                showAlert("Perhatian", "Pengembalian Terlambat!",
                        "Anda telah mengembalikan buku setelah batas waktu yang ditentukan.", Alert.AlertType.WARNING);
                loadReturnBookIfLateFXML();
            } else {
                // Update return date and increase stock
                if (updateReturnAndIncreaseStock(bookId, returnDate)) {
                    String successMessage = "Pengembalian buku dengan ID " + bookId + " berhasil.";
                    showAlert("Sukses", "Pengembalian Berhasil", successMessage, Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Perhatian", "Gagal Mengembalikan Buku!",
                            "Gagal memperbarui tanggal pengembalian atau meningkatkan stok buku.", Alert.AlertType.ERROR);
                }
            }

            // Refresh borrowing history after update
            loadBookBorrowingHistory();
        } else {
            showAlert("Perhatian", "Tanggal Pengembalian Belum Dipilih!",
                    "Silakan pilih tanggal pengembalian buku.", Alert.AlertType.INFORMATION);
        }
    }

    private void loadReturnBookIfLateFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/returnbookiflatestudent.fxml"));
            Parent root = loader.load();
              StudentLateReturning controller = loader.getController();
              controller.setNimStudent(nimStudent);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1400, 800));
            stage.setTitle("Return Book If Late");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update return date in database and increase stock
    private boolean updateReturnAndIncreaseStock(String bookId, LocalDate returnDate) {
        String updateBorrowingQuery = "UPDATE borrowing SET returndate = ? WHERE bookid = ?";
        String updateBooksQuery = "UPDATE book SET stock = stock + 1 WHERE id = ?";
        boolean success = false;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(updateBorrowingQuery);
             PreparedStatement stmt2 = conn.prepareStatement(updateBooksQuery)) {

            // Update borrowing table
            stmt1.setObject(1, returnDate);
            stmt1.setString(2, bookId);
            int affectedRows1 = stmt1.executeUpdate();

            // Update books table (increase stock)
            stmt2.setString(1, bookId);
            int affectedRows2 = stmt2.executeUpdate();

            // Check if both updates were successful
            if (affectedRows1 > 0 && affectedRows2 > 0) {
                success = true;
                System.out.println("Return date updated and stock increased successfully.");
            } else {
                System.out.println("Failed to update return date or increase stock.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    private LocalDate getDueDateFromDatabase(String bookId) {
        String query = "SELECT duedate FROM borrowing WHERE bookid = ?";
        LocalDate dueDate = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dueDate = rs.getObject("duedate", LocalDate.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dueDate;
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
                String pic = rs.getString("faculty");
                String faculty = rs.getString("major");
                String major = rs.getString("email");
                String email = rs.getString("pic");
                studentInfoTable.getItems().add(new Student(name, nim, pic, faculty, major, email));
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

    @FXML
    public void handleExtendBookStudent(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/extendbookstudent.fxml"));
            Parent borrowBookRoot = loader.load();

            StudentExtendBook controller = loader.getController();
            controller.setNimStudent(nimStudent);

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(borrowBookRoot, 1400, 800));
            stage.setTitle("Extend Book Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
