package com.ldpst.web.utils;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Setter
@Getter
public class RoutManager {
    private Object controller;
    private Method method;

    public RoutManager(Method method, Object controller) {
        this.method = method;
        this.controller = controller;
    }
}
