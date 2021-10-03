package com.ntnu.idatt2105.aop;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import javax.servlet.http.HttpServletRequest;

import com.ntnu.idatt2105.service.SecurityService;

@Aspect
@Component
public class TokenRequiredAspect {
    private final String ROLE_ADMIN = "ADMIN";
    private final String ROLE_USER = "USER";
    Logger logger = LoggerFactory.getLogger(TokenRequiredAspect.class);

    @Autowired
    SecurityService securityService;
    
    /**
     * User for APIs annoted with TokenRequired so that only tokens with the
     * role "User"
     * @param tokenRequired
     * @throws Throwable
     */
    @Before("@annotation(tokenRequired)")
    public void tokenRequiredWithAnnotation(TokenRequired tokenRequired) throws Throwable {
        validateToken(ROLE_USER, ROLE_ADMIN);
    }

    /**
     * User for APIs annoted with TokenRequiredAdmin so that only tokens
     * with the role "Admin" 
     * @param tokenRequiredAdmin
     * @throws Throwable
     */
    @Before("@annotation(tokenRequiredAdmin)")
    public void tokenRequiredAdminWithAnnotation(TokenRequiredAdmin tokenRequiredAdmin) throws Throwable {
        validateToken(ROLE_ADMIN);
    }

    /**
     * Method to validate a token when theres only one role to check.
     * Usually used to secure requests which is admin-only. 
     */
    public void validateToken(String role) throws Throwable {

        ServletRequestAttributes reqAttributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        HttpServletRequest request = reqAttributes.getRequest();
        // checks for token in request header
        String tokenInHeader = request.getHeader("Authorization");
        try {
            if (tokenInHeader.startsWith("Bearer")) {
                tokenInHeader = tokenInHeader.substring("Bearer ".length());
            }
        } catch (NullPointerException e) {
            logger.error("Header is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header is null");
        }

        if (ObjectUtils.isEmpty(tokenInHeader)) {
            // throw new IllegalArgumentException("Empty token");

            // Instead of using a "more correct" exception here, like
            // IllegalArgumentException,
            // we cheat a little and directly throw a ResponseStatusException. This is due
            // to the fact that otherwise Spring Boot will return a "500 - Internal Server
            // Error". Directly returning the ResponseStatusException here is
            // something I consider OK, even if it's not optimal, given that this advice
            // is exclusively meant to be used in conjunction with Controllers.
            logger.error("Empty token received");
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Empty token");
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(securityService.secret.getBytes()))
                    .parseClaimsJws(tokenInHeader).getBody();

            if (claims == null || claims.getSubject() == null) {
                // throw new IllegalArgumentException("Token Error : Claim is null");
                logger.error("Claim is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Error : Claim is null");
            }
            if (!securityService.validToken(tokenInHeader)) {
                logger.error("Could not find token among valid ones");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Token error: could not find token among valid ones");
                // throw new IllegalArgumentException("Token error: could find token among valid
                // ones");
            }

            // Checks to see if the token has the right role for the given request
            String assertedRole = claims.get("role").toString();
            if (!(assertedRole.equals(role))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized role");
            }
            // Here we probably should have checked the expiration, as well, and if it's too
            // close to
            // expiry, refresh it. If it's after expiry, call SecurityService and tell it to
            // remove the token from the list of valid tokens.

        } catch (SignatureException e) {
            logger.error("SignatureException" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwtException" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("ExipredJwtException" + e.getMessage());
            securityService.removeToken(tokenInHeader);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /**
     * Method to validate a token when theres to roles to check. 
     * Usally used so that a user and admin has the same rights for
     * a certain request.
     * @param role1 
     * @param role2
     * @throws Throwable
     */
    public void validateToken(String role1, String role2) throws Throwable {

        ServletRequestAttributes reqAttributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        HttpServletRequest request = reqAttributes.getRequest();
        // checks for token in request header
        String tokenInHeader = request.getHeader("Authorization");
        logger.info("tokenInHeader: " + tokenInHeader);
        try {
            if (tokenInHeader.startsWith("Bearer")) {
                tokenInHeader = tokenInHeader.substring("Bearer ".length());
            }
        } catch (NullPointerException e) {
            logger.error("Header is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header is null");
        }

        if (ObjectUtils.isEmpty(tokenInHeader)) {
            // throw new IllegalArgumentException("Empty token");

            // Instead of using a "more correct" exception here, like
            // IllegalArgumentException,
            // we cheat a little and directly throw a ResponseStatusException. This is due
            // to the fact that otherwise Spring Boot will return a "500 - Internal Server
            // Error". Directly returning the ResponseStatusException here is
            // something I consider OK, even if it's not optimal, given that this advice
            // is exclusively meant to be used in conjunction with Controllers.
            logger.error("Empty token received");
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Empty token");
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(securityService.secret.getBytes()))
                    .parseClaimsJws(tokenInHeader).getBody();

            if (claims == null || claims.getSubject() == null) {
                // throw new IllegalArgumentException("Token Error : Claim is null");
                logger.error("Claim is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Error : Claim is null");
            }
            if (!securityService.validToken(tokenInHeader)) {
                logger.error("Could not find token among valid ones");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Token error: could not find token among valid ones");
                // throw new IllegalArgumentException("Token error: could find token among valid
                // ones");
            }

            // Checks to see if the token has the right role for the given request
            String assertedRole = claims.get("role").toString();
            if (!(assertedRole.equals(role1) || assertedRole.equals(role2))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized role");
            }
            // Here we probably should have checked the expiration, as well, and if it's too
            // close to
            // expiry, refresh it. If it's after expiry, call SecurityService and tell it to
            // remove the token from the list of valid tokens.

        } catch (SignatureException e) {
            logger.error("SignatureException" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwtException" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("ExipredJwtException" + e.getMessage());
            securityService.removeToken(tokenInHeader);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
