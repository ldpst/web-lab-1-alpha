package com.ldpst.web;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckerTest {
    private Map<String, BigDecimal> fillBody(int x, double y, int r) {
        Map<String, BigDecimal> body = new HashMap<>();
        body.put("x", new BigDecimal(x));
        body.put("y", new BigDecimal(y));
        body.put("r", new BigDecimal(r));
        return body;
    }

    @Test
    public void test() {
        assertTrue(Checker.check(fillBody(-1, 1, 5)));
        assertFalse(Checker.check(fillBody(-1, -1, 1)));
        assertTrue(Checker.check(fillBody(1, -2, 3)));
        assertTrue(Checker.check(fillBody(1, 2, 2)));
        assertFalse(Checker.check(fillBody(3, 2, 5)));
    }
}