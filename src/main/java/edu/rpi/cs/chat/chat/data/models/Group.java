package edu.rpi.cs.chat.chat.data.models;

import jakarta.persistence.*;

/**
 * stores groups
 * a.k.a its stores all the user and groupchat relations
 */
@Table(name = "groups")
@Entity
public class Group {
    

    /**
     * the id of the user-groupchat association
     */
    @Id
    @GeneratedValue
    @Basic(optional = false)
    @Column(nullable = false)
    private int id;

    /**
     * the name of a user
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private String username;

    /**
     * id of a GroupChat
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private int groupId;

    /**
     * constructor
     */
    protected Group()
    {
    }

    /**
     * creates a association between a user and a group
     * @param user the username of a user
     * @param groupid the id of a GroupChat
     */
    public Group(String user, int groupid) {
        this.username = user;
        this.groupId = groupid;
    }

    /**
     * returns the name of the user
     * @return the name of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * returns the id of the groupchat
     * @return the id of the groupchat
     */
    public int getGroupChatID() {
        return this.groupId;
    }

    /**
     * returns the id of the group
     * @return the id of the group
     */
    public int getGroupID() {
        return this.id;
    }

    /**
     * sets the group id
     * @param groupId group id to set
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * sets id
     * @param id id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * sets username
     * @param username username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
