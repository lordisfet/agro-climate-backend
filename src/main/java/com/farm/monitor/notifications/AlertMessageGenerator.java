package com.farm.monitor.notifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.farm.monitor.dto.AlertDetailDTO;
import com.farm.monitor.entities.ControlRule;
import com.farm.monitor.entities.Measurement;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlertMessageGenerator {
    private final TemplateEngine templateEngine;
    
    @Value("${telegram.bot.alert-template-path}")
    private String templatePath;

    public String messageGenerate(Measurement measurement, List<ControlRule> rules) {    
        List<AlertDetailDTO> details = new ArrayList<>();
        for (ControlRule rule : rules) {
            details.add(new AlertDetailDTO(
                rule.getParameter().name(), 
                measurement.getValueForParameter(rule.getParameter()), 
                rule.getMinValue(), 
                rule.getMaxValue()
            ));
        }

        Context context = new Context();
        context.setVariable("devEUI", measurement.getNode().getDevEUI());
        context.setVariable("details", details);

        return templateEngine.process(templatePath, context);
    }
}
