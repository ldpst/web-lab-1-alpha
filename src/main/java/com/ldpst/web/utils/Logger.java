package com.ldpst.web.utils;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private FileWriter fileWriter;

    public Logger(String path) {
        try {
            fileWriter = new FileWriter(path);
        } catch (IOException e) {
            //
        }
    }

    public void write(String text) {
        if (fileWriter == null) return;
        try {
            fileWriter.write(text);
            fileWriter.flush();
        } catch (IOException e) {
            //
        }
    }
}
