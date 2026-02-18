package org.csps.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    /**
     * send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@csps.edu");
            
            mailSender.send(message);
            log.info("email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("failed to send email to: {}, error: {}", to, e.getMessage());
            throw new RuntimeException("failed to send email", e);
        }
    }
    
    /**
     * send html email
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("noreply@csps.edu");
            
            mailSender.send(message);
            log.info("html email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("failed to send html email to: {}, error: {}", to, e.getMessage());
            throw new RuntimeException("failed to send html email", e);
        }
    }
    
    /**
     * send email verification link
     */
    public void sendVerificationEmail(String to, String userName, String verificationLink) {
        String subject = "Email Verification - CSPS Account";
        String htmlBody = buildVerificationEmailTemplate(userName, verificationLink);
        sendHtmlEmail(to, subject, htmlBody);
    }
    
    /**
     * send password recovery email
     */
    public void sendPasswordRecoveryEmail(String to, String userName, String recoveryLink) {
        String subject = "Password Recovery - CSPS Account";
        String htmlBody = buildPasswordRecoveryEmailTemplate(userName, recoveryLink);
        sendHtmlEmail(to, subject, htmlBody);
    }
    
    /**
     * build verification email template
     */
    private String buildVerificationEmailTemplate(String userName, String verificationLink) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #007bff;">Email Verification Required</h2>
                    
                    <p>Hello <strong>%s</strong>,</p>
                    
                    <p>Thank you for registering with CSPS. Please verify your email address by clicking the link below:</p>
                    
                    <div style="margin: 30px 0;">
                        <a href="%s" style="background-color: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 4px; display: inline-block;">Verify Email</a>
                    </div>
                    
                    <p>Or copy and paste this link in your browser:</p>
                    <p style="background-color: #f5f5f5; padding: 10px; word-break: break-all; font-size: 12px;">%s</p>
                    
                    <p>This link expires in 24 hours.</p>
                    
                    <hr style="border: none; border-top: 1px solid #ddd; margin: 20px 0;">
                    <p style="font-size: 12px; color: #666;">If you did not register for this account, please ignore this email.</p>
                </div>
            </body>
            </html>
            """.formatted(userName, verificationLink, verificationLink);
    }
    
    /**
     * build password recovery email template
     */
    private String buildPasswordRecoveryEmailTemplate(String userName, String recoveryLink) {
        return """
            <html>
            <body style="margin: 0; padding: 0; background-color: #f4f2f7; font-family: 'Segoe UI', Arial, sans-serif;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f2f7; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.06);">

                                <!-- Purple accent bar -->
                                <tr>
                                    <td style="height: 4px; background: linear-gradient(90deg, #7c3aed, #a855f7);"></td>
                                </tr>

                                <!-- Content -->
                                <tr>
                                    <td style="padding: 48px 40px 40px;">

                                        <!-- Heading -->
                                        <h1 style="margin: 0 0 8px; font-size: 22px; font-weight: 700; color: #1a1a2e; letter-spacing: -0.02em;">
                                            Reset your password
                                        </h1>

                                        <p style="margin: 0 0 32px; font-size: 14px; color: #6b7280; line-height: 1.6;">
                                            Hello <strong style="color: #1a1a2e;">%s</strong>, we received a request to reset your password. Click the button below to create a new one.
                                        </p>

                                        <!-- CTA button -->
                                        <table role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 auto 32px;" align="center" width="100%%">
                                            <tr>
                                                <td align="center" style="border-radius: 12px; background: linear-gradient(135deg, #7c3aed, #a855f7);">
                                                    <a href="%s" target="_blank" style="display: block; padding: 16px 32px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none; letter-spacing: 0.01em;">
                                                        Reset password
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>

                                        <!-- Fallback link -->
                                        <p style="margin: 0 0 8px; font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.08em; color: #9ca3af;">
                                            Or copy this link
                                        </p>
                                        <p style="margin: 0 0 32px; font-size: 12px; color: #7c3aed; word-break: break-all; line-height: 1.5; background-color: #faf8ff; border: 1px solid #ede9fe; border-radius: 10px; padding: 12px 16px;">
                                            %s
                                        </p>

                                        <!-- Expiry notice -->
                                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom: 32px;">
                                            <tr>
                                                <td style="background-color: #faf8ff; border: 1px solid #ede9fe; border-radius: 10px; padding: 16px 20px;">
                                                    <p style="margin: 0 0 4px; font-size: 12px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.08em; color: #7c3aed;">
                                                        Important
                                                    </p>
                                                    <p style="margin: 0; font-size: 13px; color: #6b7280; line-height: 1.5;">
                                                        This link expires in <strong style="color: #1a1a2e;">1 hour</strong> and can only be used once.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>

                                        <!-- Divider -->
                                        <hr style="border: none; border-top: 1px solid #f0ecf9; margin: 0 0 24px;" />

                                        <!-- Footer -->
                                        <p style="margin: 0; font-size: 12px; color: #9ca3af; line-height: 1.5;">
                                            If you didn't request a password reset, you can safely ignore this email. Your account remains secure.
                                        </p>

                                    </td>
                                </tr>
                            </table>

                            <!-- Brand footer -->
                            <p style="margin: 24px 0 0; font-size: 11px; color: #9ca3af; letter-spacing: 0.05em;">
                                CSPS &mdash; Computer Studies and Programming Society
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName, recoveryLink, recoveryLink);
    }
}
