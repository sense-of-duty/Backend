package org.example.gdgpage.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendEmailVerificationMail(String receiver, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setSubject("[GDGoC SKHU] 이메일 인증 안내");
        message.setText(
                "GDGoC 통합 페이지 회원가입을 위해 아래 링크를 클릭하여 이메일 인증을 완료해주세요.\n\n" +
                        verificationUrl + "\n\n" +
                        "링크의 유효기간은 10분입니다."
        );
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetMail(String receiver, String passwordResetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setSubject("[GDGoC SKHU] 비밀번호 재설정 안내");
        message.setText(
                "아래 링크를 클릭하여 비밀번호를 재설정해주세요.\n\n" +
                        passwordResetUrl + "\n\n" +
                        "링크의 유효기간은 10분입니다."
        );
        mailSender.send(message);
    }
}
