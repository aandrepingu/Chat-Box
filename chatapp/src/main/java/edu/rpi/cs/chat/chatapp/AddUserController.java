package edu.rpi.cs.chat.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller for the add user to group window.
 * @author lungua
 */
public class AddUserController {
	private ChatController mainWindow;
	private String groupName;
	private String currentUser;
	@FXML
	private TextField newUserField;
	
	@FXML
	private Text headerText;
	
	void pass(String groupName, ChatController mainWindow, String currentUser) {
		this.groupName = groupName;
		this.currentUser = currentUser;
		headerText.setText("Add user to group: " + groupName);
		this.mainWindow = mainWindow;
	}
	
	/**
	 * Upon clicking the add button we add a user to the specified group
	 */
	@FXML
	private void addUser() {
		String newUser = newUserField.getText();
		if(newUser.isBlank()) {
			ErrorWindow.displayErrorWindow("Please enter a username");
			newUserField.clear();
			return;
		}
		if (newUser.equals(currentUser)) {
			ErrorWindow.displayErrorWindow("Can not add yourself to group. Already in group " + groupName);
			newUserField.clear();
			return;

		}
		this.mainWindow.addUserToGroup(newUser, groupName);
		Stage currentWindow = (Stage) this.headerText.getScene().getWindow();;
        currentWindow.close();
	}
	
	/**
     * Display help window telling user to go to the manual
     */
	
	@FXML
	private void displayHelpWindow() {
		ErrorWindow.displayInfoWindow("Please consult the user manual for detailed\nhelp for this application.");
	}
	
}
