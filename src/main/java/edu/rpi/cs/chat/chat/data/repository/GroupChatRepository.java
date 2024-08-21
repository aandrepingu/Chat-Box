package edu.rpi.cs.chat.chat.data.repository;

import edu.rpi.cs.chat.chat.data.models.GroupChat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * the repository for groupchats
 */
@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Integer> {

    /**
     * gets all the groups a user belongs to
     * @param user the user to get the groups for
     * @return a list of the groups the user belongs to
     */
    @Query(value = "SELECT gc.* FROM groupchats gc LEFT JOIN groups g ON g.groupId = gc.id WHERE g.username = ?1", nativeQuery = true)
    List<GroupChat> getAllGroups(String user);

}
