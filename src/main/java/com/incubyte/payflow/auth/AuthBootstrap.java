package com.incubyte.payflow.auth;

import com.incubyte.payflow.domain.AppUser;
import com.incubyte.payflow.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthBootstrap {

    @Bean
    CommandLineRunner seedDefaultUser(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (appUserRepository.findByUsername("system").isEmpty()) {
                appUserRepository.save(new AppUser(
                    "system",
                    passwordEncoder.encode("system"),
                    "system@payflow.local",
                    "Local",
                    "Admin",
                    AppUser.Role.HR_MANAGER
                ));
            }
        };
    }
}
