package org.example.gdgpage.service.mail;

public interface EmailService {
    void sendEmailVerificationMail(String to, String verificationLink);
    void sendPasswordResetMail(String to, String resetLink);
}
