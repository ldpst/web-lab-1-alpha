package com.ldpst.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ResultManager {
    public static String errorResult(String error) {
        return """
                Status: 400 Bad Request
                Content-Type: text/html
                Content-Length: %d
                
                
                %s
                """.formatted(error.getBytes(StandardCharsets.UTF_8).length, error);
    }

    public static String successResult(Map<String, BigDecimal> requestBody, String duration, LocalDateTime date, boolean check) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>(requestBody);
        responseMap.put("duration", duration);
        responseMap.put("date", date.format(formatter));
        responseMap.put("check", check);
        String js;
        try {
            js = mapper.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            return errorResult(e.getMessage());
        }

        String headers =
                """
                        Status: 201 Created
                        Content-Type: application/json; charset=utf-8
                        Content-Length: %d
                        
                        
                        %s
                        
                        """;

        return headers.formatted(js.getBytes(StandardCharsets.UTF_8).length + 1, js);
    }
}
