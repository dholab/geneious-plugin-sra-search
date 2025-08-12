package com.biomatters.plugins.ncbisra.model;

import java.util.List;

/**
 * Represents search results from NCBI SRA with pagination info
 */
public class SraSearchResult {
    private List<SraRecord> records;
    private int totalCount;
    private int retStart;
    private int retMax;
    private String queryKey;
    private String webEnv;
    
    public SraSearchResult() {
    }
    
    public SraSearchResult(List<SraRecord> records, int totalCount, int retStart, int retMax) {
        this.records = records;
        this.totalCount = totalCount;
        this.retStart = retStart;
        this.retMax = retMax;
    }
    
    public List<SraRecord> getRecords() {
        return records;
    }
    
    public void setRecords(List<SraRecord> records) {
        this.records = records;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getRetStart() {
        return retStart;
    }
    
    public void setRetStart(int retStart) {
        this.retStart = retStart;
    }
    
    public int getRetMax() {
        return retMax;
    }
    
    public void setRetMax(int retMax) {
        this.retMax = retMax;
    }
    
    public String getQueryKey() {
        return queryKey;
    }
    
    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }
    
    public String getWebEnv() {
        return webEnv;
    }
    
    public void setWebEnv(String webEnv) {
        this.webEnv = webEnv;
    }
    
    public boolean hasMoreResults() {
        return retStart + records.size() < totalCount;
    }
    
    public int getNextStartIndex() {
        return retStart + records.size();
    }
    
    @Override
    public String toString() {
        return "SraSearchResult{" +
                "recordCount=" + (records != null ? records.size() : 0) +
                ", totalCount=" + totalCount +
                ", retStart=" + retStart +
                ", retMax=" + retMax +
                '}';
    }
}