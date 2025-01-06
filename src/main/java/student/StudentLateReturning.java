package student;

import adminstudent.Student;
import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class StudentLateReturning {

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
    private TableColumn<Loan, LocalDate> dateBorrowedColumn;

    @FXML
    private TableColumn<Loan, LocalDate> dueDateColumn;

    @FXML
    private Text totalFineText;

    @FXML
    private RadioButton cashRadioButton;

    @FXML
    private RadioButton transferRadioButton;

    @FXML
    private RadioButton briRadioButton;

    @FXML
    private RadioButton bcaRadioButton;

    @FXML
    private RadioButton bniRadioButton;

    @FXML
    private Button doneButton;

    @FXML
    private Button chooseImageButton;

    private File paymentProof;
    private String nimStudent;

    public void setNimStudent(String nimStudent) {
        this.nimStudent = nimStudent;
        loadStudentInfo();
        loadBookBorrowingHistory();
    }

    @FXML
    private void initialize() {
        initializeStudentInfoTable();
        initializeBorrowingHistoryTable();
        initializeRadioButtons();
        initializeButtons();
    }

    private void initializeStudentInfoTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        picColumn.setCellValueFactory(new PropertyValueFactory<>("pic"));
        facultyColumn.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void initializeBorrowingHistoryTable() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateBorrowedColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
    }

    private void initializeRadioButtons() {
        cashRadioButton.setOnAction(event -> handlePaymentMethodSelection());
        transferRadioButton.setOnAction(event -> handlePaymentMethodSelection());
        briRadioButton.setOnAction(event -> handleBankSelection());
        bcaRadioButton.setOnAction(event -> handleBankSelection());
        bniRadioButton.setOnAction(event -> handleBankSelection());
    }

    private void initializeButtons() {
        chooseImageButton.setOnAction(event -> handleChooseImage());
        doneButton.setOnAction(event -> handleDoneButton());
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

        String query = "SELECT b.bookid, bk.title, b.loandate, b.duedate, b.returndate, b.finepaid, b.fineamount " +
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
                boolean finePaid = rs.getBoolean("finepaid");
                double fineAmount = rs.getLong("fineamount");

                Loan loan = new Loan(nimStudent, bookId, title, loanDate, dueDate, returnDate, finePaid, fineAmount);
                borrowingHistoryTable.getItems().add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handlePaymentMethodSelection() {
        boolean isTransfer = transferRadioButton.isSelected();
        briRadioButton.setDisable(!isTransfer);
        bcaRadioButton.setDisable(!isTransfer);
        bniRadioButton.setDisable(!isTransfer);
        chooseImageButton.setDisable(!isTransfer);

        if (isTransfer) {
            briRadioButton.setSelected(false);
            bcaRadioButton.setSelected(false);
            bniRadioButton.setSelected(false);
        }
    }

    private void handleBankSelection() {
        if (briRadioButton.isSelected()) {
            bcaRadioButton.setSelected(false);
            bniRadioButton.setSelected(false);
        } else if (bcaRadioButton.isSelected()) {
            briRadioButton.setSelected(false);
            bniRadioButton.setSelected(false);
        } else if (bniRadioButton.isSelected()) {
            briRadioButton.setSelected(false);
            bcaRadioButton.setSelected(false);
        }
    }

    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        paymentProof = fileChooser.showOpenDialog(new Stage());
    }

    private void handleDoneButton() {
        if (!cashRadioButton.isSelected() && !transferRadioButton.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Payment Method Error", "Please select a payment method.");
            return;
        }

        if (transferRadioButton.isSelected() && !briRadioButton.isSelected() && !bcaRadioButton.isSelected() && !bniRadioButton.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Bank Selection Error", "Please select a bank for transfer.");
            return;
        }

        if (transferRadioButton.isSelected() && paymentProof == null) {
            showAlert(Alert.AlertType.ERROR, "Payment Proof Error", "Please select an image file as proof of payment.");
            return;
        }

        processPayment();
        updateBorrowingDatabase();
        updateBookStock();
    }

    private void updateBookStock() {
        String updateStockQuery = "UPDATE book SET stock = stock + 1 WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateStockQuery)) {
            for (Loan loan : borrowingHistoryTable.getItems()) {
                stmt.setString(1, loan.getBookId());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Book stock updated successfully for book ID: " + loan.getBookId());
                } else {
                    System.out.println("Failed to update book stock for book ID: " + loan.getBookId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBorrowingDatabase() {
        for (Loan loan : borrowingHistoryTable.getItems()) {
            if (loan.getReturnDate() != null && loan.getReturnDate().isAfter(loan.getDueDate())) {
                // Update return date, fine paid status, and fine amount in database
                updateLoanDataInDatabase(loan.getBookId(), loan.getReturnDate(), true, loan.getFineAmount());
                updateLoanStatusInDatabase(loan.getBookId(), "Returned with fine");
            } else {
                // Update return date and status for on-time returns
                updateLoanDataInDatabase(loan.getBookId(), loan.getReturnDate(), false, 0);
                updateLoanStatusInDatabase(loan.getBookId(), "Returned on time");
            }
        }
    }

    private void processPayment() {
        LocalDate today = LocalDate.now();
        for (Loan loan : borrowingHistoryTable.getItems()) {
            if (loan.getReturnDate() != null && loan.getReturnDate().isAfter(loan.getDueDate())) {
                long fine = calculateFine(loan.getDueDate(), loan.getReturnDate());
                updateTotalFine(loan.getDueDate(), loan.getReturnDate(), loan.getFineAmount());
                saveFinePayment(loan.getBookId(), fine);

                // Update return date, fine paid status, and fine amount in database
                updateLoanDataInDatabase(loan.getBookId(), loan.getReturnDate(), true, fine);
                updateLoanStatusInDatabase(loan.getBookId(), "Returned with fine");
            }
        }

        showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "Your payment has been successfully recorded.");
    }

    private void updateLoanDataInDatabase(String bookId, LocalDate returnDate, boolean finePaid, double fineAmount) {
        String updateQuery = "UPDATE borrowing SET returndate = ?, finepaid = ?, fineamount = ? " +
                "WHERE nimstudent = ? AND bookid = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDate(1, java.sql.Date.valueOf(returnDate));
            stmt.setBoolean(2, finePaid);
            stmt.setDouble(3, fineAmount);
            stmt.setString(4, nimStudent);
            stmt.setString(5, bookId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Loan data updated successfully.");
            } else {
                System.out.println("Failed to update loan data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLoanStatusInDatabase(String bookId, String status) {
        String updateStatusQuery = "UPDATE borrowing SET status = ? WHERE nimstudent = ? AND bookid = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateStatusQuery)) {
            stmt.setString(1, status);
            stmt.setString(2, nimStudent);
            stmt.setString(3, bookId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Loan status updated successfully.");
            } else {
                System.out.println("Failed to update loan status.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveFinePayment(String bookId, long fineAmount) {
        String insertQuery = "INSERT INTO finepayment (nimstudent, bookid, fineamount, paymentdate) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, nimStudent);
            stmt.setString(2, bookId);
            stmt.setLong(3, fineAmount);
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Fine payment recorded successfully.");
            } else {
                System.out.println("Failed to record fine payment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * 500;
    }

    public void updateTotalFine(LocalDate dueDate, LocalDate returnDate, double fineAmount) {
        long fine = calculateFine(dueDate, returnDate);
        totalFineText.setText("Total fine: Rp " + fineAmount);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
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
