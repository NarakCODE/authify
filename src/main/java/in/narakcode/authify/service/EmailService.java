package in.narakcode.authify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        String subject = "Welcome to Authify! 🎉";
        String htmlContent = loadTemplate("templates/welcome-email.html")
                .replace("{{name}}", name);
        sendHtmlEmail(toEmail, name, subject, htmlContent);
    }

    @Async
    public void sendResetOtpEmail(String toEmail, String otp) {
        String subject = "Reset Your Password - OTP Inside";
        String htmlContent = loadTemplate("templates/reset-password-otp-email.html")
                .replace("{{otp}}", otp);
        sendHtmlEmail(toEmail, "User", subject, htmlContent);
    }

    @Async
    public void sendPasswordResetSuccessEmail(String toEmail, String name) {
        String subject = "Password Reset Successful";
        String htmlContent = loadTemplate("templates/password-reset-success-email.html")
                .replace("{{name}}", name);
        sendHtmlEmail(toEmail, name, subject, htmlContent);
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Verify Your Email Address - OTP Inside";
        String htmlContent = loadTemplate("templates/verification-otp-email.html")
                .replace("{{otp}}", otp);
        sendHtmlEmail(toEmail, "User", subject, htmlContent);
    }

    private String loadTemplate(String templatePath) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load email template: {}", templatePath, e);
            return "<html><body><p>Error loading email template</p></body></html>";
        }
    }

    private void sendHtmlEmail(String toEmail, String toName, String subject, String htmlContent) {
        try {
            // Escape special characters in HTML for JSON
            String escapedHtml = htmlContent
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "")
                    .replace("\r", "");

            String json = String.format(
                    "{\"sender\":{\"name\":\"%s\",\"email\":\"%s\"},\"to\":[{\"email\":\"%s\",\"name\":\"%s\"}],\"subject\":\"%s\",\"htmlContent\":\"%s\"}",
                    fromName, fromEmail, toEmail, toName, subject, escapedHtml);

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
                            toEmail, response.code(), response.body() != null ? response.body().string() : "null");
                }
            }
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
