package com.ldpst.web.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.laspringweb.annotation.GetMapping;
import com.laspringweb.annotation.PostMapping;
import com.laspringweb.annotation.RestController;
import com.ldpst.web.utils.Checker;
import com.ldpst.web.utils.PSQLManager;
import com.ldpst.web.utils.RequestManager;
import com.ldpst.web.utils.ResultManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class ShootController {
    private final static PSQLManager psql = new PSQLManager();

    @PostMapping("/api/shoot")
    public void shoot() throws IOException {
        String requestBodyString = RequestManager.readRequestBody();
        long start = System.nanoTime();
        Map<String, BigDecimal> requestBody;
        try {
            requestBody = RequestManager.parseRequestBody(requestBodyString);
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(ResultManager.errorResult("Invalid request body. Not decimal"));
            return;
        } catch (JsonProcessingException e) {
            System.out.println(ResultManager.errorResult("Invalid JSON body."));
            return;
        }

        if (!RequestManager.validate(requestBody)) {
            System.out.println(ResultManager.errorResult("Invalid request body"));
            return;
        }

        boolean check = Checker.check(requestBody);

        String duration = Math.round(((System.nanoTime() - start) / 1e6) * 1e6) / 1e6 + " ms";
        LocalDateTime end = LocalDateTime.now();

        String result = ResultManager.createdResult(requestBody, duration, end, check);
        psql.addShoot(requestBody, duration, end, check);
        System.out.println(result);
    }

    @GetMapping("/api/shoots")
    public void shoots() {
        String js = psql.getShoots();
        System.out.println(ResultManager.okResult(js));
    }

    @PostMapping("/api/clear")
    public void clear() {
        psql.clear();
        String js = psql.getShoots();
        System.out.println(ResultManager.okResult(js));
    }
}
