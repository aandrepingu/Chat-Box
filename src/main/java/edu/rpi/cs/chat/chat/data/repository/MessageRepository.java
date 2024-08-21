package edu.rpi.cs.chat.chat.data.repository;

import edu.rpi.cs.chat.chat.data.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * repository for messages
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /**
     * gets the messages of a group
     * @param id the group id of the messages you want
     * @return a list of messages that belong to a group
     */
    @Query(value = "SELECT * FROM messages m WHERE m.groupId = ?1 ORDER BY m.timeReceived", nativeQuery = true)
    List<Message> getAllMessages(Integer id);

}
