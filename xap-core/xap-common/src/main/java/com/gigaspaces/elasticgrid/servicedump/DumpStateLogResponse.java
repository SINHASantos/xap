package com.gigaspaces.elasticgrid.servicedump;


import java.util.List;

public class DumpStateLogResponse {
    private final List<JobLogs> jobLogs;
    private final String stateDumpResult;


    public DumpStateLogResponse(String stateDumpResult, List<JobLogs> jobLogs) {
        this.jobLogs = jobLogs;
        this.stateDumpResult = stateDumpResult;
    }

    public List<JobLogs> getJobLogs() {
        return jobLogs;
    }

    public String getStateDumpResult() {
        return stateDumpResult;
    }
}
