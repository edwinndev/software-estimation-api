package com.intecx.inscope.service.impl;

import com.intecx.inscope.service.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String LOGO_CONTENT_ID = "appLogo";
    private static final String[] LOGO_CANDIDATES = {
        "logo/intecx-dark-logo.png",
        "logo/intecx-dark-logo.svg"
    };

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfiguration;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode, int expirationMinutes) {
        Map<String, Object> model = new HashMap<>();
        model.put("otpCode", otpCode);
        model.put("expirationMinutes", expirationMinutes);
        sendHtml(toEmail, "Código de verificación - Intecx InScope", "otp.ftlh", model);
    }

    @Override
    public void sendInvitationOtpEmail(String toEmail, String firstName, String otpCode, int expirationHours) {
        Map<String, Object> model = new HashMap<>();
        model.put("firstName", firstName);
        model.put("otpCode", otpCode);
        model.put("expirationHours", expirationHours);
        sendHtml(toEmail, "Invitación a Intecx InScope", "invitation.ftlh", model);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        Map<String, Object> model = new HashMap<>();
        model.put("firstName", firstName);
        model.put("email", toEmail);
        sendHtml(toEmail, "Bienvenido a Intecx InScope", "welcome.ftlh", model);
    }

    private void sendHtml(String to, String subject, String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfiguration.getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            attachLogo(helper);

            mailSender.send(message);
            log.info("Correo enviado a '{}' con asunto '{}'", to, subject);
        } catch (Exception ex) {
            log.error("No se pudo enviar el correo a '{}' (asunto '{}'): {}", to, subject, ex.getMessage());
        }
    }

    private void attachLogo(MimeMessageHelper helper) throws Exception {
        for (String path : LOGO_CANDIDATES) {
            ClassPathResource logo = new ClassPathResource(path);
            if (logo.exists()) {
                helper.addInline(LOGO_CONTENT_ID, logo);
                return;
            }
        }
        log.warn("No se encontró el logo de Intecx en el classpath; el correo se enviará sin imagen");
    }
}
