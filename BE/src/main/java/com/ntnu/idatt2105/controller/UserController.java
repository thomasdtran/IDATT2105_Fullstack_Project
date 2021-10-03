package com.ntnu.idatt2105.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.ntnu.idatt2105.aop.TokenRequiredAdmin;
import com.ntnu.idatt2105.model.User;
import com.ntnu.idatt2105.service.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for simple CRUD operations on the user table in the database
 * 
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Endpoint to retrieve all users, will only be accessible by admin
     * @return
     */
    @GetMapping
    @TokenRequiredAdmin
    public ResponseEntity<?> getAllUsers(){
        try{
            List<User> users = userService.getAllUsers();
            logger.info("Sucessfully retrieved all users");
            return new ResponseEntity<>(users, HttpStatus.OK);
        }catch(NullPointerException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Endpoint to create a user, will only be accessible by admin
     * @param user
     * @return
     */
    @PostMapping
    @TokenRequiredAdmin
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try{
            User user1 = userService.createUser(user);
            logger.info("User was created");
            return new ResponseEntity<>(user1.getId(), HttpStatus.CREATED);
        }catch(IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Method for deleting User, will only be accessible by admin.
     * @param id Id of the user to be deleted.
     * @return httpstatus ok if user is deleted, bad_request if not.
     */
    @DeleteMapping("/{id}")
    @TokenRequiredAdmin
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            logger.info("User with id " + id + " was deleted.");
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
