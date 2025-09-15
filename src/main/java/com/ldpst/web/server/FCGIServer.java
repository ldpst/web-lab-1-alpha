package com.ldpst.web.server;

import com.fastcgi.FCGIInterface;
import com.laspringweb.router.*;
import com.ldpst.web.utils.ResultManager;

import java.io.FileWriter;
import java.io.IOException;

public class FCGIServer extends FCGIInterface {
    private final FileWriter writer = new FileWriter("logs.txt", true);
    private final HTTPRouter router = new HTTPRouter("com.ldpst.web");

    public FCGIServer() throws IOException {
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
        var uri = FCGIInterface.request.params.getProperty("REQUEST_URI");
        if (method == null) {
            System.out.println(ResultManager.errorResult("Unsupported HTTP method: null"));
            return;
        }
        int code = router.handleRequest(uri, method);
        if (code == -1) {
            System.out.println(ResultManager.errorResult("Unsupported HTTP URI: " + uri));
        }
    }
}
