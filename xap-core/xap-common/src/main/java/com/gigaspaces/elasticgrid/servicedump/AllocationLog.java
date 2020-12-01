package com.gigaspaces.elasticgrid.servicedump;

public class AllocationLog {
    private final String allocId;
    private final String stdout;
    private final String stderr;

    public AllocationLog(String allocId, String stdout, String stderr) {
        this.allocId = allocId;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public String getAllocId() {
        return allocId;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}
