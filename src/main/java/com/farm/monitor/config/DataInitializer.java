package com.farm.monitor.config;

import com.farm.monitor.entities.User;
import com.farm.monitor.enums.Role;
import com.farm.monitor.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final static Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("lordisfet").isEmpty()) {
            User admin = new User();
            admin.setPasswordHash(passwordEncoder.encode("1212")); 
            admin.setRole(Role.ADMIN);
            admin.setName("Max");
            admin.setSecondName("Savchenko");
            admin.setUsername("lordisfet");
            admin.setTelegramChatId("Lord_Isfet");
            admin.setPhone("+380991234567");
            admin.setActive(true);
            admin.setCreatedAt(Instant.now());

            userRepository.save(admin);
            logger.info("Admin user created: username='lordisfet'");
        }

        if (userRepository.findByUsername("analyst").isEmpty()) {
            User analyst = new User();
            analyst.setPasswordHash(passwordEncoder.encode("1212")); 
            analyst.setRole(Role.ANALIST);
            analyst.setName("Analyst");
            analyst.setSecondName("Smith");
            analyst.setUsername("analyst");
            analyst.setTelegramChatId("Analyst_Smith");
            analyst.setPhone("+380997654321");
            analyst.setActive(true);
            analyst.setCreatedAt(Instant.now());

            userRepository.save(analyst);
            logger.info("Analyst user created: username='analyst'");
        }
    }
}
