package com.emailworkflow.service;

import com.emailworkflow.DTO.EmailRequest;
import com.emailworkflow.config.TemplateMappingConfig;
import com.emailworkflow.util.TemplateRenderer;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final String BASE_TEMPLATE = "emailTemplate";

    private final JavaMailSender mailSender;
    private final TemplateEngine fileTemplateEngine;
    private final TemplateMappingConfig templateMappingConfig;
    private final TemplateRenderer templateRenderer;

    public EmailService(JavaMailSender mailSender, TemplateEngine fileTemplateEngine,
                        TemplateMappingConfig templateMappingConfig, TemplateRenderer templateRenderer) {
        this.mailSender = mailSender;
        this.fileTemplateEngine = fileTemplateEngine;
        this.templateMappingConfig = templateMappingConfig;
        this.templateRenderer = templateRenderer;
    }

    public void sendEmail(EmailRequest request) {
        try {
            String finalHtml = generateEmailContent(request);
            sendMimeEmail(request, finalHtml);
            log.info("Email successfully sent to {} for requestType {}", request.getTo(), request.getRequestType());
        } catch (Exception e) {
            log.error("Failed to send email to {} for requestType {}. Error: {}",
                    request.getTo(), request.getRequestType(), e.getMessage(), e);
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }

    private String generateEmailContent(EmailRequest request) {
        Context context = new Context();
        context.setVariables(request.getData());

        String templateName = templateMappingConfig.getTemplateName(request.getRequestType());

        String bodyContent = templateRenderer.renderFromFile(templateName, context);
        context.setVariable("bodyContent", bodyContent);

        return fileTemplateEngine.process(BASE_TEMPLATE, context);
    }

    private void sendMimeEmail(EmailRequest request, String finalHtml) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("meghanarathod456@gmail.com");
        helper.setTo(request.getTo());
        if (request.getCc() != null) {
            helper.setCc(request.getCc());
        }
        helper.setSubject(request.getSubject());
        helper.setText(finalHtml, true);

        mailSender.send(message);
    }
}
