package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    public void sendMail(MailDto dto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject(dto.getSubject());
        helper.setFrom(from);
        helper.setTo(dto.getTo());
        helper.setText(dto.getBody(), true);
        if(dto.getAttachments() != null) {
            for(MultipartFile file : dto.getAttachments()) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }
        mailSender.send(message);
    }
}
