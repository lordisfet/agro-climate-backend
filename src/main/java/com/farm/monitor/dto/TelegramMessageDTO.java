package com.farm.monitor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramMessageDTO(
    @JsonProperty("chat_id") String chatId,
    @JsonProperty("text") String text,
    @JsonProperty("parse_mode") String parseMode
) {}
