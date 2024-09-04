package com.serversentry.server_sentry.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.serversentry.server_sentry.service.ServerService;

@RestController
@RequestMapping ("api/v1/details")
@CrossOrigin
public class ServerController {
    @Autowired
    private ServerService serverService;


    // get all memory details
    @GetMapping ("/memory")
    public Map<String, Object> getMemoryMetrics() {
        Map<String, Object> memoryMetrics = new HashMap<>();
        memoryMetrics.put("Total Memory", serverService.getTotalMemory());
        memoryMetrics.put("Available Memory", serverService.getAvailableMemory());
        memoryMetrics.put("memoryUsagePercentage", serverService.getMemoryUsagePercentage());
        return memoryMetrics;
    }


    // get all disk details
    @GetMapping("/disk")
    public Map<String, Object> getDiskMetrics() {
        Map<String, Object> diskMetrics = new HashMap<>();
        diskMetrics.put("Total Disk Space", serverService.getTotalDiskSpace());
        diskMetrics.put("Available Disk Space", serverService.getUsableDiskSpace());
        diskMetrics.put("diskUsagePercentage", serverService.getDiskUsage());
        return diskMetrics;
    }


    // get process count
    @GetMapping("/processes")
    public Map<String, Object> getRunningProcesses() {
        Map<String, Object> processMetrics = new HashMap<>();
        processMetrics.put("Running Process Count", serverService.getRunningProcessCount());
        return processMetrics;
    }


    // get all the running process names
    @GetMapping("/process-names")
    public List<String> getProcessNames () {
        return serverService.getRunningProcesses ();
    } 

    // get cpu percentage
    @GetMapping ("/cpu/percentage")
    public long getCpuUsage () {
        return serverService.getCpuPercentage();
    }

    // get cpu usage per process
    @GetMapping("/cpu/usage/per-process")
    public Map<String, Double> getCpuUsagePerProcess() {
        return serverService.getCpuUsagePerProcess();
    }
}