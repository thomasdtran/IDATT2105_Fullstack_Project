package com.ntnu.idatt2105.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ntnu.idatt2105.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, String>{
    User findByEmail(String email);
    User findById(long id);
    Boolean existsByEmail(String email);
}
