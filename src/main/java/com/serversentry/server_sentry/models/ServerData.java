package com.serversentry.server_sentry.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table (name = "server_details")
public class ServerData {
    @Id
    private Long id;

    @Column (name = "total_memory")
    private Long totalMemory;

    @Column (name = "available_memory")
    private Long availableMemory;

    @Column (name = "total_disk_space")
    private Long totalDiskSpace;

    @Column (name = "available_disk_space")
    private Long availableDiskSpace;

    @Column(name = "number_of_processes")
    private Long numberOfProcesses;

    @Column (name = "cpu_usage")
    private Long cpuUsage;
}