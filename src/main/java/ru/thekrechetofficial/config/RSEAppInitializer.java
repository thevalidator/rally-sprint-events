/*
 * The Krechet Software
 */
package ru.thekrechetofficial.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Configuration
public class RSEAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) //throws ServletException 
    {
        FilterRegistration.Dynamic encodingFilter = container
                .addFilter("encoding-filter", new CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");

        AnnotationConfigWebApplicationContext context
                = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);
        context.setServletContext(container);

        ServletRegistration.Dynamic dispatcher = container
                .addServlet("SpringDispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}
