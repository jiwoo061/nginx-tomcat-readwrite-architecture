package dev.sample.auth;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public AuthUserDao authUserDao(DataSource sourceDS) {
        return new AuthUserDao(sourceDS);
    }

    @Bean
    public AuthService authService(AuthUserDao dao) {
        return new AuthService(dao);
    }
}