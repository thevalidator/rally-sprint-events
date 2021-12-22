///*
// * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package ru.thekrechetofficial.service;
//
//import java.io.File;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.mail.MailException;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//
///**
// * @author theValidator <the.validator@yandex.ru>
// */
//@Service
//public class EmailServiceImpl implements EmailService {
//    
//    private static final String NOREPLY_ADDRESS = "noreply@baeldung.com";
//
//    @Autowired
//    private JavaMailSender emailSender;
//
//    @Autowired
//    private SimpleMailMessage template;
//
//    
//    public void sendSimpleMessage(String to, String subject, String text) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(NOREPLY_ADDRESS);
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
//
//            emailSender.send(message);
//        } catch (MailException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    @Override
//    public void sendSimpleMessageUsingTemplate(String to,
//                                               String subject,
//                                               String ...templateModel) {
//        String text = String.format(template.getText(), templateModel);  
//        sendSimpleMessage(to, subject, text);
//    }
//
//    @Override
//    public void sendMessageWithAttachment(String to,
//                                          String subject,
//                                          String text,
//                                          String pathToAttachment) {
//        try {
//            MimeMessage message = emailSender.createMimeMessage();
//            // pass 'true' to the constructor to create a multipart message
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom(NOREPLY_ADDRESS);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text);
//
//            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
//            helper.addAttachment("Invoice", file);
//
//            emailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
