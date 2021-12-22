/*
 * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.thekrechetofficial.controller;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.thekrechetofficial.service.EmailSenderService;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class EmailSenderController {

    private final EmailSenderService emailSenderService;

    @Autowired
    public EmailSenderController(EmailSenderService emailSender) {
        this.emailSenderService = emailSender;
    }
    
    @GetMapping("/send-test-email")
    public String doSendEmail() {
        
        emailSenderService.sendTestEmail();
//        try {
//            
//            emailSenderService.sendResetPasswordEmail("ownmirik@gmail.com", "reset-link");
//        } catch (MessagingException ex) {
//            System.out.println("ex: " + ex);
//        } catch (UnsupportedEncodingException ex) {
//            System.out.println("ex: " + ex);
//        }
        
        
        // takes input from e-mail form
//        String recipientAddress = request.getParameter("recipient");
//        String subject = request.getParameter("subject");
//        String message = request.getParameter("message");
         
        // prints debug info
//        System.out.println("To: " + recipientAddress);
//        System.out.println("Subject: " + subject);
//        System.out.println("Message: " + message);
         
        // creates a simple e-mail object
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setFrom("noreply@thekrechetofficial.ru");
//        email.setTo("ownmirik@gmail.com");
//        email.setSubject("Test");
//        email.setText("This is the test email template for your email!");
//         
//        // sends the e-mail
//        mailSender.send(email);
         
        //mailSender.
        // forwards to the view named "Result"
        return "index";
    }

    

}
