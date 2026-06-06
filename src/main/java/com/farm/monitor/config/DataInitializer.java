package com.farm.monitor.config;

import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.User;
import com.farm.monitor.enums.Level;
import com.farm.monitor.enums.Parameter;
import com.farm.monitor.enums.Role;
import com.farm.monitor.repositories.ControlRuleRepository;
import com.farm.monitor.repositories.NodeRepository;
import com.farm.monitor.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ControlRuleRepository controlRuleRepository;
    private final NodeRepository nodeRepository;
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

        if (controlRuleRepository.findAll().isEmpty()) {
            ControlRule temperatureRule = new ControlRule();
            temperatureRule.setNode(nodeRepository.findByDevEUI("24E124785F467119").orElseThrow());
            temperatureRule.setParameter(Parameter.TEMPERATURE);
            temperatureRule.setLevel(Level.CRITICAL);
            temperatureRule.setMinValue(18.);
            temperatureRule.setMaxValue(22.);
            temperatureRule.setActive(true);

            ControlRule humidityRule = new ControlRule();
            humidityRule.setNode(nodeRepository.findByDevEUI("24E124785F467119").orElseThrow());
            humidityRule.setParameter(Parameter.HUMIDITY);
            humidityRule.setLevel(Level.CRITICAL);
            humidityRule.setMinValue(50.);
            humidityRule.setMaxValue(65.);
            humidityRule.setActive(true);
        }
    }
}
