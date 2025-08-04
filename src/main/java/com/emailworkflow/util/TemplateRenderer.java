package com.emailworkflow.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.net.URL;

@Component
public class TemplateRenderer {

    private final TemplateEngine stringTemplateEngine = new TemplateEngine();

    @Value("${template.path.prefix}")
    private String prefix;

    @Value("${template.path.suffix}")
    private String suffix;

    @PostConstruct
    public void init() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix(prefix);
        resolver.setSuffix(suffix);
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true); // false for dev, true for prod

        stringTemplateEngine.setTemplateResolver(resolver);
    }

    public String renderFromFile(String templateName, Context context) {
        return stringTemplateEngine.process(templateName, context);
    }

}
