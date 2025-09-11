package com.ldpst.web.server;

import com.fastcgi.FCGIInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ldpst.web.Checker;
import com.ldpst.web.utils.RequestManager;
import com.ldpst.web.utils.ResultManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;

public class FCGIServer extends FCGIInterface {
    private static Logger logger = Logger.getLogger(FCGIServer.class.getName());

    public FCGIServer() {
        super();
    }

    public void start() {
        while (this.FCGIaccept() >= 0) {
            try {
                handleRequest();
            } catch (IOException e) {
                System.out.println(ResultManager.errorResult("Unsupported error"));
            }
        }
    }

    public void handleRequest() throws IOException {


        var method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        if (method == null) {
            System.out.println(ResultManager.errorResult("Unsupported HTTP method: null"));
            return;
        }
        if (method.equals("GET")) {
            return;
        }
        if (method.equals("POST")) {
            long start = System.nanoTime();
            String requestBodyString = RequestManager.readRequestBody();
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

            String duration = (System.nanoTime() - start / 1e6) + " ms";
            LocalDateTime end = LocalDateTime.now();

            String result = ResultManager.successResult(requestBody, duration, end, check);
            System.out.println(result);
        }
    }


}
