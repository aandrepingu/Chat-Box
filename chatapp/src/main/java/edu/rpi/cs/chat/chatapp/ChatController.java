package edu.rpi.cs.chat.chatapp;

import jakarta.json.*;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;


/**
 * Controller class for the main chat window.
 *
 * @author lungua
 */
public class ChatController {

    /**
     * chat controller default constructor
     */
    public ChatController() {
    }

    @FXML
    private ListView<String> chatContainer;
    private ObservableList<String> existingChats;
    private String username;
    private String currentlyDisplayedGroup;

    @FXML
    private VBox chatsDisplay;

    // NOTE: assume that all messages are of the form <user>:<text>
//    private HashMap<String, ArrayList<String>> messages;
    private HashMap<Integer, ArrayList<String>> groups;
    private HashMap<Integer, String> groupIDToName;
    private HashMap<String, Integer> groupNameToID;
    private HashSet<Integer> groupsOwned;

    @FXML
    private Button newChatButton, sendMessageButton;
    @FXML
    private TextField newGroupName, chatMessageField;


    @FXML
    private ScrollPane chatScrollPane;

    private ChatWebSocketEndpoint endpoint;

    @FXML
    void initialize() {
        endpoint = null;
        groups = new HashMap<>();
        groupIDToName = new HashMap<>();
        groupNameToID = new HashMap<>();
        groupsOwned = new HashSet<>();
        chatMessageField.setVisible(false);
        sendMessageButton.setVisible(false);
        existingChats = FXCollections.observableArrayList();
        chatScrollPane.vvalueProperty().bind(chatsDisplay.heightProperty());

        // Context menu for adding user to chat
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addUserItem = new MenuItem("Add User");
        addUserItem.setOnAction(event -> openAddUserWindow());
//        MenuItem changeName = new MenuItem("Change Group Name");
//        changeName.setOnAction(event -> openChangeNameWindow());
        contextMenu.getItems().add(addUserItem);
//        contextMenu.getItems().add(changeName);
        chatContainer.setContextMenu(contextMenu);

    }


