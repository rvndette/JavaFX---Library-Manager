package adminstudent;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import email.EmailContent;
import email.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AdminAddStudent {

    @FXML
    private Button addButton;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField NIMfield;
    @FXML
    private ComboBox<String> facultyComboBox;
    @FXML
    private ComboBox<String> majorComboBox;
    @FXML
    private TextField PICfield;
    @FXML
    private TextField emailField;
    private EmailContent emailStudent = new EmailContent();

    // Reference to the ObservableList in AdminDisplayStudent
    private ObservableList<Student> studentList;

    // Setter method to set the reference to studentList
    public void setStudentList(ObservableList<Student> studentList) {
        this.studentList = studentList;
    }

    @FXML
    public void initialize() {
        facultyComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            updateMajorComboBox(newValue);
        });

        facultyComboBox.setItems(FXCollections.observableArrayList("FT", "FAI", "FPP", "FEB", "FAPSI", "FK", "FH", "FKIP", "FIKES", "FISIP"));
    }

    private void updateMajorComboBox(String faculty) {
        ObservableList<String> majors = FXCollections.observableArrayList();

        switch (faculty) {
            case "FT":
                majors.addAll("Teknik Informatika", "Teknik Mesin", "Teknik Sipil", "Teknik Industri", "Teknik Elektro");
                break;
            case "FAI":
                majors.addAll("Pendidikan Agama Islam", "Hukum Keluarga Islam", "Ekonomi Syariah", "Pendidikan Bahasa Arab");
                break;
            case "FPP":
                majors.addAll("Agribisnis", "Agroteknologi", "Teknologi Pangan", "Kehutanan", "Peternakan", "Akuakultur");
                break;
            case "FEB":
                majors.addAll("Manajemen", "Akuntansi", "Ekonomi Pembangunan", "Keuangan dan Perbankan");
                break;
            case "FAPSI":
                majors.addAll("Psikologi");
                break;
            case "FK":
                majors.addAll("Kedokteran");
                break;
            case "FH":
                majors.addAll("Hukum");
                break;
            case "FKIP":
                majors.addAll("Pendidikan Matematika", "Pendidikan Biologi", "Pendidikan Bahasa Indonesia", "Pendidikan PPKN", "Pendidikan Bahasa Inggris", "Pendidikan Guru Sekolah Dasar");
                break;
            case "FIKES":
                majors.addAll("Ilmu Keperawatan", "Farmasi", "Fisioterapi");
                break;
            case "FISIP":
                majors.addAll("Ilmu Komunikasi", "Ilmu Pemerintahan", "Hubungan Internasional", "Kesejahteraan Sosial", "Sosiologi");
                break;
            default:
                break;
        }

        majorComboBox.setItems(majors);
        majorComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleAddAccount(ActionEvent event) {
        String fullName = fullNameField.getText();
        String nim = NIMfield.getText();
        String faculty = facultyComboBox.getValue();
        String major = majorComboBox.getValue();
        String email = emailField.getText();
        String picString = PICfield.getText();

        // Validasi NIM harus 15 digit
        if (nim.length() != 15 || !nim.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid NIM", "NIM must be 15 digits long and numeric.");
            return;
        }

        // Validasi PIC harus 8 digit
        if (picString.length() != 8 || !picString.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid PIC", "PIC must be 8 digits long and numeric.");
            return;
        }

        int pic = Integer.parseInt(picString);

        System.out.println("Full Name: " + fullName);
        System.out.println("NIM: " + nim);
        System.out.println("Faculty: " + faculty);
        System.out.println("Major: " + major);
        System.out.println("PIC: " + pic);

        try {
            System.out.println("Connecting to the database...");
            Connection connect = Database.getConnection();
            System.out.println("Connected to the database.");

            String sql = "INSERT INTO student (name, nim, faculty, major, email, pic) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, fullName);
            statement.setString(2, nim);
            statement.setString(3, faculty);
            statement.setString(4, major);
            statement.setString(5, email);
            statement.setInt(6, pic);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Account has been added", "Account has been successfully added.");
                EmailSender.sendEmail(email, "Account Created", emailStudent.emailStudent(fullName, nim));

                // Add new student to the ObservableList
                studentList.add(new Student(fullName, nim, faculty, major, email, String.valueOf(pic)));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add account.");
            }

            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAddStudentClick (MouseEvent mouseEvent) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/com/example/demo/createstudent.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Add Student Page");
            stage.show();
            AdminAddStudent controller = loader.getController();
            controller.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDisplayStudentClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/readstudent.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Display Student Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditStudentClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/updatestudent.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Edit Student Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteStudentClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/deletestudent.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Admin Delete Student Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHomepageClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmanagestudent.fxml"));
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
            stage.setTitle("Admin Homepage");
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
            stage.setTitle("Main Menu Homepage");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
