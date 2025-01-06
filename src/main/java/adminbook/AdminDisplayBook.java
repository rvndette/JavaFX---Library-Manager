package adminbook;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDisplayBook {

    @FXML
    private AnchorPane anchorPane;

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
        tableView.setItems(bookList);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void handleHomepageClick (MouseEvent mouseEvent) {
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
    public void handleGoToAdminMenu (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogOut (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/mainmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Main Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
