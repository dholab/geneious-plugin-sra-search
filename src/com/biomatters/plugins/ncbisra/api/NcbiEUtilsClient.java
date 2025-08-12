package com.biomatters.plugins.ncbisra.api;

import com.biomatters.plugins.ncbisra.model.SraRecord;
import com.biomatters.plugins.ncbisra.model.SraSearchResult;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Client for NCBI E-utilities API to search and retrieve SRA metadata
 */
public class NcbiEUtilsClient {
    
    private static final String EUTILS_BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
    private static final String ESEARCH_URL = EUTILS_BASE_URL + "esearch.fcgi";
    private static final String EFETCH_URL = EUTILS_BASE_URL + "efetch.fcgi";
    private static final String ESUMMARY_URL = EUTILS_BASE_URL + "esummary.fcgi";
    
    private static final String DATABASE = "sra";
    private static final int DEFAULT_RETMAX = 20;
    private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
    
    private final String userAgent;
    private final SimpleDateFormat dateFormat;
    
    public NcbiEUtilsClient() {
        this.userAgent = "GeneiousNcbiSraPlugin/1.0 (geneious@biomatters.com)";
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    }
    
    /**
     * Search SRA database with query term
     */
    public SraSearchResult search(String queryTerm, int retStart, int retMax) throws IOException {
        if (queryTerm == null || queryTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Query term cannot be empty");
        }
        
        if (retMax <= 0) {
            retMax = DEFAULT_RETMAX;
        }
        
        // Optimize query term for better NCBI SRA search
        String optimizedQuery = optimizeSearchQuery(queryTerm);
        
        // First, perform esearch to get UIDs
        String searchUrl = buildSearchUrl(optimizedQuery, retStart, retMax);
        Document searchDoc = fetchXmlDocument(searchUrl);
        
        SraSearchResult result = parseSearchResult(searchDoc);
        
        // Extract UIDs from search results
        List<String> uids = extractUids(searchDoc);
        
        if (!uids.isEmpty()) {
            // Fetch detailed information for each UID
            List<SraRecord> detailedRecords = fetchDetailedRecords(uids);
            result.setRecords(detailedRecords);
        } else {
            result.setRecords(new ArrayList<>());
        }
        
        return result;
    }
    
    /**
     * Search by specific accession number
     */
    public SraRecord searchByAccession(String accession) throws IOException {
        if (accession == null || accession.trim().isEmpty()) {
            throw new IllegalArgumentException("Accession cannot be empty");
        }
        
        String queryTerm = accession.trim() + "[Accession]";
        SraSearchResult result = search(queryTerm, 0, 1);
        
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            return result.getRecords().get(0);
        }
        
