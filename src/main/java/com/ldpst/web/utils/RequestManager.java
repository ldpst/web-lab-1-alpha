package com.ldpst.web.utils;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {
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

    public Map<String, BigDecimal> parseRequestBody(String requestBody) throws NumberFormatException, NullPointerException {
        Map<String, BigDecimal> requestBodyMap = new HashMap<>();
        Arrays.stream(requestBody.split("&"))
                .forEach(kv -> requestBodyMap.put(kv.split("=")[0], new BigDecimal(kv.split("=")[1])));
        return requestBodyMap;
    }
}
