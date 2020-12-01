package com.gigaspaces.elasticgrid.servicedump;

import java.util.List;

public class JobLogs {
    private final String name;
    private final List<AllocationLog> logs;

    public JobLogs(String name, List<AllocationLog> logs) {
        this.name = name;
        this.logs = logs;
    }

    public String getName() {
        return name;
    }

    public List<AllocationLog> getLogs() {
        return logs;
    }
}

