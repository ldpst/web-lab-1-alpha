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

    public static String unite(String header, Map<String, Object> responseMap) {
        ObjectMapper mapper = new ObjectMapper();
        String js;
        try {
            js = mapper.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            return errorResult(e.getMessage());
        }
        return unite(header, js);
    }

    public static String unite(String header, String js) {
        return header.formatted(js.getBytes(StandardCharsets.UTF_8).length + 1, js);
    }

    public static String getOkHeader() {
        return  """
                Status: 200 OK
                Content-Type: application/json; charset=utf-8
                Content-Length: %d
                
                
                %s
                
                """;
    }

    public static String getCreatedHeader() {
        return """
                        Status: 201 Created
                        Content-Type: application/json; charset=utf-8
                        Content-Length: %d
                        
                        
                        %s
                        
                        """;
    }
}
