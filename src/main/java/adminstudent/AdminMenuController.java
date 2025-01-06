package adminstudent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Tooltip;

public class AdminMenuController {

    @FXML
    private Label manageBookText;

    @FXML
    private Label manageStudentText;
//    @FXML
//    private Label manageActivityText;

    @FXML
    public void initialize() {
        Tooltip manageBookTooltip = new Tooltip("Klik untuk mengelola buku");
        Tooltip.install(manageBookText, manageBookTooltip);

        Tooltip manageStudentTooltip = new Tooltip("Klik untuk mengelola siswa");
        Tooltip.install(manageStudentText, manageStudentTooltip);
    }

    @FXML
    public void handleManageBookClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmanagebook.fxml"));
            Parent manageBookRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(manageBookRoot, 1400, 800));
            stage.setTitle("Manage Book Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleManageStudentClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/adminmanagestudent.fxml"));
            Parent manageStudentRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(manageStudentRoot, 1400, 800));
            stage.setTitle("Manage Student Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleLogOut(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/mainmenu.fxml"));
            Parent manageStudentRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(manageStudentRoot, 1400, 800));
            stage.setTitle("Main Menu Page");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
