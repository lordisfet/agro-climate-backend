package com.farm.monitor.config;

import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.Measurement;
import com.farm.monitor.entities.Node;
import com.farm.monitor.entities.User;
import com.farm.monitor.enums.Level;
import com.farm.monitor.enums.Location;
import com.farm.monitor.enums.Parameter;
import com.farm.monitor.enums.Role;
import com.farm.monitor.enums.Status;
import com.farm.monitor.repositories.ControlRuleRepository;
import com.farm.monitor.repositories.MeasurementRepository;
import com.farm.monitor.repositories.NodeRepository;
import com.farm.monitor.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Transactional
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ControlRuleRepository controlRuleRepository;
    private final NodeRepository nodeRepository;
    private final MeasurementRepository measurementRepository;
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

        List<Node> nodes = nodeRepository.findAll();

        if (nodes.isEmpty()) {
            logger.warn("В базі даних немає датчиків! Спочатку створіть їх, генерацію пропущено.");
            return;
        }

        Instant now = Instant.now();
        // 1.5 года = примерно 547 дней
        Instant startTime = now.minus(547, ChronoUnit.DAYS);
        Random rand = new Random();

        for (Node node : nodes) {
            // Проверка: чтобы при каждом перезапуске не дублировать по 78к записей,
            // генерируем только если для датчика еще нет измерений.
            // (Если у тебя нет метода findFirstByNode_DevEUI, используй count или просто очисти БД перед запуском)
            List<Measurement> existing = measurementRepository.findByNode_DevEUI(node.getDevEUI());
            if (!existing.isEmpty()) {
                logger.info("Датчик {} вже має дані. Пропускаємо генерацію.", node.getDevEUI());
                continue;
            }

            logger.info("Починаємо генерацію 1.5 року даних (крок 10 хв) для датчика: {}", node.getDevEUI());

            List<Measurement> batch = new ArrayList<>();
            int batchSize = 5000; // Оптимальный размер пачки для базы данных

            for (Instant time = startTime; time.isBefore(now); time = time.plus(10, ChronoUnit.MINUTES)) {
                Measurement m = new Measurement();
                m.setNode(node);
                m.setTimestamp(time);

                // --- МАТЕМАТИЧЕСКАЯ ИМИТАЦИЯ ---
                long millis = time.toEpochMilli();

                // 1. Сезонная волна (Год). Амплитуда 15°C (Зима холоднее, лето жарче)
                double seasonalOffset = Math.sin(millis / 31536000000.0 * 2 * Math.PI) * 15;
                
                // 2. Суточная волна (День/Ночь). Амплитуда 4°C
                double dailyOffset = Math.sin(millis / 86400000.0 * 2 * Math.PI) * 4;

                // Базовая температура 12°C + сезон + день/ночь + белый шум (0.5°C)
                m.setTemperature(12.0 + seasonalOffset + dailyOffset + (rand.nextDouble() - 0.5));
                
                // Влажность: 60% база +/- 10%
                m.setHumidity(60.0 + (rand.nextDouble() * 20 - 10));

                batch.add(m);

                // Сохраняем пачками по 5000, чтобы не убить оперативную память
                if (batch.size() >= batchSize) {
                    measurementRepository.saveAll(batch);
                    batch.clear(); // Очищаем список после сохранения
                }
            }

            // Сохраняем остатки (последнюю пачку, которая меньше 5000)
            if (!batch.isEmpty()) {
                measurementRepository.saveAll(batch);
                batch.clear();
            }

            logger.info("Успішно згенеровано дані для датчика: {}", node.getDevEUI());
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

            controlRuleRepository.save(temperatureRule);
            controlRuleRepository.save(humidityRule);
        }
    }
}
