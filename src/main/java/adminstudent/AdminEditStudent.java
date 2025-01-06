package adminstudent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

public class AdminEditStudent {

    @FXML
    private TextField nimField;
    @FXML
    private ComboBox<String> comboBoxFakultas;
    @FXML
    private ComboBox<String> comboBoxJurusan;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField picField;
    private String selectedNIM;

    @FXML
    public void initialize() {
        ObservableList<String> fakultasList = FXCollections.observableArrayList(
                "FT", "FAI", "FPP", "FEB", "FAPSI", "FK", "FH", "FKIP", "FIKES", "FISIP");
        comboBoxFakultas.setItems(fakultasList);

        comboBoxFakultas.setOnAction(event -> updateMajor(comboBoxFakultas.getValue()));
    }

    private void updateMajor(String faculty) {
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
        comboBoxJurusan.setItems(majors);
    }

    public void setSelectedNIM(String nim) {
        this.selectedNIM = nim;
        loadStudentData(nim);
    }

    private void loadStudentData(String nim) {
        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }
            String query = "SELECT * FROM student WHERE nim = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nim);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                nimField.setText(rs.getString("nim"));
                nameField.setText(rs.getString("name"));
                comboBoxFakultas.setValue(rs.getString("faculty"));
                comboBoxJurusan.setValue(rs.getString("major"));
                emailField.setText(rs.getString("email"));
                picField.setText(String.valueOf(rs.getInt("pic")));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Student not found.");
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception", "Database error occurred.");
        }
    }

    @FXML
    private void handleConfirmButtonClick(ActionEvent event) {
        String nim = nimField.getText();
        String name = nameField.getText();
        String faculty = comboBoxFakultas.getValue();
        String major = comboBoxJurusan.getValue();
        String email = emailField.getText();
        int pic = Integer.parseInt(picField.getText());

        if (nim.isEmpty() || name.isEmpty() || faculty == null || major == null || email.isEmpty() || picField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all fields");
            return;
        }

        try {
            Connection conn = Database.getConnection();
            if (conn == null) {
                System.out.println("Failed to connect to the database.");
                return;
            }

            String query = "UPDATE student SET name = ?, faculty = ?, major = ?, email = ?, pic = ? WHERE nim = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, faculty);
            statement.setString(3, major);
            statement.setString(4, email);
            statement.setInt(5, pic);
            statement.setString(6, nim);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student.");
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception", "Database error occurred.");
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
    public void handleAddStudentClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/createstudent.fxml"));
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
    public void handleDisplayStudentClick(MouseEvent mouseEvent) {
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
    public void handleEditStudentClick(MouseEvent mouseEvent) {
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
    public void handleDeleteStudentClick(MouseEvent mouseEvent) {
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
    public void handleHomepageClick(MouseEvent mouseEvent) {
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
