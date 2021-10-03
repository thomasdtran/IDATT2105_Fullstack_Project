package com.ntnu.idatt2105.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Service class responsible for handling functionality for security.
 */
@Service
public class SecurityService {
    
    public static final String secret = "yL7KmpfukVaGhkeR2kJUYbjt2wIOKRKA";
    public static final long JWT_TOKEN_VALIDITY_SECONDS = 7200;
    private static List<String> tokens = new ArrayList<String>(); // JWTs are just strings, in the end
    /**
     * Creates a JWT token with one claim: the subject. The signs it and returns it.
     *
     * @param subject   Basically, whatever you decide to use as user id.
     * @return a signed JWT.
     */

    public String createToken(long subject, String role) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();

        String secretKey = Base64.getEncoder().encodeToString(secret.getBytes());

        String token = Jwts.builder().setSubject(Long.toString(subject))
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(nowMillis + JWT_TOKEN_VALIDITY_SECONDS * 1000))
        .claim("role", role)
        .signWith(signatureAlgorithm, secretKey).compact();
    
        // At this point, we can add the token to a List (either in this class, or in a
        // singleton)
        // which we can then check against when the aspect is run (see
        // TokenRequiredAspect).
        tokens.add(token);

        return token;
    }

    /**
     * The subject in JWT parlance is, practically speaking, the same thing as the
     * user id.
     *
     * @param token the JWT token
     * @return the user ID (or whatever one has decided to refer to as "subject")
     */
    
    public String getSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    public String getRole(String token) {
        Claims claims = Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token).getBody();

        return claims.get("role").toString();
    }

    /**
     * We simply put all of our tokens in a list, and check to see if the token is
     * in it.
     *
     * @param token the JWT
     * @return true, if the token is in the list of valid tokens, false otherwise.
     */
    
    public boolean validToken(String token) {
        return this.tokens.contains(token);
    }

    /**
     * Invokes when theres a token which is no longer valid, and has to be removed 
     * from the list of valid tokens. 
     * @param token the JWT
     * @return
     */
    public boolean removeToken(String token){
        return this.tokens.remove(token);
    }

}
