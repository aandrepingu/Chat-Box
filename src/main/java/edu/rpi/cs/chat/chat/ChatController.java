package edu.rpi.cs.chat.chat;

import edu.rpi.cs.chat.chat.data.models.Group;
import edu.rpi.cs.chat.chat.data.models.GroupChat;
import edu.rpi.cs.chat.chat.data.models.Message;
import edu.rpi.cs.chat.chat.data.models.User;
import edu.rpi.cs.chat.chat.data.repository.GroupChatRepository;
import edu.rpi.cs.chat.chat.data.repository.GroupRepository;
import edu.rpi.cs.chat.chat.data.repository.MessageRepository;
import edu.rpi.cs.chat.chat.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller to manage all post/gets
 */
@RestController
public class ChatController {

    /**
     * Chat Controller default constructor
     */
    public ChatController() {}

    /**
     * the user repository
     */
    @Autowired
    private UserRepository userRepo;

    /**
     * the message repository
     */
    @Autowired
    private MessageRepository messageRepo;

    /**
     * the group repository
     */
    @Autowired
    private GroupRepository groupRepo;

    /**
     * the message repository
     */
    @Autowired
    private GroupChatRepository groupChatRepo;

    /**
     * adds a user to the db
     *
     * @param user the user to add
     */
    @PostMapping("/new-user")
    public void addUser(@RequestBody User user) {
        userRepo.save(user);
    }

    /**
     * gets the users from the db
     *
     * @return all existing users, please do not use this in the actual app
     */
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    /**
     * sees if a username/password combo exists
     *
     * @param username the username to check for
     * @param password the password to check for
     * @return true or false if a username/password exists
     */
    @GetMapping("/is-user/{username}/{password}")
    public boolean isUser(
            @PathVariable("username") String username,
            @PathVariable("password") String password
    ) {
        return userRepo.findOne(Example.of(new User(username, password))).orElse(null) != null;
    }

    /**
     * sees if a username exists or not
     *
     * @param username the username to check for
     * @return whether a username already exists
     */
    @GetMapping("/is-user/{username}")
    public boolean isUsername(
            @PathVariable("username") String username
    ) {
        return (userRepo.getUsernameCount(username) == 1);
    }

    /**
     * creates a new group chat
     * @param username username of the person creating the gc
     * @param gc the group chat name
     * @return the id of the newly created group chat
     */
    @GetMapping("new-gc/{username}/{name}")
    public int createNewGC(
            @PathVariable("username") String username,
            @PathVariable("name") String gc
    ) {
        int gid = groupChatRepo.save(new GroupChat(gc, username)).getGroupID();
        groupRepo.save(new Group(username, gid));

        return gid;
    }

    /**
     * adds a member to the group chat
     * @param member the group chat and member to add
     */
    @PostMapping("add-member")
    public void addMember(
            @RequestBody Group member
    ) {
        groupRepo.save(member);
    }

    /**
     * gets the chat history of a group
     * @param groupId the group id of the chat you want the history of
     * @return a list of the messages from a chat
     */
    @GetMapping("/group-history/{groupId}")
    public List<Message> getGroupHistory(
            @PathVariable("groupId") Integer groupId
    ) {
        return messageRepo.getAllMessages(groupId);
    }

    /**
     * gets all the groups a user belongs to
     * @param un the username of the user
     * @return the list of group chats the user belongs to
     */
    @GetMapping("/groups/{username}")
    public List<GroupChat> getGroups(
            @PathVariable("username") String un
    ) {
        return groupChatRepo.getAllGroups(un);
    }

}
