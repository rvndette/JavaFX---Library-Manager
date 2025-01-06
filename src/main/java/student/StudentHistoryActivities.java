package student;

import adminstudent.Student;
import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StudentHistoryActivities {
    private String nimStudent;

    @FXML
    private TableView<Loan> borrowingHistoryTable;

    @FXML
    private TableColumn<Loan, String> bookIdColumn;

    @FXML
    private TableColumn<Loan, String> titleColumn;

    @FXML
    private TableColumn<Loan, String> statusColumn;

    @FXML
    private TableColumn<Loan, String> dateBorrowedColumn;

    @FXML
    private TableColumn<Loan, String> dateReturnedColumn;

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
    private void initialize() {
        //borrowed activities table
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dateReturnedColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        //student table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        picColumn.setCellValueFactory(new PropertyValueFactory<>("pic"));
        facultyColumn.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
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

    // Method to set the NIM of the logged-in student
    public void setNimStudent(String nimStudent) {
        this.nimStudent = nimStudent;
        loadStudentInfo();
        loadBookBorrowingHistory();
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
