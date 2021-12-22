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

package ru.thekrechetofficial.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * @author theValidator <the.validator@yandex.ru>
 */
//@ComponentScan({"ru.thekrechetofficial.email"})
//@PropertySource(value={"classpath:mail.properties"})
@Configuration
public class EmailConfig {
    
//    @Value("${spring.mail.host}")
    private String mailServerHost = "smtp.DOMAIN";

//    @Value("${spring.mail.port}")
    private Integer mailServerPort = 587;

//    @Value("${spring.mail.username}")
    private String mailServerUsername = "EMAIL";

//    @Value("${spring.mail.password}")
    private String mailServerPassword = "PASSWORD";

//    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailServerAuth = "true";

//    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailServerStartTls = "true";

    
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort);
        
        mailSender.setUsername(mailServerUsername);
        mailSender.setPassword(mailServerPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailServerAuth);
        props.put("mail.smtp.starttls.enable", mailServerStartTls);
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
//    @Bean
//    public SimpleMailMessage templateSimpleMessage() {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("noreply@thekrechetofficial.ru");
//        message.setTo("ownmirik@gmail.com");
//        message.setText("This is the test email template for your email:\n%s\n");
//        return message;
//    }

}
