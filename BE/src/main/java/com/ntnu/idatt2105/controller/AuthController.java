package com.ntnu.idatt2105.controller;

import com.ntnu.idatt2105.model.UserDetails;
import com.ntnu.idatt2105.service.SecurityService;
import com.ntnu.idatt2105.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import com.ntnu.idatt2105.aop.TokenRequired;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller to authenticate a user when the user tries to login
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    SecurityService securityService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Endpoint to login and authenticate the user
     * @param userDetails
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDetails userDetails){
        if(userDetails == null){
            logger.error("User details cannot be null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User details cannot be null");
        }
        String email = userDetails.getEmail();
        String password = userDetails.getPassword();

        try{
            if(userService.isValid(email, password)){
                String role = userService.getRole(email).getName();
                long userId = userService.getUserByEmail(email).getId();

                String token = "Bearer " + securityService.createToken(userId, role);
                MultiValueMap<String, String> headers = new HttpHeaders(); // HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", token); // JWT should be in the header of the HTTP response
                headers.add("Access-Control-Expose-Headers", "Authorization");
                logger.info("Successfully logged in");
                return new ResponseEntity<Object>(headers, HttpStatus.OK);
            }else{
                logger.warn("Not a valid username/password combo");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not a valid username/password combo.");
            }
        }catch(IllegalArgumentException e){
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Method to retreive the subject(user ID) from the users token
     * @return
     */
    @GetMapping("/sub")
    @TokenRequired
    public ResponseEntity<?> getSubject(){
        try{
            ServletRequestAttributes reqAttributes = (ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes();
            HttpServletRequest request = reqAttributes.getRequest();
            // checks for token in request header
            String token = request.getHeader("Authorization");
            try {
                if (token.startsWith("Bearer")) {
                    token = token.substring("Bearer ".length());
                }
            } catch (NullPointerException e) {
                System.out.println("Header is null");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header is null");
            }
            String subject = securityService.getSubject(token);
            logger.info("Subject: " + subject);
            return new ResponseEntity<Object>(subject, HttpStatus.OK);
        }catch(Exception e){
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Method to retrieve a users role based on the token in the header in the 
     * reqeust.
     * @return
     */
    @GetMapping("/role")
    @TokenRequired
    public ResponseEntity<?> getRole() {
        try{
            ServletRequestAttributes reqAttributes = (ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes();
            HttpServletRequest request = reqAttributes.getRequest();
            // checks for token in request header
            String token = request.getHeader("Authorization");
            try {
                if (token.startsWith("Bearer")) {
                    token = token.substring("Bearer ".length());
                }
            } catch (NullPointerException e) {
                System.out.println("Header is null");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header is null");
            }
            String role = securityService.getRole(token);
            return new ResponseEntity<Object>(role, HttpStatus.OK);
        }catch(Exception e){
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }  
    }
}
