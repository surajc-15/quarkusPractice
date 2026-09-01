package com.example.dtos;

public record ServerMetrics(
        double cpuUsage,
        long memoryUsed,
        long memoryMax,
        long heapUsed,
        long heapMax,
        long uptime
) {}