package edu.rpi.cs.chat.chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * starting point of the fx application
 */
public class Login extends Application {

    /**
     * login default constructor
     */
    public Login() {
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene s = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(s);
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed loading FXML");
            e.printStackTrace();
        }


    }

    /**
     * main method of the fx app
     * @param args arguments to pass in
     */
    public static void main(String[] args) {
        launch();
    }
}
