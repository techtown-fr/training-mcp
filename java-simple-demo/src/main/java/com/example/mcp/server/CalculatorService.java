package com.example.mcp.server;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public double sum(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }
}

