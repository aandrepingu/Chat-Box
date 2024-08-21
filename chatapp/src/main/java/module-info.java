/**
 * module info of the app
 */
module edu.rpi.cs.chat.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
	requires javafx.graphics;
    requires jakarta.json;
    requires tyrus.standalone.client;


    opens edu.rpi.cs.chat.chatapp to javafx.fxml;
    exports edu.rpi.cs.chat.chatapp;
}