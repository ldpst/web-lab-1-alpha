package com.ldpst.web.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ldpst.web.annotations.GetMapping;
import com.ldpst.web.annotations.PostMapping;
import com.ldpst.web.annotations.RestController;
import com.ldpst.web.utils.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class ShootController {
    private final Logger logger = new Logger("logs/logs.txt");
    private final RedisManager redisManager = new RedisManager();

    private final static PSQLManager psql = new PSQLManager();
    private final Cache<String, Map<String, Object>> shootCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();

    private final Cache<String, String> shootsCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();

    public ShootController() {}

    @PostMapping("/api/shoot")
    public String shoot() throws IOException {
        String requestBodyString = RequestManager.readRequestBody();
        long start = System.nanoTime();
        Map<String, BigDecimal> requestBody;
        try {
            requestBody = RequestManager.parseRequestBody(requestBodyString);
        } catch (NumberFormatException | NullPointerException e) {
            return ResultManager.errorResult("Invalid request body. Not decimal");
        } catch (JsonProcessingException e) {
            return ResultManager.errorResult("Invalid JSON body.");
        }
        Map<String, Object> req = shootCache.get(
                "POST:/api/shoot" +
                        "?x=" + requestBody.get("x") +
                        "&y=" + requestBody.get("y") +
                        "&r=" + requestBody.get("r"),
                key -> {
                    return redisManager.getShoot(key, requestBody);
                });

        if (req == null) {
            return ResultManager.errorResult("Invalid request body.");
        }



        String duration = Math.round(((System.nanoTime() - start) / 1e6) * 1e6) / 1e6 + " ms";
        LocalDateTime end = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String date = end.format(formatter);

        req.put("duration", duration);
        req.put("date", date);

        String result = ResultManager.unite(ResultManager.getCreatedHeader(), req);
        shootsCache.invalidate("GET:/api/shoots");
        redisManager.invalidate("GET:/api/shoots");
        psql.addShoot(requestBody, duration, end, (Boolean) req.get("check"));
        return result;
    }

    public static Map<String, Object> buildShootMap(Map<String, BigDecimal> requestBody) {
        if (!RequestManager.validate(requestBody)) {
            return null;
        }
        Map<String, Object> requestMap = new HashMap<>(requestBody);

        boolean check = Checker.check(requestBody);

        requestMap.put("check", check);

//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }

        return requestMap;
    }

    @GetMapping("/api/shoots")
    public String shoots() {
        String js = shootsCache.get("GET:/api/shoots", key -> {
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            return redisManager.getShoots(key);
        });
        return ResultManager.unite(ResultManager.getOkHeader(), js);
    }

    @PostMapping("/api/clear")
    public String clear() {
        shootsCache.invalidate("GET:/api/shoots");
        redisManager.invalidate("GET:/api/shoots");
        psql.clear();
        String js = psql.getShoots();
        return ResultManager.unite(ResultManager.getOkHeader(), js);
    }
}
