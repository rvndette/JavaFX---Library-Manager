package visitor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class VisitorMenu {

    @FXML
    private Text welcomeText;

    @FXML
    private Text fullNameText;

    @FXML
    private Text usernameText;

    @FXML
    private Text emailText;

    public void setUserDetails(String fullName, String username, String email) {
        welcomeText.setText("Welcome, " + fullName);
        fullNameText.setText("Full name: " + fullName);
        usernameText.setText("Username: " + username);
        emailText.setText("Email: " + email);
    }

    @FXML
    public void handleSearchBookClick (MouseEvent mouseEvent) {
        handleTextClick(mouseEvent);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/searchbookvisitor.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1000, 1000));
            stage.setTitle("Visitor Search Book Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleTextClick(MouseEvent event) {
        Text clickedText = (Text) event.getSource();
        clickedText.setStyle("-fx-fill: #2e2e2e;");
    }
}
