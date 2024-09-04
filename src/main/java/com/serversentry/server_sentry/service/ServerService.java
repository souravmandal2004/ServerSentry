package com.serversentry.server_sentry.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OSProcess;


@Service
public class ServerService {

    @Autowired
    private SystemInfo systemInfo;

    private final GlobalMemory globalMemory;
    private final CentralProcessor processor;
    private final OperatingSystem os;

    public ServerService (SystemInfo systemInfo) {
        // this.systemInfo = new SystemInfo();
        this.globalMemory = systemInfo.getHardware().getMemory();
        this.processor = systemInfo.getHardware().getProcessor();
        this.os = systemInfo.getOperatingSystem();
    }


    // method to find the total memory
    public long getTotalMemory () {
        return globalMemory.getTotal ();
    }

    // method find available memory
    public long getAvailableMemory () {
        return globalMemory.getAvailable();
    }

    // method to find the percentage of memory
    public double getMemoryUsagePercentage () {
        long usedMemory = getTotalMemory() - getAvailableMemory();
        return (double) usedMemory / getTotalMemory() * 100;
    }

    // method to find the total disk space
    public long getTotalDiskSpace () {
        return systemInfo.getOperatingSystem().getFileSystem().getFileStores().stream()
                .mapToLong(fs -> fs.getTotalSpace()).sum();
    }

    // method to find available disk space
    public long getUsableDiskSpace() {
        // Sum up the usable space across all file stores
        return systemInfo.getOperatingSystem().getFileSystem().getFileStores().stream()
                .mapToLong(fs -> fs.getUsableSpace()).sum(); // This correctly returns usable space
    }

    // method to find the percentage of disk space
    public double getDiskUsage() {
        long totalSpace = getTotalDiskSpace();
        long usableSpace = getUsableDiskSpace();
        long usedSpace = totalSpace - usableSpace;
        double diskUsagePercentage = (double) ((double) usedSpace / (double) totalSpace) * 100;
        return diskUsagePercentage;
    }    

    // method to find all the processes running on the system/server
    public List<String> getRunningProcesses () {
        List<OSProcess> processes = os.getProcesses (
            p -> true,                  // Predicate to include all the processes
            Comparator.comparing(OSProcess::getProcessID),      // Compartor to sort by ProcessId
            0
        );

        return processes.stream().map(OSProcess::getName).collect (Collectors.toList());
    }

    // method to find the total no. of processes running on the system/server
    public long getRunningProcessCount () {
        return os.getProcessCount();
    }

    // method to find the CPU usage percentage
    public long getCpuPercentage () {
        long[] prevTicks = processor.getSystemCpuLoadTicks();

        // Sleep for a short amount of time to get the change in ticks / clock cycle
        try {
            Thread.sleep (1000);
        }
        catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        long[] ticks = processor.getSystemCpuLoadTicks();
        long totalCpu = 0;
        long idleCpu = 0;
        for (int i=0; i<ticks.length; i++) {
            totalCpu += ticks[i] - prevTicks[i];
        }

        idleCpu = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        double percentage = 100.0 * (totalCpu - idleCpu) / totalCpu;
        return (int) percentage;    // return the percentage in integer
    }

    // method to get the cpu usage per process
    public Map<String, Double> getCpuUsagePerProcess () {
        List<OSProcess> processes = os.getProcesses(
            p -> true,  // Predicate to include all processes
            Comparator.comparing(OSProcess::getProcessID), // Comparator to sort by process ID
            0
        );

        long totalCpuTime = processor.getSystemCpuLoadTicks()[CentralProcessor.TickType.IDLE.getIndex()];
         // Merging function to handle duplicate keys
        BinaryOperator<Double> mergeFunction = (oldValue, newValue) -> oldValue + newValue;
    
        Map<String, Double> cpuUsageMap = processes.stream().collect(Collectors.toMap(
                OSProcess::getName,
                p -> (100d * (p.getKernelTime() + p.getUserTime())) / totalCpuTime,
                mergeFunction // Handle duplicate keys by summing their CPU usage
        ));
    
        // Sorting the map by value in descending order
        return cpuUsageMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // In case of a tie, keep the first entry
                        LinkedHashMap::new // Use LinkedHashMap to preserve the order
                ));
    }

    // method to save all the details into the db
    // public void saveServerDetails () {
    //     ServerData serverData = new ServerData ();
    //     serverData.setTotalMemory(getTotalMemory());
    //     serverData.setAvailableMemory(getAvailableMemory());
    //     serverData.setTotalDiskSpace(getTotalDiskSpace());
    //     serverData.setAvailableDiskSpace(getTotalDiskSpace());
    //     serverData.setNumberOfProcesses(getRunningProcessCount());
    //     serverData.setCpuUsage(getCpuPercentage());
    //     serverData.setCpuUsage(getCpuPercentage());
    // }
}