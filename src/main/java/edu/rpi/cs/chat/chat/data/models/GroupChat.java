package edu.rpi.cs.chat.chat.data.models;

import jakarta.persistence.*;

/**
 * stores groupchats
 * A wrapper of User to forwards messages to group users
 */
@Table(name = "groupchats")
@Entity
public class GroupChat {

    /**
     * the name of the groupchat
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private String groupname;

    /**
     * the id of the groupchat
     */
    @Id
    @GeneratedValue
    @Basic(optional = false)
    @Column(nullable = false)
    private int id;

    /**
     * the owner of the group
     * default is the user that creates the group
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private String owner;

    /**
     * constructor
     */
    protected GroupChat()
    {
    }

    /**
     * creates a new group chat
     * @param groupname name of the group
     * @param owner who owns the group
     */
    public GroupChat(String groupname, String owner) {
        this.groupname = groupname;
        this.owner = owner;
    }

    /**
     * returns the name of the Group
     * @return name of the Group
     */
    public String getGroupName() {
        return this.groupname;
    }

    /**
     * returns the id of the group
     * @return id
     */
    public int getGroupID() {
        return this.id;
    }

    /**
     * returns the owner of the Group
     * @return the owner of the Group
     */
    public String getGroupOwner() {
        return this.owner;
    }

    /**
     * switches the owner of the groupchat
     * @param owner the new owner of the groupchat
     * @return the previous owner of the groupchat
     */
    public String switchOwner(String owner) {
        String loser = this.owner;
        this.owner = owner;
        return loser;
    }

}
