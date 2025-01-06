package adminstudent;

import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.text.Text;
import java.util.Map;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminStudentController {
    private Map<String, Integer> facultyStudentCounts = new HashMap<>();

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

    @FXML
    private Text totalStudentText;
    @FXML
    private Text ftStudentText;
    @FXML
    private Text febStudentText;
    @FXML
    private Text faiStudentText;
    @FXML
    private Text fppStudentText;
    @FXML
    private Text fkipStudentText;
    @FXML
    private Text fhStudentText;
    @FXML
    private Text fapsiStudentText;
    @FXML
    private Text fikesStudentText;
    @FXML
    private Text fkStudentText;
    @FXML
    private Text fisipStudentText;

    public void initialize() {
        updateFacultyStudentCountsFromDatabase();
        updateTotalStudentCount();
    }

    private void updateFacultyStudentCountsFromDatabase() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String query = "SELECT faculty, COUNT(*) AS student_count FROM student GROUP BY faculty";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String faculty = rs.getString("faculty");
                int count = rs.getInt("student_count");
                facultyStudentCounts.put(faculty, count);
            }

            updateUIWithCounts();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUIWithCounts() {
        ftStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FT", 0)));
        febStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FEB", 0)));
        faiStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FAI", 0)));
        fppStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FPP", 0)));
        fkipStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FKIP", 0)));
        fhStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FH", 0)));
        fapsiStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FAPSI", 0)));
        fikesStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FIKES", 0)));
        fkStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FK", 0)));
        fisipStudentText.setText(String.valueOf(facultyStudentCounts.getOrDefault("FISIP", 0)));

        updateTotalStudentCount();
    }

    private void updateTotalStudentCount() {
        int total = facultyStudentCounts.values().stream().mapToInt(Integer::intValue).sum();
        totalStudentText.setText(String.valueOf(total));
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
    public void handleGoToAdminMenu (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Main Menu Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogOutClick (MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/mainmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Main Menu Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
