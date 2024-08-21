package edu.rpi.cs.chat.chat.data.models;

import jakarta.persistence.*;

/**
 * stores info about a user
 */
@Table(name = "users")
@Entity
public class User {

    /**
     * the username of the user
     */
    @Id
    @Basic(optional = false)
    @Column(nullable = false)
    private String username;

    /**
     * the password of the user
     */
    @Basic(optional = false)
    @Column(nullable = false)
    private String password;

    /**
     * constructor
     */
    protected User() {
    }

    /**
     * creates a new user
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * return password of user
     * @return password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * returns username of user
     * @return username of user
     */
    public String getUsername() {
        return username;
    }
}