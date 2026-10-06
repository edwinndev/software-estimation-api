package com.intecx.inscope.service.impl;

import com.intecx.inscope.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        String subject = "Código de verificación para restablecer contraseña";
        String content = """
                Hola,

                Has solicitado restablecer tu contraseña en la plataforma Software Estimation.

                Tu código de verificación OTP es: %s

                Este código es válido durante 10 minutos. Si no solicitaste este cambio, puedes ignorar este correo.

                Saludos,
                El equipo de Software (InScope)
                """.formatted(otpCode);

        sendSimpleMessage(toEmail, subject, content);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        String subject = "Bienvenido a Software Estimation";
        String content = """
                Hola %s,

                ¡Bienvenido a la plataforma Software Estimation!

                Tu cuenta ha sido registrada exitosamente en nuestro sistema. Ya puedes iniciar sesión con tus credenciales.

                Saludos,
                El equipo de Software (InScope)
                """.formatted(firstName);

        sendSimpleMessage(toEmail, subject, content);
    }

    @Override
    public void sendInvitationOtpEmail(String toEmail, String firstName, String otpCode) {
        String subject = "Invitación y código de activación - Software Estimation";
        String content = """
                Hola %s,

                Has sido registrado en la plataforma Software Estimation.

                Tu código de activación OTP para configurar tu contraseña es: %s

                Este código es válido durante 24 horas. Para activar tu cuenta y configurar tu contraseña por primera vez, ingresa al sistema y utiliza la opción de verificación con tu correo y este código.

                Saludos,
                El equipo de Software (InScope)
                """.formatted(firstName, otpCode);

        sendSimpleMessage(toEmail, subject, content);
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Correo enviado exitosamente a '{}' con el asunto: '{}'", to, subject);
        } catch (Exception ex) {
            log.error("No se pudo enviar el correo a '{}' (Asunto: '{}'). Causa: {}", to, subject, ex.getMessage());
        }
    }
}
