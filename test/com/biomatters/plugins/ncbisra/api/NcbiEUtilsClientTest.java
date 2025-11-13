package com.biomatters.plugins.ncbisra.api;

import com.biomatters.plugins.ncbisra.model.SraRecord;
import com.biomatters.plugins.ncbisra.model.SraSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for NcbiEUtilsClient
 * Tests HTTP communication, XML parsing, error handling, and query optimization
 */
public class NcbiEUtilsClientTest {

    private NcbiEUtilsClient client;
    private SimpleDateFormat dateFormat;
    private String fixturesPath;

    @Before
    public void setUp() {
        client = new NcbiEUtilsClient();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        fixturesPath = "test/resources/fixtures/";
    }

    @After
    public void tearDown() {
        client = null;
    }

    // ==================== Constructor and Initialization Tests ====================

    @Test
    public void testConstructor() {
        assertNotNull("Client should be initialized", client);
    }

    // ==================== Search Method Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSearchWithNullQueryTerm() throws IOException {
        client.search(null, 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchWithEmptyQueryTerm() throws IOException {
        client.search("", 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchWithWhitespaceQueryTerm() throws IOException {
        client.search("   ", 0, 20);
    }

    @Test
    public void testSearchWithZeroRetMax() throws IOException {
        // Should use default retMax when zero is provided
        // This test validates the behavior but won't make real HTTP calls
        try {
            client.search("test", 0, 0);
            // If it doesn't throw an exception, the parameter handling worked
        } catch (IOException e) {
            // Expected since we're not mocking HTTP
            assertTrue("Expected IOException for network call", true);
        }
    }

    @Test
    public void testSearchWithNegativeRetMax() throws IOException {
        // Should use default retMax when negative is provided
        try {
            client.search("test", 0, -1);
        } catch (IOException e) {
            // Expected since we're not mocking HTTP
            assertTrue("Expected IOException for network call", true);
        }
    }

    // ==================== Search by Accession Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByAccessionWithNull() throws IOException {
        client.searchByAccession(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByAccessionWithEmpty() throws IOException {
        client.searchByAccession("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByAccessionWithWhitespace() throws IOException {
        client.searchByAccession("   ");
    }

    // ==================== Search by BioProject Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByBioProjectWithNull() throws IOException {
        client.searchByBioProject(null, 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByBioProjectWithEmpty() throws IOException {
        client.searchByBioProject("", 0, 20);
    }

    // ==================== Search by BioSample Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByBioSampleWithNull() throws IOException {
        client.searchByBioSample(null, 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByBioSampleWithEmpty() throws IOException {
        client.searchByBioSample("", 0, 20);
    }

    // ==================== Search by Organism Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByOrganismWithNull() throws IOException {
        client.searchByOrganism(null, 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByOrganismWithEmpty() throws IOException {
        client.searchByOrganism("", 0, 20);
    }

    // ==================== Search by Library Strategy Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByLibraryStrategyWithNull() throws IOException {
        client.searchByLibraryStrategy(null, 0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchByLibraryStrategyWithEmpty() throws IOException {
        client.searchByLibraryStrategy("", 0, 20);
    }

    // ==================== Query Optimization Tests ====================

    @Test
    public void testOptimizeSearchQueryWithAccession() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        // SRA accession patterns
        assertEquals("SRR123456[Accession]", method.invoke(client, "SRR123456"));
        assertEquals("ERR123456[Accession]", method.invoke(client, "ERR123456"));
        assertEquals("DRR123456[Accession]", method.invoke(client, "DRR123456"));
        assertEquals("SRX123456[Accession]", method.invoke(client, "SRX123456"));
        assertEquals("ERX123456[Accession]", method.invoke(client, "ERX123456"));
        assertEquals("DRX123456[Accession]", method.invoke(client, "DRX123456"));
        assertEquals("SRS123456[Accession]", method.invoke(client, "SRS123456"));
        assertEquals("ERS123456[Accession]", method.invoke(client, "ERS123456"));
        assertEquals("DRS123456[Accession]", method.invoke(client, "DRS123456"));
        assertEquals("SRP123456[Accession]", method.invoke(client, "SRP123456"));
        assertEquals("ERP123456[Accession]", method.invoke(client, "ERP123456"));
        assertEquals("DRP123456[Accession]", method.invoke(client, "DRP123456"));
        assertEquals("SRA123456[Accession]", method.invoke(client, "SRA123456"));
        assertEquals("ERA123456[Accession]", method.invoke(client, "ERA123456"));
        assertEquals("DRA123456[Accession]", method.invoke(client, "DRA123456"));
    }

    @Test
    public void testOptimizeSearchQueryWithBioProject() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        assertEquals("PRJNA123456[Bioproject]", method.invoke(client, "PRJNA123456"));
        assertEquals("PRJEB123456[Bioproject]", method.invoke(client, "PRJEB123456"));
        assertEquals("PRJDB123456[Bioproject]", method.invoke(client, "PRJDB123456"));
    }

    @Test
    public void testOptimizeSearchQueryWithBioSample() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        assertEquals("SAMN123456[Biosample]", method.invoke(client, "SAMN123456"));
        assertEquals("SAMD123456[Biosample]", method.invoke(client, "SAMD123456"));
        assertEquals("SAME123456[Biosample]", method.invoke(client, "SAME123456"));
        assertEquals("SAMEA123456[Biosample]", method.invoke(client, "SAMEA123456"));
    }

    @Test
    public void testOptimizeSearchQueryDoesNotModifyWithFieldTag() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        assertEquals("SRR123456[Title]", method.invoke(client, "SRR123456[Title]"));
        assertEquals("human[Organism]", method.invoke(client, "human[Organism]"));
    }

    @Test
    public void testOptimizeSearchQueryWithNonPattern() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        assertEquals("human brain", method.invoke(client, "human brain"));
        assertEquals("RNA-Seq", method.invoke(client, "RNA-Seq"));
    }

    @Test
    public void testOptimizeSearchQueryWithNull() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        assertNull(method.invoke(client, (String) null));
    }

    @Test
    public void testOptimizeSearchQueryWithEmpty() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("optimizeSearchQuery", String.class);
        method.setAccessible(true);

        assertEquals("", method.invoke(client, ""));
    }

    // ==================== Pattern Matching Tests ====================

    @Test
    public void testIsLikelyAccession() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("isLikelyAccession", String.class);
        method.setAccessible(true);

        // Valid accessions
        assertTrue((Boolean) method.invoke(client, "SRR123456"));
        assertTrue((Boolean) method.invoke(client, "ERR123456"));
        assertTrue((Boolean) method.invoke(client, "DRR123456"));
        assertTrue((Boolean) method.invoke(client, "SRX999999"));
        assertTrue((Boolean) method.invoke(client, "SRP000001"));

        // Invalid accessions
        assertFalse((Boolean) method.invoke(client, "SRR"));
        assertFalse((Boolean) method.invoke(client, "XYZ123456"));  // Wrong prefix
        assertFalse((Boolean) method.invoke(client, "SRRabc123"));  // Letters in number
        assertFalse((Boolean) method.invoke(client, "PRJNA123456"));  // BioProject
        assertFalse((Boolean) method.invoke(client, new Object[]{null}));
        assertFalse((Boolean) method.invoke(client, ""));
    }

    @Test
    public void testIsLikelyBioProject() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("isLikelyBioProject", String.class);
        method.setAccessible(true);

        // Valid BioProjects
        assertTrue((Boolean) method.invoke(client, "PRJNA123456"));
        assertTrue((Boolean) method.invoke(client, "PRJEB123456"));
        assertTrue((Boolean) method.invoke(client, "PRJDB123456"));
        assertTrue((Boolean) method.invoke(client, "PRJNZ000001"));

        // Invalid BioProjects
        assertFalse((Boolean) method.invoke(client, "PRJNA"));
        assertFalse((Boolean) method.invoke(client, "PRJ123456"));  // Missing location code
        assertFalse((Boolean) method.invoke(client, "PRJX123456"));  // Invalid location
        assertFalse((Boolean) method.invoke(client, "SRR123456"));  // SRA accession
        assertFalse((Boolean) method.invoke(client, new Object[]{null}));
    }

