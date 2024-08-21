package edu.rpi.cs.chat.chat.data.models;

import jakarta.persistence.*;

/**
 * stores a message
 */
@Table(name = "messages")
@Entity
public class Message {


    /**
     * the time a message was received
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private long timeReceived;

    /**
     * the content of the message
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private String content;

    /**
     * the id of the msg
     */
    @Id
    @GeneratedValue
    @Basic(optional = false)
    @Column(nullable = false)
    private int id;

    /**
     * from which user the msg was sent from
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private String fromUser;


    /**
     * who receives the msg
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private int groupId;

    /**
     * constructor
     */
    protected Message() {
    }

    /**
     * creates a new msg
     * @param content the content of the msg
     * @param fromUser from which user the msg came from
     * @param groupId to which user the msg is going to
     */
    public Message(String content, String fromUser, int groupId) {
        this.timeReceived = System.currentTimeMillis();
        this.content = content;
        this.fromUser = fromUser;
        this.groupId = groupId;
    }

    /**
     * turns the msg into a string for readability
     * @return the msg
     */
    @Override
    public String toString() {
        return super.toString();
        //return content + " at time " + timeReceived + " id " + id + " from " + fromUser + " to " + toUser;
    }

    /**
     * returns content
     * @return content
     */
    public String getContent() {
        return content;
    }


    /**
     * returns id of msg
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * returns the time the msg was received
     * @return the time msg was received
     */
    public long getTimeReceived() {
        return timeReceived;
    }

    /**
     * returns which user the msg came from
     * @return the from user
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * sets which user the msg came from
     * @param groupId the group msg came from
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * sets who the sender was
     * @param fromUser the sender of the message
     */
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    /**
     * gets who sent the message
     * @return the person who sent the message
     */
    public String getFromUser() {
        return fromUser;
    }
}
