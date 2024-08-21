package edu.rpi.cs.chat.chatapp;

import jakarta.json.JsonObject;
import java.net.ConnectException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * controller to help logging in and registering as a new user
 */
public class LoginController {

  /**
   * login controller default constructor
   */
  public LoginController() {
  }

  @FXML
  private TextField UsernameField;
  @FXML
  private PasswordField PasswordField;

  /**
   * Attempts to log in using inputted username and password by connecting to the
   * web socket server.
   * 
   * @throws IOException if there is an error opening either the register window
   *                     or the main chat application window.
   */
  @FXML
  public void Login() throws IOException {
	try {
    String username = UsernameField.getText();
    String password = PasswordField.getText();
    if(username.isEmpty()) {
    	ErrorWindow.displayErrorWindow("Not a valid username. Please try again!");
    	return;
    }
    if (password.isEmpty()) {
      ErrorWindow.displayErrorWindow("Please enter password.");
      return;
    }
    URL url = null;
    url = new URL("http://localhost:8080/is-user/" + username + "/" + password);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    // read in the response
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    // get result of GET request as boolean
    boolean result = Boolean.parseBoolean(response.toString());

    if (!result) {
      ErrorWindow.displayErrorWindow("Incorrect username or password. ");
    } else {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("chats.fxml"));
      Parent root = loader.load();
      Stage newWindow = new Stage();
      newWindow.setTitle("Chats");
      newWindow.setScene(new Scene(root));
      newWindow.show();

      ChatController c = (ChatController) loader.getController();
      c.passUsername(username);
      newWindow.setOnCloseRequest(event -> {
        c.closeConnection();
        Platform.exit();
      });

      Stage currentWindow = (Stage) UsernameField.getScene().getWindow();
      currentWindow.close();
      System.out.println("WORKED!");
    }
    
	}catch(Exception e) {
		ErrorWindow.displayErrorWindow("Failed to connect to the server.");
	}

  }

  /**
   * Attempts to register a new user.
   * 
   * @throws IOException io exception when failed to connect to http
   */
  @FXML
  public void Register() throws IOException {
	  try {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("register.fxml"));
    DialogPane registerDialog = fxmlLoader.load();

    // set the values for the dialog
    RegisterController registerController = (RegisterController) fxmlLoader.getController();
    Dialog<ButtonType> dialog = new Dialog<ButtonType>();
    dialog.setDialogPane(registerDialog);
    dialog.setTitle("Register");

    Optional<ButtonType> clickedButton = dialog.showAndWait();
    // obtain the values from the user
    if (clickedButton.get() == ButtonType.OK) {
      String username = registerController.getUsername();
      String password = registerController.getPassword();

      if (username.isEmpty() || username.matches(".*[^a-zA-Z0-9].*")) {
        ErrorWindow.displayErrorWindow("Not a valid username. Please try again!");
        return;
      }
      if (password.isEmpty()) {
        ErrorWindow.displayErrorWindow("Not a valid password. Please try again!");
        return;
      }

      URL users = new URL("http://localhost:8080/is-user/" + username);
      HttpURLConnection usersConnection = (HttpURLConnection) users.openConnection();
      usersConnection.setRequestMethod("GET");

      // read in the response
      BufferedReader in = new BufferedReader(new InputStreamReader(usersConnection.getInputStream()));
      StringBuilder response = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      // get result of GET request as boolean
      boolean exists = Boolean.parseBoolean(response.toString());
      if (!exists) {
        String data = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        URL url = new URL("http://localhost:8080/new-user");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
          byte[] input = data.getBytes("UTF-8");
          os.write(input, 0, input.length);
          os.flush();
        }
        /* DOES NOT WORK WITHOUT */
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
          StringBuilder res = new StringBuilder();
          String responseLine = null;
          while ((responseLine = br.readLine()) != null) {
            res.append(responseLine.trim());
          }
          System.out.println(res.toString());
        }

        /* Registration is successful, show a successful dialog*/
        ErrorWindow.displaySuccessful("Successfully created an account. You may now login!");

      } else {
        ErrorWindow.displayErrorWindow("Username already exists!");
      }

    }
  }catch(Exception e) {
	  ErrorWindow.displayErrorWindow("Failed to connect to the server.");
  }
  }

}
