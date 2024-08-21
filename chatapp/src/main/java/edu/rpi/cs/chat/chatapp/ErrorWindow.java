package edu.rpi.cs.chat.chatapp;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class which displays an error window or an information window.
 * @author lungua
 * @since hw3
 */
public class ErrorWindow {

	/**
	 * error window default constructor
	 */
	public ErrorWindow() {}

	/**
	 * Displays an error message with a message
	 * @author lungua
	 * @since hw3
	 * @param message Error message to display.
	 */
	public static void displayErrorWindow(String message) {
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
	}
	/**
	 * Displays an information window with a message.
	 * @author lungua
	 * @since hw3
	 * @param message Information message to display.
	 */
	public static void displayInfoWindow(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Manual");
        alert.setHeaderText("Manual Information");
        alert.setContentText(message);
        alert.showAndWait();
	}

	/**
	 * Displays a successful dialog along with a custom message.
	 *
	 * @param message The message to dispaly to the user
	 */
	public static void displaySuccessful(String message) {
		Alert successful = new Alert(AlertType.CONFIRMATION);
		successful.setTitle("Registration");
		successful.setHeaderText("Successful");
		successful.setContentText(message);
		successful.showAndWait();
	}
}
