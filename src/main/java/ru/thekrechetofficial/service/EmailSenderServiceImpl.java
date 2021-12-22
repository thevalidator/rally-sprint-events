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
package ru.thekrechetofficial.service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    
    private final static String domain = "thekrechetofficial.ru";

    private JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @Override
    public void sendEmail(SimpleMailMessage mail) {
        javaMailSender.send(mail);
    }

    @Async
    @Override
    public void sendTestEmail() {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("noreply@" + domain);
        mail.setTo("ownmirik@gmail.com");
        mail.setSubject("Inform test");
        mail.setText("This is the test email message for your email!");
        javaMailSender.send(mail);

    }

    @Async
    @Override
    public void sendResetPasswordEmail(String recipientEmail, String link) {
        
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            
            helper.setFrom("noreply@" + domain, "Rally Sprint Events");
            helper.setTo(recipientEmail);
            
            
            String subject = "Сброс пароля";
            String content = "<p>Здравствуйте!</p>"
                    + "<p>Было запрошено восстановление пароля для пользователя,"
                    + " зарегистрированного с вашим адресом электронной почты.</p>"
                    + "<p>Для восстановления пароля перейдите по ссылке:</p>"
                    + "<p><a href=\"" + link + "\">Сменить пароль</a></p>"
                    + "<br>"
                    + "<p>Ссылка действительна в течение 24 часов.</p>";
            
            helper.setSubject(subject);
            helper.setText(content, true);
            
            javaMailSender.send(message);
            
        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(EmailSenderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
