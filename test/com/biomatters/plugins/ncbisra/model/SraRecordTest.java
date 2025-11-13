package com.biomatters.plugins.ncbisra.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for SraRecord model class
 * Tests cover constructors, getters/setters, business logic, edge cases, and boundary values
 */
public class SraRecordTest {

    private SraRecord record;
    private Date testDate;

    @Before
    public void setUp() {
        record = new SraRecord();
        testDate = new Date();
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testDefaultConstructor() {
        SraRecord newRecord = new SraRecord();
        assertNotNull("Default constructor should create instance", newRecord);
        assertNull("Default accession should be null", newRecord.getAccession());
        assertNotNull("Attributes map should be initialized", newRecord.getAttributes());
        assertTrue("Attributes map should be empty", newRecord.getAttributes().isEmpty());
    }

    @Test
    public void testConstructorWithAccession() {
        String accession = "SRR123456";
        SraRecord newRecord = new SraRecord(accession);
        assertNotNull("Constructor with accession should create instance", newRecord);
        assertEquals("Accession should match", accession, newRecord.getAccession());
        assertNotNull("Attributes map should be initialized", newRecord.getAttributes());
        assertTrue("Attributes map should be empty", newRecord.getAttributes().isEmpty());
    }

    @Test
    public void testConstructorWithNullAccession() {
        SraRecord newRecord = new SraRecord(null);
        assertNotNull("Constructor should handle null accession", newRecord);
        assertNull("Accession should be null", newRecord.getAccession());
        assertNotNull("Attributes map should be initialized", newRecord.getAttributes());
    }

    @Test
    public void testConstructorWithEmptyAccession() {
        SraRecord newRecord = new SraRecord("");
        assertNotNull("Constructor should handle empty accession", newRecord);
        assertEquals("Accession should be empty string", "", newRecord.getAccession());
    }

    // ==================== Accession Tests ====================

    @Test
    public void testGetSetAccession() {
        String accession = "SRR123456";
        record.setAccession(accession);
        assertEquals("Accession getter should return set value", accession, record.getAccession());
    }

    @Test
    public void testSetAccessionNull() {
        record.setAccession("SRR123456");
        record.setAccession(null);
        assertNull("Accession should be null after setting to null", record.getAccession());
    }

    @Test
    public void testSetAccessionEmpty() {
        record.setAccession("");
        assertEquals("Empty accession should be preserved", "", record.getAccession());
    }

    @Test
    public void testAccessionWithSpecialCharacters() {
        String accession = "SRR_123-456.7";
        record.setAccession(accession);
        assertEquals("Accession with special characters should be preserved", accession, record.getAccession());
    }

    // ==================== String Field Tests ====================

    @Test
    public void testGetSetTitle() {
        String title = "RNA-Seq of Homo sapiens";
        record.setTitle(title);
        assertEquals("Title should match", title, record.getTitle());
    }

    @Test
    public void testSetTitleNull() {
        record.setTitle("Test");
        record.setTitle(null);
        assertNull("Title should be null", record.getTitle());
    }

    @Test
    public void testGetSetOrganism() {
        String organism = "Homo sapiens";
        record.setOrganism(organism);
        assertEquals("Organism should match", organism, record.getOrganism());
    }

    @Test
    public void testGetSetStudy() {
        String study = "SRP123456";
        record.setStudy(study);
        assertEquals("Study should match", study, record.getStudy());
    }

    @Test
    public void testGetSetSample() {
        String sample = "SRS123456";
        record.setSample(sample);
        assertEquals("Sample should match", sample, record.getSample());
    }

    @Test
    public void testGetSetExperiment() {
        String experiment = "SRX123456";
        record.setExperiment(experiment);
        assertEquals("Experiment should match", experiment, record.getExperiment());
    }

    @Test
    public void testGetSetRun() {
        String run = "SRR123456";
        record.setRun(run);
        assertEquals("Run should match", run, record.getRun());
    }

    @Test
    public void testGetSetPlatform() {
        String platform = "ILLUMINA";
        record.setPlatform(platform);
        assertEquals("Platform should match", platform, record.getPlatform());
    }

    @Test
    public void testGetSetLibraryStrategy() {
        String strategy = "RNA-Seq";
        record.setLibraryStrategy(strategy);
        assertEquals("Library strategy should match", strategy, record.getLibraryStrategy());
    }

    @Test
    public void testGetSetLibrarySource() {
        String source = "TRANSCRIPTOMIC";
        record.setLibrarySource(source);
        assertEquals("Library source should match", source, record.getLibrarySource());
    }

    @Test
    public void testGetSetLibrarySelection() {
        String selection = "cDNA";
        record.setLibrarySelection(selection);
        assertEquals("Library selection should match", selection, record.getLibrarySelection());
    }

    @Test
    public void testGetSetLibraryLayout() {
        String layout = "PAIRED";
        record.setLibraryLayout(layout);
        assertEquals("Library layout should match", layout, record.getLibraryLayout());
    }

    @Test
    public void testGetSetCenterName() {
        String centerName = "BROAD";
        record.setCenterName(centerName);
        assertEquals("Center name should match", centerName, record.getCenterName());
    }

    @Test
    public void testGetSetBioProject() {
        String bioProject = "PRJNA123456";
        record.setBioProject(bioProject);
        assertEquals("BioProject should match", bioProject, record.getBioProject());
    }

    @Test
    public void testGetSetBioSample() {
        String bioSample = "SAMN123456";
        record.setBioSample(bioSample);
        assertEquals("BioSample should match", bioSample, record.getBioSample());
    }

    // ==================== Date Field Tests ====================

    @Test
    public void testGetSetSubmissionDate() {
        record.setSubmissionDate(testDate);
        assertEquals("Submission date should match", testDate, record.getSubmissionDate());
    }

    @Test
    public void testSetSubmissionDateNull() {
        record.setSubmissionDate(testDate);
        record.setSubmissionDate(null);
        assertNull("Submission date should be null", record.getSubmissionDate());
    }

    @Test
    public void testGetSetPublicationDate() {
        record.setPublicationDate(testDate);
        assertEquals("Publication date should match", testDate, record.getPublicationDate());
    }

    @Test
    public void testSetPublicationDateNull() {
        record.setPublicationDate(testDate);
        record.setPublicationDate(null);
        assertNull("Publication date should be null", record.getPublicationDate());
    }

    // ==================== Long Field Tests ====================

    @Test
    public void testGetSetTotalSpots() {
        long spots = 1000000L;
        record.setTotalSpots(spots);
        assertEquals("Total spots should match", spots, record.getTotalSpots());
    }

    @Test
    public void testSetTotalSpotsZero() {
        record.setTotalSpots(0L);
        assertEquals("Total spots should be zero", 0L, record.getTotalSpots());
    }

    @Test
    public void testSetTotalSpotsMaxValue() {
        long maxValue = Long.MAX_VALUE;
        record.setTotalSpots(maxValue);
        assertEquals("Total spots should handle max value", maxValue, record.getTotalSpots());
    }

    @Test
    public void testSetTotalSpotsNegative() {
        // Edge case: negative values are technically allowed but may not be meaningful
        record.setTotalSpots(-1L);
        assertEquals("Total spots should store negative value", -1L, record.getTotalSpots());
    }

    @Test
    public void testGetSetTotalBases() {
        long bases = 50000000L;
        record.setTotalBases(bases);
        assertEquals("Total bases should match", bases, record.getTotalBases());
    }

    @Test
    public void testSetTotalBasesZero() {
        record.setTotalBases(0L);
        assertEquals("Total bases should be zero", 0L, record.getTotalBases());
    }

    @Test
    public void testSetTotalBasesMaxValue() {
        long maxValue = Long.MAX_VALUE;
        record.setTotalBases(maxValue);
        assertEquals("Total bases should handle max value", maxValue, record.getTotalBases());
    }

    // ==================== Attributes Map Tests ====================

    @Test
    public void testGetAttributesReturnsInitializedMap() {
        Map<String, String> attributes = record.getAttributes();
        assertNotNull("Attributes map should not be null", attributes);
        assertTrue("Attributes map should be empty initially", attributes.isEmpty());
    }

    @Test
    public void testSetAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");
        record.setAttributes(attributes);
        assertEquals("Attributes should match", attributes, record.getAttributes());
        assertEquals("Should have 2 attributes", 2, record.getAttributes().size());
    }

