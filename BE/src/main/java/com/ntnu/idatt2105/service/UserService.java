package com.ntnu.idatt2105.service;

import com.ntnu.idatt2105.service.EmailService;
import com.ntnu.idatt2105.security.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ntnu.idatt2105.model.Role;
import com.ntnu.idatt2105.model.User;
import com.ntnu.idatt2105.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class responsible for handling functionality for users.
 */
@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Method for fetching out all users in database, will only be user by Admin
     * @return A list of users
     */
    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    /**
     * Method to getch a user based on email
     * @param email
     * @return
     */
    public User getUserByEmail(String email){
        return userRepo.findByEmail(email);
    }

    /**
     * 
     * @param user
     * @return
     */

    /**
     * Method for creating user
     * @param user data received from client to create user
     * @return user created or throw exception if input is wrong
     */
    public User createUser(User user){
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }else if(user.getEmail() == null){
            throw new IllegalArgumentException("Email cannot be null");
        }
        if(!isLetters(user.getFirstname()) || !isLetters(user.getLastname())) {
            throw new IllegalArgumentException("Firstname or lastname did not contain only letters");
        } else if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new IllegalArgumentException("Email must contain @ and .");
        }

        User checkUser = userRepo.findByEmail(user.getEmail());
        if(checkUser != null){
            throw new IllegalArgumentException("A user with the email already exists");
        }
        user.setValid(true);
        createPassword(user);
        return userRepo.save(user);
    }

    /**
     * Method for deleting user.
     * @param id Id of user to be deleted.
     */
    public void deleteUser(long id) {
        User user = userRepo.findById(id);
        userRepo.delete(user);
    }

    /**
     * Method used to check if input from user contains only letters
     * @param name, name recieved from user
     * @return boolean saying if the input is valid
     */
    public boolean isLetters(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(Character.isLetter(c) || c ==' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to validate if the credentials matches
     * @param email
     * @param password
     * @return
     */
    public boolean isValid(String email, String password){
        if(email == null){
            throw new IllegalArgumentException("Email cannot be null!");
        }
        User user = userRepo.findByEmail(email);
        if(user == null){
            throw new IllegalArgumentException("Email not found");
        }
        //Checks if the user is valid based on the term
        if(!user.isValid()){
            throw new IllegalArgumentException("User no longer valid");
        }
        return encoder.matches(password, user.getPassword());
    }

    public Role getRole(String email){
        User user = userRepo.findByEmail(email);
        return user.getRole();
    }

    public Role getRoleById(long id){
        User user = userRepo.findById(id);
        return user.getRole();
    }

    /**
     * Method to automatically check periodically if its the
     * start or end of a term. Sets a user to valid if its the start of a term,
     * and invalid if its the end of a term. 
     * 
     */
    //@Scheduled(cron = "0 0 5 * * ?") //Checks everyday at 05:00 o´clock
    @Scheduled(cron="@midnight") //Checks daily at midnight
    //Scheduled(cron="0 32 12 * * ?")
    public void checkTerm(){
        logger.info("Checks the term");
        List<User> users = userRepo.findAll();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        try{
            Date springStart = formatter.parse(currentYear + "-01-11");//From January 11
            Date springEnd =  formatter.parse(currentYear + "-06-21");// To June 21

            Date fallStart = formatter.parse(currentYear + "-08-16");//From August 16
            Date fallEnd = formatter.parse(currentYear + "-12-21");//To December 21

            if(!users.isEmpty()){
                //Checks if its the end of a term
                if(formatter.format(today).equals(formatter.format(springEnd))
                || formatter.format(today).equals(formatter.format(fallEnd))){
                    logger.info("Term is over");
                    users.forEach(user -> {
                        if(user.getRole().getName().equalsIgnoreCase("USER")){
                            user.setValid(false);
                            logger.info("User ID: " + user.getId() + " is set to " + user.isValid());
                            userRepo.save(user);
                        }
                    });
                }
                //Checks if its the start of a term
                else if (formatter.format(today).equals(formatter.format(springStart))
                        || formatter.format(today).equals(formatter.format(fallStart))) {
                    logger.info("Currently the start of a semester");
                    users.forEach(user -> {
                        if(user.getRole().getName().equalsIgnoreCase("USER")){
                            user.setValid(true);
                            logger.info("User ID: " + user.getId() + " is set to " + user.isValid());
                            userRepo.save(user);
                        }
                    });
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }

    /**
     * Method for creating password to user
     * @param user User object
     */
    private void createPassword(User user){
        String newPassword = PasswordGenerator.generatePassword();
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        emailService.sendEmail(user.getEmail(), "RoomiU & Zectionet", "Velkommen til RoomiU & Zectionet! Ditt nye passord er: " + newPassword);
    }


}
