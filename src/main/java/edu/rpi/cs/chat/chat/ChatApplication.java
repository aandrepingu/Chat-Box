package edu.rpi.cs.chat.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * entry point for the server
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "edu.rpi.cs.chat.chat.data.repository")
public class ChatApplication {

    /**
     * default constructor for a chat app
     */

    public ChatApplication() {
    }

    /**
     * main method
     *
     * @param args args to pass
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

}