        return null;
    }
    
    /**
     * Search by BioProject ID
     */
    public SraSearchResult searchByBioProject(String bioProject, int retStart, int retMax) throws IOException {
        if (bioProject == null || bioProject.trim().isEmpty()) {
            throw new IllegalArgumentException("BioProject ID cannot be empty");
        }
        
        String queryTerm = bioProject.trim() + "[Bioproject]";
        return search(queryTerm, retStart, retMax);
    }
    
    /**
     * Search by BioSample ID
     */
    public SraSearchResult searchByBioSample(String bioSample, int retStart, int retMax) throws IOException {
        if (bioSample == null || bioSample.trim().isEmpty()) {
            throw new IllegalArgumentException("BioSample ID cannot be empty");
        }
        
        String queryTerm = bioSample.trim() + "[Biosample]";
        return search(queryTerm, retStart, retMax);
    }
    
    /**
     * Search by organism name
     */
    public SraSearchResult searchByOrganism(String organism, int retStart, int retMax) throws IOException {
        if (organism == null || organism.trim().isEmpty()) {
            throw new IllegalArgumentException("Organism name cannot be empty");
        }
        
        String queryTerm = organism.trim() + "[Organism]";
        return search(queryTerm, retStart, retMax);
    }
    
    /**
     * Search by library strategy (e.g., RNA-Seq, WGS, ChIP-Seq)
     */
    public SraSearchResult searchByLibraryStrategy(String strategy, int retStart, int retMax) throws IOException {
        if (strategy == null || strategy.trim().isEmpty()) {
            throw new IllegalArgumentException("Library strategy cannot be empty");
        }
        
        String queryTerm = strategy.trim() + "[Strategy]";
        return search(queryTerm, retStart, retMax);
    }
    
    /**
     * Optimize search query for better NCBI SRA results
     */
    private String optimizeSearchQuery(String queryTerm) {
        if (queryTerm == null || queryTerm.trim().isEmpty()) {
            return queryTerm;
        }
        
        String optimized = queryTerm.trim();
        
        // If query looks like an accession but doesn't have a field tag, add it
        if (isLikelyAccession(optimized) && !optimized.contains("[")) {
            optimized = optimized + "[Accession]";
        }
        // If query looks like a BioProject but doesn't have a field tag, add it
        else if (isLikelyBioProject(optimized) && !optimized.contains("[")) {
            optimized = optimized + "[Bioproject]";
        }
        // If query looks like a BioSample but doesn't have a field tag, add it
        else if (isLikelyBioSample(optimized) && !optimized.contains("[")) {
            optimized = optimized + "[Biosample]";
        }
        
        return optimized;
    }
    
    /**
     * Check if a string looks like an SRA/ENA/DRA accession
     */
    private boolean isLikelyAccession(String term) {
        if (term == null || term.length() < 6) {
            return false;
        }
        
        // Common SRA/ENA/DRA accession patterns
        return term.matches("^(SRR|ERR|DRR|SRX|ERX|DRX|SRS|ERS|DRS|SRP|ERP|DRP|SRA|ERA|DRA)\\d+$");
    }
    
    /**
     * Check if a string looks like a BioProject ID
     */
    private boolean isLikelyBioProject(String term) {
        if (term == null) {
            return false;
        }
        
        return term.matches("^PRJ[NED][A-Z]\\d+$");
    }
    
    /**
     * Check if a string looks like a BioSample ID
     */
    private boolean isLikelyBioSample(String term) {
        if (term == null) {
            return false;
        }
        
        return term.matches("^SAM[NED][A-Z]?\\d+$");
    }
    
    private String buildSearchUrl(String queryTerm, int retStart, int retMax) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder(ESEARCH_URL);
        url.append("?db=").append(DATABASE);
        url.append("&term=").append(URLEncoder.encode(queryTerm, "UTF-8"));
        url.append("&retstart=").append(retStart);
        url.append("&retmax=").append(retMax);
        url.append("&usehistory=y");
        url.append("&retmode=xml");
        
        return url.toString();
    }
    
    private Document fetchXmlDocument(String urlString) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            throw new IOException("Operation was interrupted");
        }
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", userAgent);
        
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP error " + responseCode + " when accessing " + urlString);
            }
            
            InputStream inputStream = connection.getInputStream();
            SAXBuilder builder = new SAXBuilder();
            return builder.build(inputStream);
            
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException("Error parsing XML response: " + e.getMessage(), e);
        } finally {
            connection.disconnect();
        }
    }
    
    private SraSearchResult parseSearchResult(Document doc) {
        Element root = doc.getRootElement();
        
        SraSearchResult result = new SraSearchResult();
        
        // Parse count and pagination info
        Element countElement = root.getChild("Count");
        if (countElement != null) {
            result.setTotalCount(Integer.parseInt(countElement.getText()));
        }
        
        Element retStartElement = root.getChild("RetStart");
        if (retStartElement != null) {
            result.setRetStart(Integer.parseInt(retStartElement.getText()));
        }
        
        Element retMaxElement = root.getChild("RetMax");
        if (retMaxElement != null) {
            result.setRetMax(Integer.parseInt(retMaxElement.getText()));
        }
        
        Element queryKeyElement = root.getChild("QueryKey");
        if (queryKeyElement != null) {
            result.setQueryKey(queryKeyElement.getText());
        }
        
        Element webEnvElement = root.getChild("WebEnv");
        if (webEnvElement != null) {
            result.setWebEnv(webEnvElement.getText());
        }
        
        return result;
    }
    
    private List<String> extractUids(Document doc) {
        List<String> uids = new ArrayList<>();
        Element root = doc.getRootElement();
        Element idListElement = root.getChild("IdList");
        
        if (idListElement != null) {
            @SuppressWarnings("unchecked")
            List<Element> idElements = idListElement.getChildren("Id");
            for (Element idElement : idElements) {
                uids.add(idElement.getText());
            }
        }
        
        return uids;
    }
    
    private List<SraRecord> fetchDetailedRecords(List<String> uids) throws IOException {
        if (uids.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Use esummary to get detailed information
        String summaryUrl = buildSummaryUrl(uids);
        Document summaryDoc = fetchXmlDocument(summaryUrl);
        
        return parseSummaryRecords(summaryDoc);
    }
    
    private String buildSummaryUrl(List<String> uids) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder(ESUMMARY_URL);
        url.append("?db=").append(DATABASE);
        url.append("&id=").append(String.join(",", uids));
        url.append("&retmode=xml");
        
        return url.toString();
    }
    
    private List<SraRecord> parseSummaryRecords(Document doc) {
        List<SraRecord> records = new ArrayList<>();
        Element root = doc.getRootElement();
        
        @SuppressWarnings("unchecked")
        List<Element> docSumElements = root.getChildren("DocSum");
        
        for (Element docSum : docSumElements) {
            SraRecord record = parseSingleSummaryRecord(docSum);
            if (record != null) {
                records.add(record);
            }
        }
        
        return records;
    }
    
    private SraRecord parseSingleSummaryRecord(Element docSum) {
        SraRecord record = new SraRecord();
        
        // Parse UID
        Element idElement = docSum.getChild("Id");
        if (idElement != null) {
            record.addAttribute("uid", idElement.getText());
        }
        
        // Parse items
        @SuppressWarnings("unchecked")
        List<Element> itemElements = docSum.getChildren("Item");
        
        for (Element item : itemElements) {
            String name = item.getAttributeValue("Name");
            String type = item.getAttributeValue("Type");
            String content = item.getText();
            
            if (name == null) continue;
            
            // Handle ExpXml specially - it contains nested XML with the actual metadata
            if ("ExpXml".equals(name) && content != null) {
                parseExpXml(record, content);
                continue;
            }
            
            // Handle Runs specially - it contains run information
            if ("Runs".equals(name) && content != null) {
                parseRunsXml(record, content);
                continue;
            }
            
            if (content == null) continue;
            
            switch (name) {
                case "CreateDate":
                case "UpdateDate":
                    try {
                        // Try different date formats
                        Date date;
                        if (content.contains("/")) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                            date = format.parse(content);
                        } else {
                            date = dateFormat.parse(content);
                        }
                        
                        if ("CreateDate".equals(name)) {
                            record.setSubmissionDate(date);
                        } else {
                            record.setPublicationDate(date);
                        }
                    } catch (ParseException e) {
                        // Ignore date parsing errors
                    }
                    break;
                default:
                    // Store as generic attribute
                    record.addAttribute(name, content);
                    break;
            }
        }
        
        return record;
    }
    
    /**
     * Parse the nested XML in ExpXml field
     */
    private void parseExpXml(SraRecord record, String expXml) {
        try {
            // The ExpXml contains escaped XML - we need to unescape and parse it
            String unescapedXml = expXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
            
            // Add a root wrapper to make it valid XML since it contains multiple root elements
            String wrappedXml = "<root>" + unescapedXml + "</root>";
            
            SAXBuilder builder = new SAXBuilder();
            Document expDoc = builder.build(new StringReader(wrappedXml));
            Element root = expDoc.getRootElement();
            
            // Extract title
            Element summaryElement = root.getChild("Summary");
            if (summaryElement != null) {
                Element titleElement = summaryElement.getChild("Title");
                if (titleElement != null) {
                    record.setTitle(titleElement.getText());
                }
                
                // Extract platform
                Element platformElement = summaryElement.getChild("Platform");
                if (platformElement != null) {
                    String platform = platformElement.getText();
                    String instrumentModel = platformElement.getAttributeValue("instrument_model");
                    if (instrumentModel != null) {
                        platform = instrumentModel + " (" + platform + ")";
                    }
                    record.setPlatform(platform);
                }
                
                // Extract statistics
                Element statsElement = summaryElement.getChild("Statistics");
                if (statsElement != null) {
                    String totalSpots = statsElement.getAttributeValue("total_spots");
                    String totalBases = statsElement.getAttributeValue("total_bases");
                    
                    if (totalSpots != null) {
                        try {
                            record.setTotalSpots(Long.parseLong(totalSpots));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    
                    if (totalBases != null) {
                        try {
                            record.setTotalBases(Long.parseLong(totalBases));
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                }
            }
            
            // Extract organism
            Element organismElement = root.getChild("Organism");
            if (organismElement != null) {
                String scientificName = organismElement.getAttributeValue("ScientificName");
                if (scientificName != null) {
                    record.setOrganism(scientificName);
                }
            }
            
            // Extract library info
            Element libraryElement = root.getChild("Library_descriptor");
            if (libraryElement != null) {
                Element strategyElement = libraryElement.getChild("LIBRARY_STRATEGY");
                if (strategyElement != null) {
                    record.setLibraryStrategy(strategyElement.getText());
                }
                
                Element sourceElement = libraryElement.getChild("LIBRARY_SOURCE");
                if (sourceElement != null) {
                    record.setLibrarySource(sourceElement.getText());
                }
                
                Element selectionElement = libraryElement.getChild("LIBRARY_SELECTION");
                if (selectionElement != null) {
                    record.setLibrarySelection(selectionElement.getText());
                }
                
                Element layoutElement = libraryElement.getChild("LIBRARY_LAYOUT");
                if (layoutElement != null) {
                    if (layoutElement.getChild("PAIRED") != null) {
                        record.setLibraryLayout("PAIRED");
                    } else if (layoutElement.getChild("SINGLE") != null) {
                        record.setLibraryLayout("SINGLE");
                    }
                }
            }
            
            // Extract study, sample, experiment info
            Element studyElement = root.getChild("Study");
            if (studyElement != null) {
                String studyAcc = studyElement.getAttributeValue("acc");
                if (studyAcc != null) {
                    record.setStudy(studyAcc);
                }
            }
            
            Element sampleElement = root.getChild("Sample");
            if (sampleElement != null) {
                String sampleAcc = sampleElement.getAttributeValue("acc");
                if (sampleAcc != null) {
                    record.setSample(sampleAcc);
                }
            }
            
            Element experimentElement = root.getChild("Experiment");
            if (experimentElement != null) {
                String expAcc = experimentElement.getAttributeValue("acc");
                if (expAcc != null) {
                    record.setExperiment(expAcc);
                }
            }
            
            // Extract bioproject and biosample
            Element bioprojectElement = root.getChild("Bioproject");
            if (bioprojectElement != null) {
                record.setBioProject(bioprojectElement.getText());
            }
            
            Element biosampleElement = root.getChild("Biosample");
            if (biosampleElement != null) {
                record.setBioSample(biosampleElement.getText());
            }
            
            // Try alternative paths for BioProject and BioSample
            if (record.getBioProject() == null) {
                Element altStudyElement = root.getChild("Study");
                if (altStudyElement != null) {
                    Element studyDescriptorElement = altStudyElement.getChild("DESCRIPTOR");
                    if (studyDescriptorElement != null) {
                        Element studyLinksElement = studyDescriptorElement.getChild("STUDY_LINKS");
                        if (studyLinksElement != null) {
                            @SuppressWarnings("unchecked")
                            List<Element> linkElements = studyLinksElement.getChildren("STUDY_LINK");
                            for (Element linkElement : linkElements) {
                                Element xrefLinkElement = linkElement.getChild("XREF_LINK");
                                if (xrefLinkElement != null) {
                                    Element dbElement = xrefLinkElement.getChild("DB");
                                    Element idElement = xrefLinkElement.getChild("ID");
                                    if (dbElement != null && idElement != null) {
                                        String db = dbElement.getText();
                                        String id = idElement.getText();
                                        if ("bioproject".equalsIgnoreCase(db)) {
                                            record.setBioProject(id);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (record.getBioSample() == null) {
                Element altSampleElement = root.getChild("Sample");
                if (altSampleElement != null) {
                    Element sampleLinksElement = altSampleElement.getChild("SAMPLE_LINKS");
                    if (sampleLinksElement != null) {
                        @SuppressWarnings("unchecked")
                        List<Element> linkElements = sampleLinksElement.getChildren("SAMPLE_LINK");
                        for (Element linkElement : linkElements) {
                            Element xrefLinkElement = linkElement.getChild("XREF_LINK");
                            if (xrefLinkElement != null) {
                                Element dbElement = xrefLinkElement.getChild("DB");
                                Element idElement = xrefLinkElement.getChild("ID");
                                if (dbElement != null && idElement != null) {
                                    String db = dbElement.getText();
                                    String id = idElement.getText();
                                    if ("biosample".equalsIgnoreCase(db)) {
                                        record.setBioSample(id);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            // Ignore XML parsing errors for ExpXml - not critical for basic functionality
        }
    }
    
    /**
     * Parse the nested XML in Runs field
     */
    private void parseRunsXml(SraRecord record, String runsXml) {
        try {
            // The Runs contains escaped XML with run information
            String unescapedXml = runsXml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
            
            SAXBuilder builder = new SAXBuilder();
            Document runsDoc = builder.build(new StringReader(unescapedXml));
            Element runElement = runsDoc.getRootElement();
            
            // Extract run accession
            String runAcc = runElement.getAttributeValue("acc");
            if (runAcc != null) {
                record.setRun(runAcc);
                // If no accession set yet, use the run accession
                if (record.getAccession() == null) {
                    record.setAccession(runAcc);
                }
            }
            
        } catch (Exception e) {
            // Ignore XML parsing errors for Runs - not critical for basic functionality
        }
    }
    
    /**
     * Check if the service is available
     */
    public boolean isServiceAvailable() {
        try {
            URL url = new URL(EUTILS_BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", userAgent);
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
}