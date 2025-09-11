package com.ldpst.web;

import com.ldpst.web.server.FCGIServer;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        FCGIServer server = new FCGIServer();
        server.start();
    }
}