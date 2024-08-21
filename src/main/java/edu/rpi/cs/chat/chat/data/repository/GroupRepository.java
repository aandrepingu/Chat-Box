package edu.rpi.cs.chat.chat.data.repository;

import edu.rpi.cs.chat.chat.data.models.Group;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * the repository for groups
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    /**
     * gets all groupchat ids from a user
     * @param user the user to search from
     * @return the list of all groupchat ids a user is apart of
     */
    @Query(value = "SELECT groupId FROM groups m WHERE m.getUser = ?1", nativeQuery = true)
    List<Integer> getAllIDS(String user);

    /**
     * gets all users from a groupchat id
     * @param id the id to search from
     * @return the list of all users apart of id's groupchat 
     */
    @Query(value = "SELECT m.username FROM groups m WHERE m.groupId = ?1", nativeQuery = true)
    List<String> getAllUsers(Integer id);

}