    @Test
    public void testSetAttributesNull() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        record.setAttributes(attributes);
        record.setAttributes(null);
        assertNull("Attributes should be null", record.getAttributes());
    }

    @Test
    public void testAddAttribute() {
        record.addAttribute("test_key", "test_value");
        assertEquals("Attribute should be added", "test_value", record.getAttribute("test_key"));
        assertEquals("Should have 1 attribute", 1, record.getAttributes().size());
    }

    @Test
    public void testAddMultipleAttributes() {
        record.addAttribute("key1", "value1");
        record.addAttribute("key2", "value2");
        record.addAttribute("key3", "value3");
        assertEquals("Should have 3 attributes", 3, record.getAttributes().size());
        assertEquals("First attribute should match", "value1", record.getAttribute("key1"));
        assertEquals("Second attribute should match", "value2", record.getAttribute("key2"));
        assertEquals("Third attribute should match", "value3", record.getAttribute("key3"));
    }

    @Test
    public void testAddAttributeOverwrite() {
        record.addAttribute("key1", "value1");
        record.addAttribute("key1", "value2");
        assertEquals("Attribute should be overwritten", "value2", record.getAttribute("key1"));
        assertEquals("Should still have 1 attribute", 1, record.getAttributes().size());
    }

    @Test
    public void testGetAttribute() {
        record.addAttribute("test_key", "test_value");
        String value = record.getAttribute("test_key");
        assertEquals("Retrieved attribute should match", "test_value", value);
    }

    @Test
    public void testGetAttributeNonExistent() {
        String value = record.getAttribute("nonexistent_key");
        assertNull("Non-existent attribute should return null", value);
    }

    @Test
    public void testGetAttributeNull() {
        String value = record.getAttribute(null);
        assertNull("Null key should return null", value);
    }

    @Test
    public void testAddAttributeNullKey() {
        record.addAttribute(null, "value");
        assertEquals("Should have 1 attribute", 1, record.getAttributes().size());
        assertEquals("Null key should be stored", "value", record.getAttribute(null));
    }

    @Test
    public void testAddAttributeNullValue() {
        record.addAttribute("key", null);
        assertTrue("Attributes should contain key", record.getAttributes().containsKey("key"));
        assertNull("Value should be null", record.getAttribute("key"));
    }

    // ==================== isPairedEnd() Business Logic Tests ====================

    @Test
    public void testIsPairedEndWithPairedUppercase() {
        record.setLibraryLayout("PAIRED");
        assertTrue("Should return true for PAIRED layout", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithPairedLowercase() {
        record.setLibraryLayout("paired");
        assertTrue("Should return true for paired layout (case-insensitive)", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithPairedMixedCase() {
        record.setLibraryLayout("PaIrEd");
        assertTrue("Should return true for PaIrEd layout (case-insensitive)", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithSingle() {
        record.setLibraryLayout("SINGLE");
        assertFalse("Should return false for SINGLE layout", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithNull() {
        record.setLibraryLayout(null);
        assertFalse("Should return false for null layout", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithEmptyString() {
        record.setLibraryLayout("");
        assertFalse("Should return false for empty string layout", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithInvalidValue() {
        record.setLibraryLayout("INVALID");
        assertFalse("Should return false for invalid layout", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndWithWhitespace() {
        record.setLibraryLayout("  PAIRED  ");
        assertFalse("Should return false for layout with whitespace", record.isPairedEnd());
    }

    @Test
    public void testIsPairedEndDefaultValue() {
        assertFalse("Should return false for unset layout", record.isPairedEnd());
    }

    // ==================== toString() Tests ====================

    @Test
    public void testToStringWithAllFields() {
        record.setAccession("SRR123456");
        record.setTitle("Test Title");
        record.setOrganism("Homo sapiens");
        record.setPlatform("ILLUMINA");
        record.setLibraryStrategy("RNA-Seq");
        record.setLibraryLayout("PAIRED");

        String result = record.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain accession", result.contains("SRR123456"));
        assertTrue("toString should contain title", result.contains("Test Title"));
        assertTrue("toString should contain organism", result.contains("Homo sapiens"));
        assertTrue("toString should contain platform", result.contains("ILLUMINA"));
        assertTrue("toString should contain library strategy", result.contains("RNA-Seq"));
        assertTrue("toString should contain library layout", result.contains("PAIRED"));
    }

    @Test
    public void testToStringWithNullFields() {
        String result = record.toString();
        assertNotNull("toString should not return null with null fields", result);
        assertTrue("toString should contain SraRecord", result.contains("SraRecord"));
    }

    @Test
    public void testToStringWithPartialFields() {
        record.setAccession("SRR123456");
        record.setPlatform("ILLUMINA");

        String result = record.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain accession", result.contains("SRR123456"));
        assertTrue("toString should contain platform", result.contains("ILLUMINA"));
    }

    // ==================== Integration/Complex Scenario Tests ====================

    @Test
    public void testFullyPopulatedRecord() {
        SraRecord fullRecord = createFullyPopulatedRecord();

        assertEquals("Accession should match", "SRR123456", fullRecord.getAccession());
        assertEquals("Title should match", "Test RNA-Seq", fullRecord.getTitle());
        assertEquals("Organism should match", "Homo sapiens", fullRecord.getOrganism());
        assertEquals("Study should match", "SRP123456", fullRecord.getStudy());
        assertEquals("Sample should match", "SRS123456", fullRecord.getSample());
        assertEquals("Experiment should match", "SRX123456", fullRecord.getExperiment());
        assertEquals("Run should match", "SRR123456", fullRecord.getRun());
        assertEquals("Platform should match", "ILLUMINA", fullRecord.getPlatform());
        assertEquals("Library strategy should match", "RNA-Seq", fullRecord.getLibraryStrategy());
        assertEquals("Library source should match", "TRANSCRIPTOMIC", fullRecord.getLibrarySource());
        assertEquals("Library selection should match", "cDNA", fullRecord.getLibrarySelection());
        assertEquals("Library layout should match", "PAIRED", fullRecord.getLibraryLayout());
        assertEquals("Center name should match", "BROAD", fullRecord.getCenterName());
        assertEquals("BioProject should match", "PRJNA123456", fullRecord.getBioProject());
        assertEquals("BioSample should match", "SAMN123456", fullRecord.getBioSample());
        assertEquals("Total spots should match", 1000000L, fullRecord.getTotalSpots());
        assertEquals("Total bases should match", 50000000L, fullRecord.getTotalBases());
        assertNotNull("Submission date should not be null", fullRecord.getSubmissionDate());
        assertNotNull("Publication date should not be null", fullRecord.getPublicationDate());
        assertEquals("Should have 2 attributes", 2, fullRecord.getAttributes().size());
        assertTrue("Should be paired end", fullRecord.isPairedEnd());
    }

    @Test
    public void testMinimallyPopulatedRecord() {
        SraRecord minimalRecord = new SraRecord("SRR123456");

        assertEquals("Accession should match", "SRR123456", minimalRecord.getAccession());
        assertNull("Title should be null", minimalRecord.getTitle());
        assertNull("Organism should be null", minimalRecord.getOrganism());
        assertEquals("Total spots should be zero", 0L, minimalRecord.getTotalSpots());
        assertEquals("Total bases should be zero", 0L, minimalRecord.getTotalBases());
        assertFalse("Should not be paired end", minimalRecord.isPairedEnd());
        assertNotNull("Attributes should be initialized", minimalRecord.getAttributes());
        assertTrue("Attributes should be empty", minimalRecord.getAttributes().isEmpty());
    }

    @Test
    public void testRecordWithVeryLongStrings() {
        String longString = createLongString(10000);
        record.setTitle(longString);
        assertEquals("Long title should be preserved", longString, record.getTitle());
        assertEquals("Long title length should match", 10000, record.getTitle().length());
    }

    @Test
    public void testRecordWithSpecialCharactersInAllFields() {
        String specialChars = "Test !@#$%^&*()_+-=[]{}|;':\",./<>?";
        record.setTitle(specialChars);
        record.setOrganism(specialChars);
        record.setCenterName(specialChars);

        assertEquals("Title with special chars should be preserved", specialChars, record.getTitle());
        assertEquals("Organism with special chars should be preserved", specialChars, record.getOrganism());
        assertEquals("Center name with special chars should be preserved", specialChars, record.getCenterName());
    }

    @Test
    public void testRecordWithUnicodeCharacters() {
        String unicode = "Test \u00E9\u00F1\u4E2D\u6587 Unicode";
        record.setTitle(unicode);
        record.setOrganism(unicode);

        assertEquals("Title with unicode should be preserved", unicode, record.getTitle());
        assertEquals("Organism with unicode should be preserved", unicode, record.getOrganism());
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a fully populated SraRecord for testing
     */
    private SraRecord createFullyPopulatedRecord() {
        SraRecord fullRecord = new SraRecord("SRR123456");
        fullRecord.setTitle("Test RNA-Seq");
        fullRecord.setOrganism("Homo sapiens");
        fullRecord.setStudy("SRP123456");
        fullRecord.setSample("SRS123456");
        fullRecord.setExperiment("SRX123456");
        fullRecord.setRun("SRR123456");
        fullRecord.setPlatform("ILLUMINA");
        fullRecord.setLibraryStrategy("RNA-Seq");
        fullRecord.setLibrarySource("TRANSCRIPTOMIC");
        fullRecord.setLibrarySelection("cDNA");
        fullRecord.setLibraryLayout("PAIRED");
        fullRecord.setCenterName("BROAD");
        fullRecord.setBioProject("PRJNA123456");
        fullRecord.setBioSample("SAMN123456");
        fullRecord.setTotalSpots(1000000L);
        fullRecord.setTotalBases(50000000L);
        fullRecord.setSubmissionDate(new Date());
        fullRecord.setPublicationDate(new Date());
        fullRecord.addAttribute("key1", "value1");
        fullRecord.addAttribute("key2", "value2");
        return fullRecord;
    }

    /**
     * Creates a string of specified length for testing
     */
    private String createLongString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append('A');
        }
        return sb.toString();
    }
}
