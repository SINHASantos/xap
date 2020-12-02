package com.gigaspaces.elasticgrid.servicedump;

import java.util.Date;

public class AllocationLog {
    private final String allocId;
    private final String stdout;
    private final String stderr;
    private final Date timestamp;

    public AllocationLog(String allocId, String stdout, String stderr, Date timestamp) {
        this.allocId = allocId;
        this.stdout = stdout;
        this.stderr = stderr;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
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
