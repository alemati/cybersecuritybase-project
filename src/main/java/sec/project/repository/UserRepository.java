package sec.project.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import sec.project.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    @Transactional
    @Modifying  
    @Query(value = "INSERT INTO Account (username, password) VALUES (?1, ?2)", nativeQuery = true)
    void insertInto(String name, String password);
    
//    @Transactional
//    @Modifying  
//    @Query(value = "INSERT INTO Agent (id, name) VALUES ('" + name + "', '" + password + "')", nativeQuery = true)
//    void insertInto(String name, String password);
}

