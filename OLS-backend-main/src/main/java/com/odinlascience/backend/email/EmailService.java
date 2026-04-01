package com.odinlascience.backend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Service generique d'envoi d'emails.
 * Utilise Thymeleaf pour le rendu HTML et JavaMailSender pour l'envoi SMTP.
 * En dev (mail.enabled=false), les emails sont logges sans etre envoyes.
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;

    @Value("${mail.from-address:noreply@odinlascience.com}")
    private String fromAddress;

    @Value("${mail.from-name:Odin La Science}")
    private String fromName;

    @Value("${mail.enabled:false}")
    private boolean enabled;

    public EmailService(JavaMailSender mailSender,
                        @Qualifier("emailTemplateEngine") TemplateEngine emailTemplateEngine) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    /**
     * Envoie un email HTML a partir d'un template Thymeleaf.
     *
     * @param to           adresse du destinataire
     * @param subject      sujet de l'email
     * @param templateName nom du template (sans extension) dans templates/email/
     * @param variables    variables injectees dans le template
     */
    @Async("emailExecutor")
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = emailTemplateEngine.process(templateName, context);

        if (!enabled) {
            log.info("[EMAIL DESACTIVE] to={}, subject='{}', template={}", to, subject, templateName);
            log.debug("[EMAIL CONTENU] {}", htmlContent);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email envoye a {} (template={})", to, templateName);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Echec envoi email a {} (template={}): {}", to, templateName, e.getMessage());
        }
    }

    /**
     * Envoie un email en texte brut.
     */
    @Async("emailExecutor")
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!enabled) {
            log.info("[EMAIL DESACTIVE] to={}, subject='{}', text='{}'", to, subject, text);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(message);
            log.info("Email (texte) envoye a {}", to);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Echec envoi email texte a {}: {}", to, e.getMessage());
        }
    }
}
