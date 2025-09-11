package com.ldpst.web.server;

import com.fastcgi.FCGIInterface;
import com.ldpst.web.utils.RequestManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FCGIServer extends FCGIInterface {
    public FCGIServer() {super();}

    public void start() {
        while (this.FCGIaccept() >= 0) {
            try {
                handleRequest();
            } catch (IOException e) {
                System.out.println(errorResult("Unsupported error"));
            }
        }
    }

    public void handleRequest() throws IOException {
        var method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        if (method == null) {
            System.out.println(errorResult("Unsupported HTTP method: null"));
            return;
        }
        if (method.equals("GET")) {
//            var contentType = FCGIInterface.request.params.getProperty("CONTENT_TYPE");
//            if (contentType == null) {
//                System.out.println(errorResult("Content-Type is null"));
//                return;
//            }
//
//            if (!contentType.equals("application/x-www-form-urlencoded")) {
//                System.out.println(errorResult("Content-Type is not supported"));
//                return;
//            }
        }
        if (method.equals("POST")) {
            String requestBody = RequestManager.readRequestBody();

        }
    }


    private static String errorResult(String error) {
        return """
                HTTP/1.1 400 Bad Request
                Content-Type: text/html
                Content-Length: %d


                %s
                """.formatted(error.getBytes(StandardCharsets.UTF_8).length, error);
    }
}
