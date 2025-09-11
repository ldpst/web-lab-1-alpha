package com.ldpst.web.utils;

import com.fastcgi.FCGIInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();

        var contentLength = FCGIInterface.request.inStream.available();
        ByteBuffer buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);

        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();

        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }

    public static Map<String, BigDecimal> parseRequestBody(String requestBody) throws NumberFormatException, NullPointerException, JsonProcessingException {
        return mapper.readValue(requestBody, mapper.getTypeFactory()
                .constructMapType(Map.class, String.class, BigDecimal.class));
    }

    public static boolean validate(Map<String, BigDecimal> requestBody) {
        BigDecimal x = requestBody.get("x");
        BigDecimal y = requestBody.get("y");
        BigDecimal r = requestBody.get("r");

        try {
            int value = x.intValueExact();
        } catch (ArithmeticException e) {
            return false;
        }

        if (y.compareTo(new BigDecimal("-3")) < 0 || y.compareTo(new BigDecimal("3")) > 0) {
            return false;
        }

        try {
            int value = r.intValueExact();
            if (value < 1 || value > 5) {
                return false;
            }
        } catch (ArithmeticException e) {
            return false;
        }
        return true;
    }
}
