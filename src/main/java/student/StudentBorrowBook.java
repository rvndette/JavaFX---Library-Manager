package student;

//import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import adminbook.Book;
import database.Database;
import email.EmailContent;
import email.EmailSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class StudentBorrowBook {

    @FXML
    private TextField bookIdField;

    @FXML
    private DatePicker loanDatePicker;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, String> idColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> genreColumn;

    @FXML
    private TableColumn<Book, Integer> stockColumn;

    @FXML
    private TableColumn<Book, byte[]> imageColumn;

    @FXML
    private TextField searchField;

    private String nimStudent;
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private ObservableList<Book> allBookList = FXCollections.observableArrayList();
    private EmailContent emailContent;

    private void searchBooks(String keyword) {
        ObservableList<Book> filteredList = FXCollections.observableArrayList();
        for (Book book : allBookList) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(book);
            }
        }
        tableView.setItems(filteredList);
    }

    private void loadBookData() {
        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }
            String query = "SELECT * FROM book";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String genre = rs.getString("genre");
                int stock = rs.getInt("stock");
                byte[] cover = rs.getBytes("cover");

                Book book = new Book(id, title, author, genre, stock, cover);
                bookList.add(book);
                allBookList.add(book);
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("cover"));

        // Set cell factory for image column
        imageColumn.setCellFactory(param -> new TableCell<Book, byte[]>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(byte[] imageData, boolean empty) {
                super.updateItem(imageData, empty);

                if (empty || imageData == null) {
                    setGraphic(null);
                } else {
                    Image image = new Image(new ByteArrayInputStream(imageData));
                    imageView.setImage(image);
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(100);
                    setGraphic(imageView);
                }
            }
        });

        loadBookData();
        tableView.setItems(bookList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchBooks(newValue.trim());
        });

        // Disable dueDatePicker initially
        dueDatePicker.setDisable(true);

        // Add listener to loanDatePicker to automatically set dueDatePicker
        loanDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dueDatePicker.setValue(newValue.plusDays(3));
                dueDatePicker.setDisable(true); // Disable dueDatePicker after setting value
            } else {
                dueDatePicker.setDisable(true);
            }
        });

        // Initialize emailContent
        emailContent = new EmailContent();
    }

    @FXML
    private void handleBorrowBook() {
        String bookId = bookIdField.getText();
        LocalDate loanDate = LocalDate.now(); // Set loan date to today's date
        LocalDate dueDate = loanDate.plusDays(3);

        // Check if the book stock is greater than 0
        Book selectedBook = null;
        for (Book book : allBookList) {
            if (book.getId().equals(bookId)) {
                selectedBook = book;
                break;
            }
        }

        if (selectedBook == null || selectedBook.getStock() <= 0) {
            showAlert("Book ID not found or not available for borrowing!");
            return;
        }

        boolean success = borrowBook(bookId, nimStudent, loanDate, dueDate);
        if (success) {
            showAlert("Book Borrowed Successfully!");

            // Send email to student
            sendBorrowedBookEmail(nimStudent, bookId, loanDate, dueDate);
        } else {
            showAlert("Failed to borrow the book. Please try again later.");
        }
    }

    public void setNimStudent(String nimStudent) {
        this.nimStudent = nimStudent;
    }

    private void sendBorrowedBookEmail(String nim, String bookId, LocalDate loanDate, LocalDate dueDate) {
        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }

            String queryStudent = "SELECT name, email FROM student WHERE nim = ?";
            PreparedStatement stmtStudent = conn.prepareStatement(queryStudent);
            stmtStudent.setString(1, nim);
            ResultSet rsStudent = stmtStudent.executeQuery();
            if (!rsStudent.next()) {
                System.out.println("Student not found for nim: " + nim);
                return;
            }
            String fullName = rsStudent.getString("name");
            String email = rsStudent.getString("email");

            String queryBook = "SELECT title FROM book WHERE id = ?";
            PreparedStatement stmtBook = conn.prepareStatement(queryBook);
            stmtBook.setString(1, bookId);
            ResultSet rsBook = stmtBook.executeQuery();
            if (!rsBook.next()) {
                System.out.println("Book not found for id: " + bookId);
                return;
            }
            String bookTitle = rsBook.getString("title");

            rsStudent.close();
            stmtStudent.close();
            rsBook.close();
            stmtBook.close();
            conn.close();

            String emailContentStr = emailContent.borrowBookEmail(fullName, email, bookTitle, dueDate.toString());
            EmailSender.sendEmail(email, "Book Borrowed", emailContentStr);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean borrowBook(String bookId, String nimStudent, LocalDate loanDate, LocalDate dueDate) {
        String sql = "INSERT INTO borrowing (bookid, nimstudent, loandate, duedate, returndate) VALUES (?, ?, ?, ?, NULL)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookId);
            pstmt.setString(2, nimStudent);
            pstmt.setDate(3, Date.valueOf(loanDate));
            pstmt.setDate(4, Date.valueOf(dueDate));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Update book stock
                String updateQuery = "UPDATE book SET stock = stock - 1 WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, bookId);
                updateStmt.executeUpdate();
                updateStmt.close();

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Ensure dialog fits content
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
