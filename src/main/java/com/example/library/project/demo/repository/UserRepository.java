package com.example.library.project.demo.repository;
import com.example.library.project.demo.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    @Query(value = "SELECT * FROM User u WHERE u.username = ?1", nativeQuery = true)
    Collection<User> findUserByUsername(String username);
}
