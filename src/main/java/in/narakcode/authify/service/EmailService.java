package in.narakcode.authify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    @Value("${brevo.from.name:Authify}")
    private String fromName;

    private final OkHttpClient httpClient = new OkHttpClient();
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Authify";
        String content = "Dear " + name + ",\n\nWelcome to Authify. We are excited to have you on board.\n\nBest regards,\nAuthify Team";
        sendEmail(toEmail, name, subject, content);
    }

    @Async
    public void sendResetOtpEmail(String toEmail, String otp) {
        String subject = "Reset Password";
        String content = "Dear User,\n\nPlease use the following OTP to reset your password: " + otp + "\n\nBest regards,\nAuthify Team";
        sendEmail(toEmail, "User", subject, content);
    }

    @Async
    public void sendPasswordResetSuccessEmail(String toEmail, String name) {
        String subject = "Your Password Has Been Reset";
        String content = "Dear " + name + ",\n\nYour password for Authify has been successfully reset.\n\nIf you did not make this change, please contact our support team immediately.\n\nBest regards,\nAuthify Team";
        sendEmail(toEmail, name, subject, content);
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Verify Your Email";
        String content = "Dear User,\n\nPlease use the following OTP to verify your email: " + otp + "\n\nBest regards,\nAuthify Team";
        sendEmail(toEmail, "User", subject, content);
    }

    private void sendEmail(String toEmail, String toName, String subject, String textContent) {
        try {
            String json = String.format(
                "{\"sender\":{\"name\":\"%s\",\"email\":\"%s\"},\"to\":[{\"email\":\"%s\",\"name\":\"%s\"}],\"subject\":\"%s\",\"textContent\":\"%s\"}",
                fromName, fromEmail, toEmail, toName, subject, textContent.replace("\n", "\\n")
            );

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                .url(BREVO_API_URL)
                .addHeader("api-key", brevoApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    log.info("Email sent successfully to {}", toEmail);
                } else {
                    log.error("Failed to send email to {}. Status: {}, Response: {}",
                        toEmail, response.code(), response.body().string());
                }
            }
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
