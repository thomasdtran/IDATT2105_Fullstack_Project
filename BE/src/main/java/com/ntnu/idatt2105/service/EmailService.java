package com.ntnu.idatt2105.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Class used to send emails
 */
@Component
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    /**
     * Method for sending mail to email
     * @param to email address of user receiving email
     * @param subject Subject of the email
     * @param text content of the email
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("RoomiU & Zectionet Varsler! <noreply@rezroomz.com>");
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
