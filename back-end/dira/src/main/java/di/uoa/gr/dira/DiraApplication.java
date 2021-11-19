package di.uoa.gr.dira;

import di.uoa.gr.dira.configuration.modelMapper.ModelMapperConfigurator;
import di.uoa.gr.dira.configuration.spring.SpringProfiles;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DiraApplication {
    @Autowired
    @Bean
    public ModelMapper modelMapperSingleton(ModelMapperConfigurator configurator) {
        ModelMapper mapper = new ModelMapper();
        configurator.configure(mapper);
        return mapper;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000", "https://localhost:3000").allowedMethods("*");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(DiraApplication.class, args);
    }
}