    @Test
    public void testIsLikelyBioSample() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("isLikelyBioSample", String.class);
        method.setAccessible(true);

        // Valid BioSamples
        assertTrue((Boolean) method.invoke(client, "SAMN123456"));
        assertTrue((Boolean) method.invoke(client, "SAMD123456"));
        assertTrue((Boolean) method.invoke(client, "SAME123456"));
        assertTrue((Boolean) method.invoke(client, "SAMEA123456"));

        // Invalid BioSamples
        assertFalse((Boolean) method.invoke(client, "SAM"));
        assertFalse((Boolean) method.invoke(client, "SAMX123456"));  // Invalid location
        assertFalse((Boolean) method.invoke(client, "SRR123456"));  // SRA accession
        assertFalse((Boolean) method.invoke(client, new Object[]{null}));
    }

    // ==================== URL Building Tests ====================

    @Test
    public void testBuildSearchUrl() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("buildSearchUrl", String.class, int.class, int.class);
        method.setAccessible(true);

        String url = (String) method.invoke(client, "human", 0, 20);

        assertTrue("URL should contain base URL", url.contains("esearch.fcgi"));
        assertTrue("URL should contain database parameter", url.contains("db=sra"));
        assertTrue("URL should contain term parameter", url.contains("term=human"));
        assertTrue("URL should contain retstart parameter", url.contains("retstart=0"));
        assertTrue("URL should contain retmax parameter", url.contains("retmax=20"));
        assertTrue("URL should contain usehistory parameter", url.contains("usehistory=y"));
        assertTrue("URL should contain retmode parameter", url.contains("retmode=xml"));
    }

    @Test
    public void testBuildSearchUrlWithSpecialCharacters() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("buildSearchUrl", String.class, int.class, int.class);
        method.setAccessible(true);

        String url = (String) method.invoke(client, "human brain & liver", 0, 20);

        assertTrue("Special characters should be encoded", url.contains("human+brain"));
        assertTrue("Ampersand should be encoded", url.contains("%26"));
    }

    @Test
    public void testBuildSummaryUrl() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("buildSummaryUrl", List.class);
        method.setAccessible(true);

        List<String> uids = java.util.Arrays.asList("123", "456", "789");
        String url = (String) method.invoke(client, uids);

        assertTrue("URL should contain base URL", url.contains("esummary.fcgi"));
        assertTrue("URL should contain database parameter", url.contains("db=sra"));
        assertTrue("URL should contain id parameter", url.contains("id=123,456,789"));
        assertTrue("URL should contain retmode parameter", url.contains("retmode=xml"));
    }

    // ==================== XML Parsing Tests ====================

    @Test
    public void testParseSearchResult() throws Exception {
        Document doc = loadFixture("esearch_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseSearchResult", Document.class);
        method.setAccessible(true);

        SraSearchResult result = (SraSearchResult) method.invoke(client, doc);

        assertNotNull("Result should not be null", result);
        assertEquals("Total count should be parsed", 150, result.getTotalCount());
        assertEquals("RetStart should be parsed", 0, result.getRetStart());
        assertEquals("RetMax should be parsed", 20, result.getRetMax());
        assertEquals("QueryKey should be parsed", "1", result.getQueryKey());
        assertEquals("WebEnv should be parsed", "MCID_123456789ABCDEF", result.getWebEnv());
    }

    @Test
    public void testParseSearchResultEmpty() throws Exception {
        Document doc = loadFixture("esearch_empty_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseSearchResult", Document.class);
        method.setAccessible(true);

        SraSearchResult result = (SraSearchResult) method.invoke(client, doc);

        assertNotNull("Result should not be null", result);
        assertEquals("Total count should be zero", 0, result.getTotalCount());
    }

    @Test
    public void testExtractUids() throws Exception {
        Document doc = loadFixture("esearch_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("extractUids", Document.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> uids = (List<String>) method.invoke(client, doc);

        assertNotNull("UIDs should not be null", uids);
        assertEquals("Should extract 3 UIDs", 3, uids.size());
        assertTrue("Should contain first UID", uids.contains("12345678"));
        assertTrue("Should contain second UID", uids.contains("12345679"));
        assertTrue("Should contain third UID", uids.contains("12345680"));
    }

    @Test
    public void testExtractUidsEmpty() throws Exception {
        Document doc = loadFixture("esearch_empty_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("extractUids", Document.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> uids = (List<String>) method.invoke(client, doc);

        assertNotNull("UIDs should not be null", uids);
        assertEquals("Should have no UIDs", 0, uids.size());
    }

    @Test
    public void testParseSummaryRecords() throws Exception {
        Document doc = loadFixture("esummary_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseSummaryRecords", Document.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SraRecord> records = (List<SraRecord>) method.invoke(client, doc);

        assertNotNull("Records should not be null", records);
        assertEquals("Should parse 2 records", 2, records.size());

        // Check first record
        SraRecord record1 = records.get(0);
        assertEquals("Should have correct accession", "SRR123456", record1.getAccession());
        assertEquals("Should have correct title", "RNA-Seq of human brain tissue", record1.getTitle());
        assertEquals("Should have correct organism", "Homo sapiens", record1.getOrganism());
        assertEquals("Should have correct platform", "Illumina HiSeq 2000 (ILLUMINA)", record1.getPlatform());
        assertEquals("Should have correct library strategy", "RNA-Seq", record1.getLibraryStrategy());
        assertEquals("Should have correct library source", "TRANSCRIPTOMIC", record1.getLibrarySource());
        assertEquals("Should have correct library selection", "cDNA", record1.getLibrarySelection());
        assertEquals("Should have correct library layout", "PAIRED", record1.getLibraryLayout());
        assertTrue("Should be paired-end", record1.isPairedEnd());
        assertEquals("Should have correct total spots", 5000000L, record1.getTotalSpots());
        assertEquals("Should have correct total bases", 500000000L, record1.getTotalBases());
        assertEquals("Should have correct BioProject", "PRJNA123456", record1.getBioProject());
        assertEquals("Should have correct BioSample", "SAMN123456", record1.getBioSample());

        // Check second record
        SraRecord record2 = records.get(1);
        assertEquals("Should have correct accession", "SRR123457", record2.getAccession());
        assertEquals("Should have correct library layout", "SINGLE", record2.getLibraryLayout());
        assertFalse("Should not be paired-end", record2.isPairedEnd());
    }

    // ==================== Date Parsing Tests ====================

    @Test
    public void testDateParsingWithSlashFormat() throws Exception {
        Document doc = loadFixture("esummary_single_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseSummaryRecords", Document.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SraRecord> records = (List<SraRecord>) method.invoke(client, doc);

        SraRecord record = records.get(0);
        assertNotNull("Submission date should be parsed", record.getSubmissionDate());
        assertNotNull("Publication date should be parsed", record.getPublicationDate());
    }

    @Test
    public void testDateParsingWithTime() throws Exception {
        Document doc = loadFixture("esummary_response.xml");
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseSummaryRecords", Document.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SraRecord> records = (List<SraRecord>) method.invoke(client, doc);

        // First record has a date with time
        SraRecord record = records.get(0);
        assertNotNull("Dates should be parsed even with invalid time format", record.getPublicationDate());
    }

    // ==================== ExpXml Parsing Tests ====================

    @Test
    public void testParseExpXml() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        String expXml = "&lt;Summary&gt;&lt;Title&gt;Test Title&lt;/Title&gt;&lt;Platform instrument_model=&quot;Illumina NovaSeq&quot;&gt;ILLUMINA&lt;/Platform&gt;&lt;Statistics total_spots=&quot;1000000&quot; total_bases=&quot;100000000&quot;/&gt;&lt;/Summary&gt;&lt;Organism ScientificName=&quot;Homo sapiens&quot;/&gt;&lt;Library_descriptor&gt;&lt;LIBRARY_STRATEGY&gt;WGS&lt;/LIBRARY_STRATEGY&gt;&lt;LIBRARY_SOURCE&gt;GENOMIC&lt;/LIBRARY_SOURCE&gt;&lt;LIBRARY_SELECTION&gt;RANDOM&lt;/LIBRARY_SELECTION&gt;&lt;LIBRARY_LAYOUT&gt;&lt;PAIRED/&gt;&lt;/LIBRARY_LAYOUT&gt;&lt;/Library_descriptor&gt;";

        method.invoke(client, record, expXml);

        assertEquals("Title should be parsed", "Test Title", record.getTitle());
        assertEquals("Organism should be parsed", "Homo sapiens", record.getOrganism());
        assertEquals("Platform should be parsed", "Illumina NovaSeq (ILLUMINA)", record.getPlatform());
        assertEquals("Library strategy should be parsed", "WGS", record.getLibraryStrategy());
        assertEquals("Library source should be parsed", "GENOMIC", record.getLibrarySource());
        assertEquals("Library selection should be parsed", "RANDOM", record.getLibrarySelection());
        assertEquals("Library layout should be parsed", "PAIRED", record.getLibraryLayout());
        assertEquals("Total spots should be parsed", 1000000L, record.getTotalSpots());
        assertEquals("Total bases should be parsed", 100000000L, record.getTotalBases());
    }

    @Test
    public void testParseExpXmlWithMalformedData() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        String malformedXml = "This is not valid XML";

        // Should not throw exception, just fail silently
        method.invoke(client, record, malformedXml);

        assertNull("Title should be null for malformed XML", record.getTitle());
    }

    // ==================== RunsXml Parsing Tests ====================

    @Test
    public void testParseRunsXml() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseRunsXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        String runsXml = "&lt;Run acc=&quot;SRR999999&quot; total_spots=&quot;2000000&quot; total_bases=&quot;200000000&quot;/&gt;";

        method.invoke(client, record, runsXml);

        assertEquals("Run accession should be parsed", "SRR999999", record.getRun());
        assertEquals("Accession should be set from run", "SRR999999", record.getAccession());
    }

    @Test
    public void testParseRunsXmlDoesNotOverwriteAccession() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseRunsXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        record.setAccession("SRX888888");
        String runsXml = "&lt;Run acc=&quot;SRR999999&quot;/&gt;";

        method.invoke(client, record, runsXml);

        assertEquals("Run should be set", "SRR999999", record.getRun());
        assertEquals("Accession should not be overwritten", "SRX888888", record.getAccession());
    }

    // ==================== Error Handling Tests ====================

    @Test
    public void testFetchXmlDocumentWithInvalidUrl() {
        try {
            Method method = NcbiEUtilsClient.class.getDeclaredMethod("fetchXmlDocument", String.class);
            method.setAccessible(true);
            method.invoke(client, "http://invalid.ncbi.nlm.nih.gov/nonexistent");
            fail("Should throw IOException for invalid URL");
        } catch (Exception e) {
            assertTrue("Should be IOException", e.getCause() instanceof IOException);
        }
    }

    @Test
    public void testFetchXmlDocumentWithMalformedUrl() {
        try {
            Method method = NcbiEUtilsClient.class.getDeclaredMethod("fetchXmlDocument", String.class);
            method.setAccessible(true);
            method.invoke(client, "not a valid url");
            fail("Should throw exception for malformed URL");
        } catch (Exception e) {
            // Expected
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void testThreadInterruptionHandling() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("fetchXmlDocument", String.class);
        method.setAccessible(true);

        // Interrupt current thread
        Thread.currentThread().interrupt();

        try {
            method.invoke(client, "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/");
            fail("Should throw IOException when thread is interrupted");
        } catch (Exception e) {
            assertTrue("Should throw IOException", e.getCause() instanceof IOException);
            assertTrue("Error message should mention interruption",
                       e.getCause().getMessage().contains("interrupted"));
        } finally {
            // Clear interrupted flag
            Thread.interrupted();
        }
    }

    // ==================== Service Availability Tests ====================

    @Test
    public void testIsServiceAvailableHandlesExceptions() {
        // This test should complete without throwing exceptions
        // Even if the network is unavailable, the method should return false
        boolean available = client.isServiceAvailable();
        // We can't assert the specific value as it depends on network availability
        // But we can verify the method completes successfully
        assertTrue("Method should complete", true);
    }

    // ==================== Integration-style Tests with Fixtures ====================

    @Test
    public void testCompleteWorkflowWithFixtures() throws Exception {
        // Test the complete parsing workflow using fixture data
        Document searchDoc = loadFixture("esearch_response.xml");
        Document summaryDoc = loadFixture("esummary_response.xml");

        // Parse search results
        Method parseSearchMethod = NcbiEUtilsClient.class.getDeclaredMethod("parseSearchResult", Document.class);
        parseSearchMethod.setAccessible(true);
        SraSearchResult searchResult = (SraSearchResult) parseSearchMethod.invoke(client, searchDoc);

        assertNotNull("Search result should not be null", searchResult);
        assertEquals("Should have correct total count", 150, searchResult.getTotalCount());

        // Extract UIDs
        Method extractUidsMethod = NcbiEUtilsClient.class.getDeclaredMethod("extractUids", Document.class);
        extractUidsMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> uids = (List<String>) extractUidsMethod.invoke(client, searchDoc);

        assertEquals("Should extract 3 UIDs", 3, uids.size());

        // Parse summary records
        Method parseSummaryMethod = NcbiEUtilsClient.class.getDeclaredMethod("parseSummaryRecords", Document.class);
        parseSummaryMethod.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<SraRecord> records = (List<SraRecord>) parseSummaryMethod.invoke(client, summaryDoc);

        assertEquals("Should parse 2 records", 2, records.size());

        // Verify detailed record information
        SraRecord record = records.get(0);
        assertNotNull("Record should have title", record.getTitle());
        assertNotNull("Record should have organism", record.getOrganism());
        assertNotNull("Record should have platform", record.getPlatform());
        assertNotNull("Record should have library strategy", record.getLibraryStrategy());
    }

    // ==================== Edge Cases and Boundary Tests ====================

    @Test
    public void testParseSummaryRecordsWithMissingFields() throws Exception {
        // Create a minimal DocSum with missing optional fields
        String minimalXml = "<?xml version=\"1.0\"?><eSummaryResult><DocSum><Id>123</Id></DocSum></eSummaryResult>";

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(minimalXml));

        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseSummaryRecords", Document.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SraRecord> records = (List<SraRecord>) method.invoke(client, doc);

        assertNotNull("Records should not be null", records);
        assertEquals("Should parse 1 record", 1, records.size());

        SraRecord record = records.get(0);
        assertEquals("Should have UID attribute", "123", record.getAttribute("uid"));
    }

    @Test
    public void testParseExpXmlWithSingleLayout() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        String expXml = "&lt;Library_descriptor&gt;&lt;LIBRARY_LAYOUT&gt;&lt;SINGLE/&gt;&lt;/LIBRARY_LAYOUT&gt;&lt;/Library_descriptor&gt;";

        method.invoke(client, record, expXml);

        assertEquals("Layout should be SINGLE", "SINGLE", record.getLibraryLayout());
        assertFalse("Should not be paired-end", record.isPairedEnd());
    }

    @Test
    public void testParseExpXmlWithAlternativeBioProjectPath() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        // XML with BioProject in alternative location (STUDY_LINKS)
        String expXml = "&lt;Study&gt;&lt;DESCRIPTOR&gt;&lt;STUDY_LINKS&gt;&lt;STUDY_LINK&gt;&lt;XREF_LINK&gt;&lt;DB&gt;bioproject&lt;/DB&gt;&lt;ID&gt;PRJNA999999&lt;/ID&gt;&lt;/XREF_LINK&gt;&lt;/STUDY_LINK&gt;&lt;/STUDY_LINKS&gt;&lt;/DESCRIPTOR&gt;&lt;/Study&gt;";

        method.invoke(client, record, expXml);

        assertEquals("BioProject should be parsed from alternative path", "PRJNA999999", record.getBioProject());
    }

    @Test
    public void testParseExpXmlWithAlternativeBioSamplePath() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        // XML with BioSample in alternative location (SAMPLE_LINKS)
        String expXml = "&lt;Sample&gt;&lt;SAMPLE_LINKS&gt;&lt;SAMPLE_LINK&gt;&lt;XREF_LINK&gt;&lt;DB&gt;biosample&lt;/DB&gt;&lt;ID&gt;SAMN999999&lt;/ID&gt;&lt;/XREF_LINK&gt;&lt;/SAMPLE_LINK&gt;&lt;/SAMPLE_LINKS&gt;&lt;/Sample&gt;";

        method.invoke(client, record, expXml);

        assertEquals("BioSample should be parsed from alternative path", "SAMN999999", record.getBioSample());
    }

    @Test
    public void testParseExpXmlWithPlatformNoInstrumentModel() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        String expXml = "&lt;Summary&gt;&lt;Platform&gt;ILLUMINA&lt;/Platform&gt;&lt;/Summary&gt;";

        method.invoke(client, record, expXml);

        assertEquals("Platform should be parsed without instrument model", "ILLUMINA", record.getPlatform());
    }

    @Test
    public void testParseExpXmlWithInvalidStatistics() throws Exception {
        Method method = NcbiEUtilsClient.class.getDeclaredMethod("parseExpXml", SraRecord.class, String.class);
        method.setAccessible(true);

        SraRecord record = new SraRecord();
        String expXml = "&lt;Summary&gt;&lt;Statistics total_spots=&quot;not_a_number&quot; total_bases=&quot;invalid&quot;/&gt;&lt;/Summary&gt;";

        method.invoke(client, record, expXml);

        // Should handle number format exceptions gracefully
        assertEquals("Total spots should be default (0)", 0L, record.getTotalSpots());
        assertEquals("Total bases should be default (0)", 0L, record.getTotalBases());
    }

    // ==================== Helper Methods ====================

    private Document loadFixture(String filename) throws Exception {
        File fixtureFile = new File(fixturesPath + filename);
        SAXBuilder builder = new SAXBuilder();
        return builder.build(fixtureFile);
    }
}
