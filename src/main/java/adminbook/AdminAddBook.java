package adminbook;

import adminstudent.Student;
import database.Database;
import email.EmailSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class AdminAddBook {

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField IDfield;
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

    public void setBookListList(ObservableList<Book> bookList) {
        this.bookList = bookList;
    }

    @FXML
    public void handleGenreSelection() {
        String selectedGenre = genreComboBox.getValue();
        if (selectedGenre != null) {
            IDfield.setText(generateID(selectedGenre));
        }
    }

    public static String generateID(String genre) {
        String prefix = "";
        switch (genre.toLowerCase()) {
            case "fantasy":
                prefix = "fts";
                break;
            case "fiction":
                prefix = "fic";
                break;
            case "languange and culture":
                prefix = "lnd";
                break;
            case "non-fiction":
                prefix = "nfc";
                break;
            case "social science":
                prefix = "ssc";
                break;
            case "science":
                prefix = "sci";
                break;
            case "technology":
                prefix = "tec";
                break;
            case "health and medicine":
                prefix = "ham";
                break;
            case "business and economy":
                prefix = "bae";
                break;
            case "philosophy and religion":
                prefix = "par";
                break;
            case "art":
                prefix = "ahu";
                break;
            default:
                prefix = "gen";
                break;
        }
        int randomNumber = new Random().nextInt(900) + 100;
        return prefix + randomNumber;
    }

    @FXML
    public void initialize() {
        genreComboBox.setItems(FXCollections.observableArrayList("Fiction", "Language and Culture", "Non-Fiction", "Social Science", "Science", "Technology", "Health and Medicine", "Business and Economy", "Philosophy and Religion", "Art"));
    }

    @FXML
    public void handleChooseImage(ActionEvent actionEvent) {
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

    @FXML
    private void handleAddBook(ActionEvent actionEvent) {
        try {
            Connection conn = Database.getConnection();
            String query = "INSERT INTO book (id, title, author, genre, stock, cover) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            String bookID = IDfield.getText();
            if (bookID.isEmpty() && genreComboBox.getValue() != null) {
                bookID = generateID(genreComboBox.getValue());
                IDfield.setText(bookID);
            }

            stmt.setString(1, bookID);
            stmt.setString(2, titleField.getText());
            stmt.setString(3, authorField.getText());
            stmt.setString(4, genreComboBox.getValue());
            stmt.setInt(5, Integer.parseInt(stockField.getText()));

            byte[] coverBytes = null;

            if (selectedImageFile != null) {
                // Convert File to byte[]
                coverBytes = convertFileToBytes(selectedImageFile);
                stmt.setBytes(6, coverBytes);
            } else {
                stmt.setNull(6, java.sql.Types.BLOB);
            }

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book Added", "The book has been successfully added.");
                // Create a new Book object and add it to the bookList
                Book newBook = new Book(IDfield.getText(), titleField.getText(), authorField.getText(),
                        genreComboBox.getValue(), Integer.parseInt(stockField.getText()), coverBytes);
                bookList.add(newBook);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the book.");
            }

            stmt.close();
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

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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