package student;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import adminbook.Book;
import database.Database;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class StudentSearchBook {

    @FXML
    private TextField searchField;

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

    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private ObservableList<Book> filteredList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchBooks(newValue.trim());
        });
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

                bookList.add(new Book(id, title, author, genre, stock, cover));
            }

            rs.close();
            statement.close();
            conn.close();

            tableView.setItems(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchBooks(String keyword) {
        filteredList.clear();
        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(book);
            }
        }
        tableView.setItems(filteredList);
    }
}