    /**
     * Upon clicking the add user option in chatContainer's context window, display window to add new user to a group.
     */
    private void openAddUserWindow() {
        // TODO add code to open a new window to add user to a group using addUserToGroup
        try {
            String group = this.chatContainer.getSelectionModel().getSelectedItem();
            // should only open the new window if we have selected a chat
            if (group != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("addtogroup.fxml"));
                Parent root = loader.load();
                Stage newWindow = new Stage();
                newWindow.setTitle("Add User");
                newWindow.setScene(new Scene(root));

                AddUserController c = (AddUserController) loader.getController();
                c.pass(group, this, username);
                newWindow.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    /**
//     * Upon clicking the change group name option, open a new window that allows configuring the details of the group
//     */
//    private void openChangeNameWindow() {
//        try {
//            String group = this.chatContainer.getSelectionModel().getSelectedItem();
//            // should only open the new window if we have selected a chat
//            if (group != null) {
//                // TODO: Open new window for configuring group details
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("addtogroup.fxml"));
//                Parent root = loader.load();
//                Stage newWindow = new Stage();
//                newWindow.setTitle("Add User");
//                newWindow.setScene(new Scene(root));
//
//                AddUserController c = (AddUserController) loader.getController();
//                c.pass(group, this, username);
//                newWindow.show();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Communicates the user's username to this window in order to get and display the user's chats.
     *
     * @param username this user's username.
     */
    public void passUsername(String username) {
        this.username = username;
        try {
            // connect to websocket and get user's chats
            getUserChatsInit();

            URI websocket = new URI("ws://localhost:8080/msg/" + username);


            this.endpoint = new ChatWebSocketEndpoint(websocket, this);

//            ContainerProvider.getWebSocketContainer().connectToServer(this.endpoint, websocket);


        } catch (URISyntaxException e) {
            e.printStackTrace();
            ErrorWindow.displayErrorWindow("bad URI syntax; stacktrace printed");
        }

    }

    /**
     * Creates a new chat for the user using the entered username. This is done via a series of GET/POST requests
     *
     * @throws IOException
     */
    @FXML
    private void addNewChat() throws IOException {
        // execute is-user/ + newChatUsername.getText() request
        String newGroup = newGroupName.getText().trim();
        if (newGroup.isBlank()) {
            this.newGroupName.clear();
            return;
        }
        if(newGroup.matches(".*[^a-zA-Z0-9].*")) {
        	this.newGroupName.clear();
        	ErrorWindow.displayErrorWindow("Failed to create group.");
        	return;
        }

        if (this.existingChats.contains(newGroup)) {
            // do not add duplicate users
        	this.newGroupName.clear();
            ErrorWindow.displayErrorWindow("Group name already exists.");
            return;
        }

        /** Create a new group **/
        Integer groupID = this.createGroup(newGroup);
        this.newGroupName.clear();
        if (groupID == null) {
            ErrorWindow.displayErrorWindow("Failed to create group.");
            return;
        }

    }


    /**
     * Execute GET request and determine if desiredUser is a valid user or not.
     *
     * @param desiredUser username.
     * @return true if desiredUser is an existing user, false otherwise.
     */
    private boolean verifyUser(String desiredUser) {
        try {
            URL checkUserIsValid = new URL("http://localhost:8080/is-user/" + desiredUser);
            HttpURLConnection connection = (HttpURLConnection) checkUserIsValid.openConnection();
            connection.setRequestMethod("GET");


            // parse response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return Boolean.parseBoolean(response.toString());
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }

    /**
     * Execute GET request to create a new group with title groupName (equal to a username if this is a 1 person chat)
     *
     * @param groupName desired name of group; can be a username of another chatter.
     * @return ID of newly created group.
     */
    private Integer createGroup(String groupName) {
        // TODO Auto-generated method stub
        Integer groupID = null;
        try {
            URL newGroup = new URL("http://localhost:8080/new-gc/" + this.username + "/" + groupName);
            HttpURLConnection connection = (HttpURLConnection) newGroup.openConnection();
            connection.setRequestMethod("GET");


            // parse response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine = null;
            ;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            try {
                groupID = Integer.parseInt(response.toString());
            } catch (NumberFormatException e) {
                ErrorWindow.displayErrorWindow("Error in parsing group id from GET req");
//                e.printStackTrace();
                return null;
            }

            URL addMember = new URL("http://localhost:8080/add-member");
            HttpURLConnection addMemberConnection = (HttpURLConnection) addMember.openConnection();
            addMemberConnection.setRequestMethod("POST");


            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("username", groupName);
            builder.add("groupId", groupID);
            String jsonString = builder.build().toString();

            addMemberConnection.setRequestMethod("POST");
            addMemberConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            addMemberConnection.setRequestProperty("Accept", "application/json");
            addMemberConnection.setDoInput(true);
            addMemberConnection.setDoOutput(true);

            try (OutputStream os = addMemberConnection.getOutputStream()) {
                byte[] input = jsonString.getBytes("UTF-8");
                os.write(input, 0, input.length);
                os.flush();
            }
            /* DOES NOT WORK WITHOUT */
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(addMemberConnection.getInputStream(), "utf-8"))) {
                StringBuilder res = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    res.append(responseLine.trim());
                }
            }


            // add this group to everything and update chatContainer
            this.groups.put(groupID, new ArrayList<>());
            this.groupIDToName.put(groupID, groupName);
            this.groupNameToID.put(groupName, groupID);
            this.existingChats.add(groupName);
            this.groupsOwned.add(groupID);
            chatContainer.setItems(existingChats);
        } catch (IOException e) {
        	ErrorWindow.displayErrorWindow("Failed to create group.");
        	return null;
        }
        return groupID;
    }

    /**
     * Execute POST request to add desiredUser to the group named groupName. Dealing with group names is easier
     * than IDs because names are displayed on the app. Assumes this user is the owner of the group; this should be checked by the caller
     *
     * @param desiredUser user to add to group
     * @param groupName   name of group to which desiredUser should be added
     */
    void addUserToGroup(String desiredUser, String groupName) {
        try {
        	if(desiredUser.equals(this.username)) {
        		ErrorWindow.displayErrorWindow(groupName);
        		return;
        	}

            // check if user actually exists first
            if (!this.verifyUser(desiredUser)) {
                ErrorWindow.displayErrorWindow("User does not exist.");
                return;
            }


            if (!this.groupNameToID.containsKey(groupName)) {
                ErrorWindow.displayErrorWindow("ERROR: " + groupName + " NOT PRESENT IN groupNameToID");
                return;
            }
            URL addDesiredUser = new URL("http://localhost:8080/add-member");
            String data = "{\"username\": \"" + desiredUser + "\", \"groupId\": \"" + this.groupNameToID.get(groupName) + "\"}";
            HttpURLConnection connection = (HttpURLConnection) addDesiredUser.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes("UTF-8");
                os.write(input, 0, input.length);
                os.flush();
            }
            /* DOES NOT WORK WITHOUT */
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder res = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    res.append(responseLine.trim());
                }
