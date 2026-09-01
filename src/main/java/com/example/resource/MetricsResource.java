package com.example.resource;

import com.example.dtos.ServerMetrics;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

@Path("/metrics")
public class MetricsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ServerMetrics getServerMetrics() {

        OperatingSystemMXBean os =
                ManagementFactory.getOperatingSystemMXBean();

        MemoryMXBean memory =
                ManagementFactory.getMemoryMXBean();

        RuntimeMXBean runtime =
                ManagementFactory.getRuntimeMXBean();

        return new ServerMetrics(
                getCpuUsage(os),
                getUsedMemory(),
                getMaxMemory(),
                memory.getHeapMemoryUsage().getUsed(),
                memory.getHeapMemoryUsage().getMax(),
                runtime.getUptime()
        );
    }

    private double getCpuUsage(OperatingSystemMXBean os) {
        if (os instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            return sunOs.getCpuLoad() * 100;
        }

        return -1;
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();

        return runtime.totalMemory() - runtime.freeMemory();
    }

    private long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
}