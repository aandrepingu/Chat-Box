package edu.rpi.cs.chat.chat;

import com.google.gson.Gson;
import edu.rpi.cs.chat.chat.data.models.Message;
import edu.rpi.cs.chat.chat.data.repository.GroupRepository;
import edu.rpi.cs.chat.chat.data.repository.MessageRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * the websocket handler
 */
@Component
public class MessageSocketHandler extends TextWebSocketHandler {

    /**
     * the sessions connected to ws
     */
    private final Map<String, WebSocketSession> sessions = new HashMap<>();


    /**
     * the repository of msgs
     */
    private final MessageRepository messageRepo;

    /**
     * the repository of group relationns
     */
    private final GroupRepository groupRepo;

    /**
     * creates a new ws handler
     *
     * @param messageRepo the repository for msgs
     * @param groupRepo the repository for groups
     */
    public MessageSocketHandler(MessageRepository messageRepo, GroupRepository groupRepo) {
        this.messageRepo = messageRepo;
        this.groupRepo = groupRepo;
    }

    /**
     * what happens after connection closed
     * removes the person connected
     *
     * @param session the person who dc
     * @param status  how the dc happened
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    /**
     * Takes in a message and save and notifies other clients
     *
     * @param session the session of the ws
     * @param message the message formated as a Message object
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Message msgTemp = new Gson().fromJson(message.getPayload(), Message.class);
        Message msg = new Message(
                msgTemp.getContent(),
                (String) session.getAttributes().get("username"),
                msgTemp.getGroupId()
        );

        messageRepo.save(msg);

        for (String toUser : groupRepo.getAllUsers(msg.getGroupId())) {
            if (toUser.equals(msg.getFromUser())) continue;

            WebSocketSession to = sessions.get(toUser);

            if (to != null && to.isOpen()) {
                to.sendMessage(new TextMessage(new Gson().toJson(msg)));
            }
        }


        //session.sendMessage(new TextMessage(msg.toString()));
    }

    /**
     * what happens after connected
     * saves person
     *
     * @param session the person who connected
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put((String) session.getAttributes().get("username"), session);
    }
}
