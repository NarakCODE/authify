package in.narakcode.authify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Authify");
        message.setText("Dear " + name
                + ",\n\nWelcome to Authify. We are excited to have you on board.\n\nBest regards,\nAuthify Team");
        javaMailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Reset Password");
        message.setText("Dear User,\n\nPlease use the following OTP to reset your password: " + otp
                + "\n\nBest regards,\nAuthify Team");
        javaMailSender.send(message);
    }

    public void sendPasswordResetSuccessEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Password Has Been Reset");
        message.setText("Dear " + name
                + ",\n\nYour password for Authify has been successfully reset.\n\nIf you did not make this change, please contact our support team immediately.\n\nBest regards,\nAuthify Team");
        javaMailSender.send(message);
    }

}
