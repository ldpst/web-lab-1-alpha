package com.ldpst.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class Checker {
    public static boolean check(Map<String, BigDecimal> requestBody) {
        BigDecimal x = requestBody.get("x");
        BigDecimal y = requestBody.get("y");
        BigDecimal r = requestBody.get("r");

        BigDecimal halfR = r.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);

        BigDecimal sumSquares = x.multiply(x).add(y.multiply(y));

        boolean inTriangle =
                (x.compareTo(BigDecimal.ZERO) <= 0) &&
                        (x.compareTo(halfR.negate()) >= 0) &&
                        (y.compareTo(BigDecimal.ZERO) >= 0) &&
                        (y.compareTo(x.add(halfR)) <= 0);

        boolean inRectangle =
                (x.compareTo(BigDecimal.ZERO) >= 0) &&
                        (x.compareTo(halfR) <= 0) &&
                        (y.compareTo(BigDecimal.ZERO) >= 0) &&
                        (y.compareTo(r) <= 0);

        boolean inCircle =
                (x.compareTo(BigDecimal.ZERO) >= 0) &&
                        (y.compareTo(BigDecimal.ZERO) <= 0) &&
                        (sumSquares.compareTo(r.multiply(r)) <= 0);

        return inTriangle || inRectangle || inCircle;
    }
}
