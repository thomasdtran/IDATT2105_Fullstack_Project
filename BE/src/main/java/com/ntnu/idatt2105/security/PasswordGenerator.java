package com.ntnu.idatt2105.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {
    /**
     * generatePassword is a static method that can be called from everywhere
     * @return a randomly generated password
     */
    public static String generatePassword(){
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$%&";
        String numbers = "1234567890";
        Random random = new Random();
        StringBuilder newPassword = new StringBuilder();

        List<Character> password = new ArrayList<>(8);

        // Add two charactes from each of the previous defined fields
        for (int i = 0; i <= 2; i++){
            password.add(capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length())));
            password.add(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
            password.add(specialCharacters.charAt(random.nextInt(specialCharacters.length())));
            password.add(numbers.charAt(random.nextInt(numbers.length())));
        }
        // Shuffle the word for security reasons
        Collections.shuffle(password);
        for (char c : password){
            newPassword.append(c);
        }
        return newPassword.toString();
    }


}
