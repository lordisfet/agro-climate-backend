package com.farm.monitor.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.farm.monitor.dto.TelegramMessageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
    private final RestTemplate restTemplate; 

    @Value("${telegram.bot.base-url}")
    private String baseUrl;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    @Async
    public void sendAlert(String message) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "{token}/sendMessage")
                .buildAndExpand(botToken)
                .toUriString();

        TelegramMessageDTO tMessageDTO = new TelegramMessageDTO(chatId, message, "HTML");

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, tMessageDTO, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Alert successfully sent to Telegram chat: {}", chatId);
            } else {
                logger.warn("Telegram API returned non-2xx status. Response: {}", response.getBody());
            }
        } catch (RestClientException e) {
            logger.error("Failed to connect to Telegram API. Alert not sent.", e);
        }
    }
}
