package edu.rpi.cs.chat.chat.data.repository;


import edu.rpi.cs.chat.chat.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * the repository for users
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {


    /**
     * gets if a username exists or not
     * @param user the username to check for
     * @return 1 if username exists, 0 otherwise
     */
    @Query(value = "SELECT COUNT(1) FROM users u WHERE u.username = ?1", nativeQuery = true)
    int getUsernameCount(String user);


}