//                System.out.println("ADDING USER TO GROUP RESPONSE: " + res.toString()); // group id
            }
        } catch (IOException e) {
//            e.printStackTrace();
            return;
        }
    }


    /**
     * Get and display this user's chats upon app start.
     */
    private void getUserChatsInit() {
        try {
            // GET all groups
            URL getGroups = new URL("http://localhost:8080/groups/" + this.username);
            HttpURLConnection connection = (HttpURLConnection) getGroups.openConnection();
            connection.setRequestMethod("GET");
            // parse response
            JsonReader responseReader = Json.createReader(connection.getInputStream());
            JsonArray groupsArray = responseReader.readArray();
            System.out.println(groupsArray.toString());

            for (JsonObject groupObj : groupsArray.getValuesAs(JsonObject.class)) {
//	        	long time = chatObj.getInt("timeReceived");
                Integer groupID = groupObj.getInt("groupID");
                String groupName = groupObj.getString("groupName");
                String groupOwner = groupObj.getString("groupOwner");
                System.out.println("RECEIVED GROUP ID: " + groupID);

                /* Save group if it does not exist already */
                if (!this.groups.containsKey(groupID)) {
                    this.groups.put(groupID, new ArrayList<>());
                    this.groupIDToName.put(groupID, groupName);
                    this.groupNameToID.put(groupName, groupID);
                    if (groupOwner.equals(this.username)) {
                        this.groupsOwned.add(groupID);
                    }
                }

                this.existingChats.add(groupName);

                // GET all chats for this group
                URL getMsgs = new URL("http://localhost:8080/group-history/" + groupID);
                HttpURLConnection msgConn = (HttpURLConnection) getMsgs.openConnection();
                msgConn.setRequestMethod("GET");
                JsonReader msgsResponseReader = Json.createReader(msgConn.getInputStream());
                JsonArray msgsArray = msgsResponseReader.readArray();

                // add each message to the appropriate group
                for (JsonObject msgObj : msgsArray.getValuesAs(JsonObject.class)) {
                    String from = msgObj.getString("fromUser");
                    String message = msgObj.getString("content");
                    this.groups.get(groupID).add(from + ":" + message);
                }

            }


            // after getting all chats, display all users that this user has chats with.
            chatContainer.setItems(existingChats);

            /**
             * initially do not display any chats; just populate chatsList.
             * User should be able to click on a listview item and display the chat.
             */

        } catch (IOException e) {
            ErrorWindow.displayErrorWindow("Could not execute chat history request.");
//            e.printStackTrace();
        }
    }

    /**
     * Upon clicking the list of chats, display chats associated with a user if double-clicked.
     *
     * @param event mouse event.
     */
    @FXML
    private void displayUserChats(MouseEvent event) {

        String group = this.chatContainer.getSelectionModel().getSelectedItem();

        if (group != null) {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                this.chatMessageField.setVisible(true);
                this.sendMessageButton.setVisible(true);
                System.out.println("displaying chats with group: " + group + " with group id " + this.groupNameToID.get(group));
                this.chatMessageField.setPromptText("Send a chat to " + group + "...");
                this.currentlyDisplayedGroup = group;
                System.out.println("DISPLAYING GROUP: " + group);
                this.displayChat(group);
            }
//            else if (event.getButton() == MouseButton.SECONDARY){
//                ContextMenu menu = new ContextMenu();
//                MenuItem addMember = new MenuItem("Add Member");
//                menu.getItems().add(addMember);
//                menu.show(((Stage) this.chatContainer.getScene().getWindow()).getScene().getWindow(), event.getScreenX(), event.getScreenY());
//                // TODO: Add functionality to when menu item is pressed.
//            }
        }
    }

    /**
     * Display chats that this user has with another.
     *
     * @param group The user whose chats with this user are displayed.
     */
    private void displayChat(String group) {
        this.chatsDisplay.getChildren().clear();
        for (String message : this.groups.get(this.groupNameToID.get(group))) {
            System.out.println(message);
            this.displayOneChatMessage(message);
        }

//        this.chatScrollPane.setVvalue(1.0);
    }

    /**
     * Display one chat message to the application.
     *
     * @param message Message between this user and another user. Must be of the form user:message
     */
    private void displayOneChatMessage(String message) {
        String parsedMessage = message.substring(message.indexOf(':') + 1);
        String sender = message.substring(0, message.indexOf(':'));
        Label senderLabel = new Label(sender);
        senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: gray;");

        Label messageLabel = new Label(parsedMessage);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-padding: 10px; -fx-background-radius: 10px; -fx-border-radius: 10px;");


        HBox messageContainer = new HBox();
        VBox messageBox = new VBox(senderLabel, messageLabel);
        messageBox.setSpacing(2);

        // message sent from me, should be on the right hand side and blue like iphone
        // TODO: add sender name somewhere which will be useful for group chats maybe
        if (sender.equals(this.username)) {
            messageLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 10px; -fx-border-radius: 5px;");
            messageContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            senderLabel.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        } else {
            messageLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 10px; -fx-border-radius: 5px;");
            messageContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            senderLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }
        messageContainer.getChildren().add(messageBox);
        this.chatsDisplay.getChildren().add(messageContainer);
    }


    /**
     * Send the currently typed in message to the currently displayed user.
     */
    @FXML
    private void sendMessage() throws UnsupportedEncodingException {
        String messageContent = this.chatMessageField.getText();

        // message is too long
        if (messageContent.getBytes().length > 480) {
            ErrorWindow.displayErrorWindow("Message too long.");
            return;
        }
        this.chatMessageField.clear();
        System.out.println("attempting to send message: " + messageContent);
        if (messageContent.isBlank()) return;

        /** Build Json **/
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("content", messageContent);
        builder.add("groupId", this.groupNameToID.get(this.currentlyDisplayedGroup));
        String jsonString = builder.build().toString();


        String formattedMessage = this.username + ":" + messageContent;

//        this.messages.get(this.currentChatUser).add(formattedMessage);
        this.groups.get(this.groupNameToID.get(this.currentlyDisplayedGroup)).add(formattedMessage);
        this.displayOneChatMessage(formattedMessage);
        this.endpoint.sendMessage(jsonString);
    }

    /**
     * Parses a JSON message received from the web socket server and displays it.
     *
     * @param JSONMessage JSON string
     */
    public void receiveMessage(String JSONMessage) {
//		Runnable r = ()->{

        System.out.println("RECEIVEMESSAGE CALLED!");
        JsonReader reader = Json.createReader(new StringReader(JSONMessage));
        JsonObject obj = reader.readObject();
        System.out.println("CHATCONTROLLER: RECEIVED JSON OBJECT " + obj.toString());

        String messageContent = obj.getString("content");
        int groupID = obj.getInt("groupId");
        String otherUser = obj.getString("fromUser");

        if (!this.groups.containsKey(groupID)) {
            System.out.println("ADDING GROUPID " + groupID + " TO MY GROUPS");
            this.groups.put(groupID, new ArrayList<>());
            // need group name; otherUser is part of this group so can access their groups
            String groupName = getGroupName(groupID, otherUser);
            System.out.println("GROUP WITH ID " + groupID + " HAS NAME " + groupName);
            this.groupIDToName.put(groupID, groupName);
            this.groupNameToID.put(groupName, groupID);
            /* MUST ADD OTHERWISE THREAD ISSUE WITH JAVAFX THREAD*/
            Platform.runLater(() ->
            {
                this.existingChats.add(groupName);
                chatContainer.setItems(existingChats);
            });
        }

        // message sent from us to the other user, format will be `this.username:message`
        this.groups.get(groupID).add(otherUser + ":" + messageContent);

        // if current chat user is null, there is no other user so set to empty
        if (this.currentlyDisplayedGroup == null) {
            this.currentlyDisplayedGroup = "";
        }


        int messageGroup = obj.getInt("groupId");
        String otherGroup = groupIDToName.get(messageGroup);
        Platform.runLater(() ->
        {
            if (this.currentlyDisplayedGroup.equals(otherGroup)) {
                this.displayOneChatMessage(otherUser + ":" + messageContent);
            } else {
                Platform.runLater(() ->
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Notification");
                    // remove header and make non-blocking
                    alert.initModality(Modality.NONE);
                    alert.initStyle(StageStyle.UNDECORATED);

                    alert.setHeaderText(null);
                    alert.setContentText(otherUser + " (" + groupIDToName.get(groupID) + ") has sent you a message!");
                    alert.show();

                    // place notification in the bottom right of computer screen
                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    Rectangle2D screen = Screen.getPrimary().getVisualBounds();
                    double width = alertStage.getWidth();
                    double height = alertStage.getHeight();
                    double x = screen.getWidth() - width;
                    double y = screen.getMaxY() - height - 30;
                    alertStage.setX(x);
                    alertStage.setY(y);

                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(event -> alert.close());
                    delay.play();
                });
            }

        });
    }


    /**
     * Gets the name of the group with ID groupID which has otherUser as a member.
     *
     * @param groupID   group ID of new group
     * @param otherUser user which is part of the group with ID groupID
     * @return the name of the group with ID groupID that otherUser is a member of, or an empty string if there is no such group.
     */
    private String getGroupName(int groupID, String otherUser) {
        // TODO Auto-generated method stub
        try {
            URL getGroups = new URL("http://localhost:8080/groups/" + otherUser);
            HttpURLConnection connection = (HttpURLConnection) getGroups.openConnection();
            connection.setRequestMethod("GET");
            // parse response
            JsonReader responseReader = Json.createReader(connection.getInputStream());
            JsonArray groupsArray = responseReader.readArray();
            for (JsonObject groupObj : groupsArray.getValuesAs(JsonObject.class)) {
                if (groupObj.getInt("groupID") == groupID) {
                    return groupObj.getString("groupName");
                }
            }
        } catch (IOException e) {
            ErrorWindow.displayErrorWindow("GET request failed for getting group name");
            e.printStackTrace();
        }


        return ""; // something is very wrong if we get here

    }

    /**
     * Logs out of the application, allowing the user to log back in
     */
    @FXML
    private void logout() throws IOException {
        this.closeConnection();

        // reopen login window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Stage newWindow = new Stage();
        newWindow.setTitle("Login");
        newWindow.setScene(new Scene(root));
        newWindow.show();

        Stage currentWindow = (Stage) this.chatContainer.getScene().getWindow();
        ;
        currentWindow.close();
    }

    /**
     * Quits the application.
     */
    @FXML
    private void quit() {

        this.closeConnection();

        Stage currentWindow = (Stage) this.chatContainer.getScene().getWindow();
        currentWindow.close();
    }

    /**
     * Close websocket connection.
     */
    void closeConnection() {
//    	new Thread(()->{
        this.endpoint.close();
        this.endpoint = null;
//    	}).start();
    }

    /**
     * Display help window telling user to go to the manual
     */
    @FXML
    private void displayHelpWindow() {
        ErrorWindow.displayInfoWindow("Please consult the user manual for detailed\nhelp for this application.");
    }


}
