package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private void handleCreateAccountButton(ActionEvent event) {
        System.out.println("Create Account button clicked!");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/createaccount.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Create Account");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        System.out.println("Login button clicked!");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/loginmenu.fxml"));
            Parent loginMenuRoot = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginMenuRoot, 1400, 800));
            stage.setTitle("Login Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
