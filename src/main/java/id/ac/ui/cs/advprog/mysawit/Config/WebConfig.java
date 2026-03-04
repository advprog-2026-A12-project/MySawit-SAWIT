package id.ac.ui.cs.advprog.mysawit.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // memetakan URL /uploads/** ke folder lokal "uploads/"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}