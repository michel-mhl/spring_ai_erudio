package br.com.erudio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/}") // Pega a pasta de upload (padrão 'uploads/')
    private String uploadDir;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }



            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String path = "file:" + uploadDir;
                if (!path.endsWith("/")) {
                    path += "/";
                }

                // AGORA RESPONDE POR: http://localhost:8080/images/nome-da-imagem.png
                registry.addResourceHandler("/images/**")
                        .addResourceLocations(path);
            }
        };
    }
}
