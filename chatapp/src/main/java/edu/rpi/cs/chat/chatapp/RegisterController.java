package edu.rpi.cs.chat.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * controller to help a user register an account
 */
public class RegisterController {

    /**
     * default constructor
     */
    public RegisterController(){}

    @FXML
    private TextField UsernameText;
    @FXML
    private TextField PasswordText;

    /**
     * returns the username
     * @return the username
     */
    public String getUsername() {
        return UsernameText.getText();
    }

    /**
     * returns the password
     * @return the password
     */
    public String getPassword() {
        return PasswordText.getText();
    }
}
