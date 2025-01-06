package adminbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.File;
import java.sql.*;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.FileInputStream;

public class AdminEditBook {

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField IDField;
    @FXML
    private TextField stockField;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private Button chooseImageButton;
    @FXML
    private ImageView bookImageView;
    private File selectedImageFile;
    private ObservableList<Book> bookList;
    private String selectedID;

    public void setBookList(ObservableList<Book> bookList) {
        this.bookList = bookList;
    }

    @FXML
    public void initialize() {
        genreComboBox.setItems(FXCollections.observableArrayList("Classical Fiction", "Contemporary Fiction", "Non-Fiction", "Social Science", "Science", "Technology", "Health and Medicine", "Business and Economy", "Philosophy and Religion", "Art"));
    }

    public void setSelectedID(String ID) {
        this.selectedID = ID;
        loadBookData(ID);
    }

    private void loadBookData(String ID) {
        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }
            String query = "SELECT * FROM book WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                IDField.setText(rs.getString("id"));
                titleField.setText(rs.getString("title"));
                authorField.setText(rs.getString("author"));
                genreComboBox.setValue(rs.getString("genre"));
                stockField.setText(String.valueOf(rs.getInt("stock")));

                // Load the image
                String imagePath = rs.getString("cover");
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        bookImageView.setImage(new Image(new FileInputStream(imageFile)));
                        selectedImageFile = imageFile;
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Book not found.");
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    @FXML
    private void handleConfirmButtonClick(ActionEvent event) {
        String id = IDField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreComboBox.getValue();
        String stock = stockField.getText();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || genre == null || stock.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all fields");
            return;
        }

        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }

            String query = "UPDATE book SET title = ?, author = ?, genre = ?, stock = ?, cover = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, genre);
            statement.setInt(4, Integer.parseInt(stock));
            statement.setString(6, id);

            byte[] coverBytes = null;

            if (selectedImageFile != null) {
                // Convert File to byte[]
                coverBytes = convertFileToBytes(selectedImageFile);
                statement.setBytes(5, coverBytes);
            } else {
                statement.setNull(6, Types.BLOB);
            }

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book Added", "The book has been successfully added.");
                // Create a new Book object and add it to the bookList
                Book newBook = new Book(selectedID, titleField.getText(), authorField.getText(),
                        genreComboBox.getValue(), Integer.parseInt(stockField.getText()), coverBytes);
                bookList.add(newBook);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the book.");
            }

            statement.close();
            conn.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    private byte[] convertFileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] bytesArray = new byte[(int) file.length()];
        fis.read(bytesArray); // read file into bytes[]
        fis.close();
        return bytesArray;
    }

    @FXML
    public void handleChooseImageButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            bookImageView.setImage(image);
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
    public void handleDeleteBookClick(MouseEvent mouseEvent) {
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
    public void handleLogOut(MouseEvent mouseEvent) {
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
}
