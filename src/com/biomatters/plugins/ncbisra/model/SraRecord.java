package com.biomatters.plugins.ncbisra.model;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents an SRA record with metadata
 */
public class SraRecord {
    private String accession;
    private String title;
    private String organism;
    private String study;
    private String sample;
    private String experiment;
    private String run;
    private String platform;
    private String libraryStrategy;
    private String librarySource;
    private String librarySelection;
    private String libraryLayout;
    private Date submissionDate;
    private Date publicationDate;
    private long totalSpots;
    private long totalBases;
    private String centerName;
    private String bioProject;
    private String bioSample;
    private Map<String, String> attributes;
    
    public SraRecord() {
        this.attributes = new HashMap<>();
    }
    
    public SraRecord(String accession) {
        this();
        this.accession = accession;
    }
    
    // Getters and setters
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
        this.accession = accession;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getOrganism() {
        return organism;
    }
    
    public void setOrganism(String organism) {
        this.organism = organism;
    }
    
    public String getStudy() {
        return study;
    }
    
    public void setStudy(String study) {
        this.study = study;
    }
    
    public String getSample() {
        return sample;
    }
    
    public void setSample(String sample) {
        this.sample = sample;
    }
    
    public String getExperiment() {
        return experiment;
    }
    
    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }
    
    public String getRun() {
        return run;
    }
    
    public void setRun(String run) {
        this.run = run;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getLibraryStrategy() {
        return libraryStrategy;
    }
    
    public void setLibraryStrategy(String libraryStrategy) {
        this.libraryStrategy = libraryStrategy;
    }
    
    public String getLibrarySource() {
        return librarySource;
    }
    
    public void setLibrarySource(String librarySource) {
        this.librarySource = librarySource;
    }
    
    public String getLibrarySelection() {
        return librarySelection;
    }
    
    public void setLibrarySelection(String librarySelection) {
        this.librarySelection = librarySelection;
    }
    
    public String getLibraryLayout() {
        return libraryLayout;
    }
    
    public void setLibraryLayout(String libraryLayout) {
        this.libraryLayout = libraryLayout;
    }
    
    public Date getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public Date getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
    
    public long getTotalSpots() {
        return totalSpots;
    }
    
    public void setTotalSpots(long totalSpots) {
        this.totalSpots = totalSpots;
    }
    
    public long getTotalBases() {
        return totalBases;
    }
    
    public void setTotalBases(long totalBases) {
        this.totalBases = totalBases;
    }
    
    public String getCenterName() {
        return centerName;
    }
    
    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }
    
    public String getBioProject() {
        return bioProject;
    }
    
    public void setBioProject(String bioProject) {
        this.bioProject = bioProject;
    }
    
    public String getBioSample() {
        return bioSample;
    }
    
    public void setBioSample(String bioSample) {
        this.bioSample = bioSample;
    }
    
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
    
    public String getAttribute(String key) {
        return this.attributes.get(key);
    }
    
    /**
     * Check if this record represents paired-end data
     */
    public boolean isPairedEnd() {
        return "PAIRED".equalsIgnoreCase(libraryLayout);
    }
    
    @Override
    public String toString() {
        return "SraRecord{" +
                "accession='" + accession + '\'' +
                ", title='" + title + '\'' +
                ", organism='" + organism + '\'' +
                ", platform='" + platform + '\'' +
                ", libraryStrategy='" + libraryStrategy + '\'' +
                ", libraryLayout='" + libraryLayout + '\'' +
                '}';
    }
}