package com.biomatters.plugins.ncbisra.model;

import com.biomatters.geneious.publicapi.documents.DocumentField;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for SraDocument model class
 * Tests cover constructors, SraRecord integration, serialization properties,
 * field retrieval, document properties, and edge cases
 */
public class SraDocumentTest {

    private SraDocument document;
    private SraRecord testRecord;
    private Date testDate;

    @Before
    public void setUp() {
        testDate = new Date();
        testRecord = createTestSraRecord();
        document = new SraDocument(testRecord);
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testDefaultConstructor() {
        SraDocument doc = new SraDocument();
        assertNotNull("Default constructor should create instance", doc);
        assertEquals("Default name should be 'Unknown SRA'", "Unknown SRA", doc.getName());
        assertNotNull("Description should not be null", doc.getDescription());
        assertTrue("Description should contain 'SRA Dataset'", doc.getDescription().contains("SRA Dataset"));
        assertFalse("Should not have SRA record", doc.hasSraRecord());
    }

    @Test
    public void testConstructorWithSraRecord() {
        SraRecord record = new SraRecord("SRR123456");
        record.setTitle("Test Title");
        record.setOrganism("Homo sapiens");

        SraDocument doc = new SraDocument(record);
        assertNotNull("Constructor with record should create instance", doc);
        assertEquals("Name should match accession", "SRR123456", doc.getName());
        assertTrue("Description should contain title", doc.getDescription().contains("Test Title"));
        assertTrue("Description should contain organism", doc.getDescription().contains("Homo sapiens"));
        assertTrue("Should have SRA record", doc.hasSraRecord());
    }

    @Test
    public void testConstructorWithNullAccession() {
        SraRecord record = new SraRecord();
        record.setTitle("Test");

        SraDocument doc = new SraDocument(record);
        assertEquals("Name should be 'Unknown SRA' for null accession", "Unknown SRA", doc.getName());
    }

    // ==================== Document Basic Properties Tests ====================

    @Test
    public void testGetDocumentType() {
        assertEquals("Document type should be 'SRA Dataset'", "SRA Dataset", document.getDocumentType());
    }

    @Test
    public void testGetName() {
        assertEquals("Document name should match accession", "SRR123456", document.getName());
    }

    @Test
    public void testGetDescription() {
        String description = document.getDescription();
        assertNotNull("Description should not be null", description);
        assertTrue("Description should contain accession", description.contains("SRR123456"));
        assertTrue("Description should contain title", description.contains("Test RNA-Seq"));
        assertTrue("Description should contain organism", description.contains("Homo sapiens"));
        assertTrue("Description should contain platform", description.contains("ILLUMINA"));
        assertTrue("Description should contain NCBI SRA Dataset", description.contains("NCBI SRA Dataset"));
    }

    @Test
    public void testGetSequence() {
        String sequence = document.getCharSequence().toString();
        assertNotNull("Sequence should not be null", sequence);
        assertFalse("Sequence should not be empty", sequence.isEmpty());
        // Placeholder sequence should be all N's
        assertTrue("Sequence should contain only N characters", sequence.matches("N+"));
    }

    // ==================== SraRecord Integration Tests ====================

    @Test
    public void testGetSraRecord() {
        SraRecord record = document.getSraRecord();
        assertNotNull("SraRecord should not be null", record);
        assertEquals("Accession should match", "SRR123456", record.getAccession());
        assertEquals("Title should match", "Test RNA-Seq", record.getTitle());
        assertEquals("Organism should match", "Homo sapiens", record.getOrganism());
    }

    @Test
    public void testHasSraRecord() {
        assertTrue("Should have SRA record", document.hasSraRecord());
    }

    @Test
    public void testHasSraRecordWithDefaultConstructor() {
        SraDocument doc = new SraDocument();
        assertFalse("Default document should not have SRA record", doc.hasSraRecord());
    }

    @Test
    public void testGetSraRecordAfterSerialization() {
        // Simulate transient field being lost (as would happen after serialization)
        // We can't directly set the transient field to null, but we can test reconstruction
        SraDocument doc = new SraDocument(testRecord);
        SraRecord retrievedRecord = doc.getSraRecord();

        assertNotNull("Should be able to retrieve record", retrievedRecord);
        assertEquals("Accession should match", "SRR123456", retrievedRecord.getAccession());
    }

    @Test
    public void testGetSraAccession() {
        String accession = document.getSraAccession();
        assertEquals("SRA accession should match", "SRR123456", accession);
    }

    @Test
    public void testGetSraAccessionWithDefaultConstructor() {
        SraDocument doc = new SraDocument();
        String accession = doc.getSraAccession();
        assertNull("Default document should return null for accession", accession);
    }

    @Test
    public void testGetSraAccessionFromProperties() {
        // Create document and verify accession can be retrieved from stored properties
        SraDocument doc = new SraDocument(testRecord);
        String accession = doc.getSraAccession();
        assertEquals("Accession should be retrievable from properties", "SRR123456", accession);
    }

    // ==================== Document Field Tests ====================

    @Test
    public void testGetDisplayableFields() {
        List<DocumentField> fields = document.getDisplayableFields();
        assertNotNull("Displayable fields should not be null", fields);
        assertFalse("Displayable fields should not be empty", fields.isEmpty());

        // Verify SRA-specific fields are included
        boolean hasAccessionField = false;
        boolean hasOrganismField = false;
        boolean hasPlatformField = false;

        for (DocumentField field : fields) {
            String code = field.getCode();
            if ("sra.accession".equals(code)) hasAccessionField = true;
            if ("sra.organism".equals(code)) hasOrganismField = true;
            if ("sra.platform".equals(code)) hasPlatformField = true;
        }

        assertTrue("Should include SRA accession field", hasAccessionField);
        assertTrue("Should include SRA organism field", hasOrganismField);
        assertTrue("Should include SRA platform field", hasPlatformField);
    }

    @Test
    public void testStaticFieldDefinitions() {
        assertNotNull("FIELD_SRA_ACCESSION should not be null", SraDocument.FIELD_SRA_ACCESSION);
        assertNotNull("FIELD_SRA_ORGANISM should not be null", SraDocument.FIELD_SRA_ORGANISM);
        assertNotNull("FIELD_SRA_PLATFORM should not be null", SraDocument.FIELD_SRA_PLATFORM);
        assertNotNull("FIELD_SRA_LIBRARY_STRATEGY should not be null", SraDocument.FIELD_SRA_LIBRARY_STRATEGY);
        assertNotNull("FIELD_SRA_LIBRARY_LAYOUT should not be null", SraDocument.FIELD_SRA_LIBRARY_LAYOUT);
        assertNotNull("FIELD_SRA_TITLE should not be null", SraDocument.FIELD_SRA_TITLE);
        assertNotNull("FIELD_SRA_STUDY should not be null", SraDocument.FIELD_SRA_STUDY);
        assertNotNull("FIELD_SRA_BIOPROJECT should not be null", SraDocument.FIELD_SRA_BIOPROJECT);
        assertNotNull("FIELD_SRA_BIOSAMPLE should not be null", SraDocument.FIELD_SRA_BIOSAMPLE);
        assertNotNull("FIELD_SRA_TOTAL_SPOTS should not be null", SraDocument.FIELD_SRA_TOTAL_SPOTS);
        assertNotNull("FIELD_SRA_TOTAL_BASES should not be null", SraDocument.FIELD_SRA_TOTAL_BASES);
    }

    @Test
    public void testSraFieldsList() {
        List<DocumentField> sraFields = SraDocument.SRA_FIELDS;
        assertNotNull("SRA_FIELDS list should not be null", sraFields);
        assertEquals("Should have 10 SRA fields", 10, sraFields.size());

        assertTrue("Should contain accession field", sraFields.contains(SraDocument.FIELD_SRA_ACCESSION));
        assertTrue("Should contain organism field", sraFields.contains(SraDocument.FIELD_SRA_ORGANISM));
        assertTrue("Should contain platform field", sraFields.contains(SraDocument.FIELD_SRA_PLATFORM));
    }

    // ==================== Field Value Retrieval Tests ====================

    @Test
    public void testGetFieldValueAccession() {
        Object value = document.getFieldValue("sra.accession");
        assertNotNull("Accession field value should not be null", value);
        assertEquals("Accession should match", "SRR123456", value.toString());
    }

    @Test
    public void testGetFieldValueOrganism() {
        Object value = document.getFieldValue("sra.organism");
        assertNotNull("Organism field value should not be null", value);
        assertEquals("Organism should match", "Homo sapiens", value.toString());
    }

    @Test
    public void testGetFieldValuePlatform() {
        Object value = document.getFieldValue("sra.platform");
        assertNotNull("Platform field value should not be null", value);
        assertEquals("Platform should match", "ILLUMINA", value.toString());
    }

    @Test
    public void testGetFieldValueLibraryStrategy() {
        Object value = document.getFieldValue("sra.libraryStrategy");
        assertNotNull("Library strategy field value should not be null", value);
        assertEquals("Library strategy should match", "RNA-Seq", value.toString());
    }

    @Test
    public void testGetFieldValueLibraryLayout() {
        Object value = document.getFieldValue("sra.libraryLayout");
        assertNotNull("Library layout field value should not be null", value);
        assertEquals("Library layout should match", "PAIRED", value.toString());
    }

    @Test
    public void testGetFieldValueTitle() {
        Object value = document.getFieldValue("sra.title");
        assertNotNull("Title field value should not be null", value);
        assertEquals("Title should match", "Test RNA-Seq", value.toString());
    }

    @Test
    public void testGetFieldValueStudy() {
        Object value = document.getFieldValue("sra.study");
        assertNotNull("Study field value should not be null", value);
        assertEquals("Study should match", "SRP123456", value.toString());
    }

    @Test
    public void testGetFieldValueBioProject() {
        Object value = document.getFieldValue("sra.bioproject");
        assertNotNull("BioProject field value should not be null", value);
        assertEquals("BioProject should match", "PRJNA123456", value.toString());
    }

    @Test
    public void testGetFieldValueBioSample() {
        Object value = document.getFieldValue("sra.biosample");
        assertNotNull("BioSample field value should not be null", value);
        assertEquals("BioSample should match", "SAMN123456", value.toString());
    }

    @Test
    public void testGetFieldValueTotalSpots() {
        Object value = document.getFieldValue("sra.totalSpots");
        assertNotNull("Total spots field value should not be null", value);
        assertEquals("Total spots should match", 1000000, ((Number)value).intValue());
    }

    @Test
    public void testGetFieldValueTotalBases() {
        Object value = document.getFieldValue("sra.totalBases");
        assertNotNull("Total bases field value should not be null", value);
        assertEquals("Total bases should match", 50000000L, ((Number)value).longValue());
    }

    @Test
    public void testGetFieldValueNonExistent() {
        Object value = document.getFieldValue("nonexistent.field");
        assertNull("Non-existent field should return null", value);
    }

    @Test
    public void testGetFieldValueWithNullField() {
        SraRecord record = new SraRecord("SRR123456");
        // Don't set organism
        SraDocument doc = new SraDocument(record);

        Object value = doc.getFieldValue("sra.organism");
        assertNull("Null field should return null", value);
    }

    @Test
    public void testGetFieldValueWithZeroValues() {
        SraRecord record = new SraRecord("SRR123456");
        record.setTotalSpots(0L);
        record.setTotalBases(0L);
        SraDocument doc = new SraDocument(record);

        Object spots = doc.getFieldValue("sra.totalSpots");
        Object bases = doc.getFieldValue("sra.totalBases");

        assertNull("Zero spots should return null", spots);
        assertNull("Zero bases should return null", bases);
    }

    // ==================== Serialization Property Tests ====================

    @Test
    public void testDocumentPropertiesAreStored() {
        // Verify that properties are stored in the document
        SraDocument doc = new SraDocument(testRecord);

        // These should be stored as document properties
        Object accession = doc.getFieldValue("sra.accession");
        Object organism = doc.getFieldValue("sra.organism");
        Object platform = doc.getFieldValue("sra.platform");

        assertNotNull("Accession property should be stored", accession);
        assertNotNull("Organism property should be stored", organism);
        assertNotNull("Platform property should be stored", platform);
    }

    @Test
    public void testRecordReconstructionFromProperties() {
        // Create a document with minimal data
        SraRecord record = new SraRecord("SRR999999");
        record.setOrganism("Test Organism");
        record.setPlatform("TEST_PLATFORM");

        SraDocument doc = new SraDocument(record);

        // Verify record can be reconstructed
        SraRecord reconstructed = doc.getSraRecord();
        assertNotNull("Record should be reconstructed", reconstructed);
        assertEquals("Accession should match", "SRR999999", reconstructed.getAccession());
        assertEquals("Organism should match", "Test Organism", reconstructed.getOrganism());
        assertEquals("Platform should match", "TEST_PLATFORM", reconstructed.getPlatform());
    }

    @Test
    public void testReconstructionWithPartialData() {
        SraRecord record = new SraRecord("SRR888888");
        // Only set a few fields
        record.setOrganism("Partial Organism");

        SraDocument doc = new SraDocument(record);
        SraRecord reconstructed = doc.getSraRecord();

        assertNotNull("Record should be reconstructed with partial data", reconstructed);
        assertEquals("Accession should match", "SRR888888", reconstructed.getAccession());
        assertEquals("Organism should match", "Partial Organism", reconstructed.getOrganism());
        assertNull("Unset fields should be null", reconstructed.getPlatform());
    }

    // ==================== Description Generation Tests ====================

    @Test
    public void testDescriptionWithFullMetadata() {
        SraRecord record = createFullSraRecord();
        SraDocument doc = new SraDocument(record);

        String description = doc.getDescription();
        assertTrue("Description should contain title", description.contains("Full Test Title"));
        assertTrue("Description should contain accession", description.contains("SRR999999"));
        assertTrue("Description should contain organism", description.contains("Full Organism"));
        assertTrue("Description should contain platform", description.contains("PACBIO"));
        assertTrue("Description should contain library strategy", description.contains("WGS"));
        assertTrue("Description should contain library layout", description.contains("SINGLE"));
        assertTrue("Description should contain study", description.contains("SRP999999"));
        assertTrue("Description should contain bioproject", description.contains("PRJNA999999"));
        assertTrue("Description should contain biosample", description.contains("SAMN999999"));
        assertTrue("Description should contain total spots", description.contains("2,000,000"));
        assertTrue("Description should contain total bases", description.contains("100,000,000"));
    }

    @Test
    public void testDescriptionWithMinimalMetadata() {
        SraRecord record = new SraRecord("SRR777777");
        SraDocument doc = new SraDocument(record);

        String description = doc.getDescription();
        assertTrue("Description should contain accession", description.contains("SRR777777"));
        assertTrue("Description should contain NCBI SRA Dataset", description.contains("NCBI SRA Dataset"));
        assertTrue("Description should contain download message",
                description.contains("Use 'Download FASTQ' to obtain the actual sequence data"));
    }

    @Test
    public void testDescriptionFormattingWithLargeNumbers() {
        SraRecord record = new SraRecord("SRR666666");
        record.setTotalSpots(1234567890L);
        record.setTotalBases(9876543210L);

        SraDocument doc = new SraDocument(record);
        String description = doc.getDescription();

        // Should contain formatted numbers with commas (locale-dependent)
        assertTrue("Description should contain formatted total spots",
                description.contains("Total Spots:"));
        assertTrue("Description should contain formatted total bases",
                description.contains("Total Bases:"));
    }

    @Test
    public void testDescriptionWithNullValues() {
        SraRecord record = new SraRecord("SRR555555");
        // Leave all other fields null

        SraDocument doc = new SraDocument(record);
        String description = doc.getDescription();

        assertNotNull("Description should not be null", description);
        assertTrue("Description should contain accession", description.contains("SRR555555"));
        assertFalse("Description should not contain null strings", description.contains("null"));
    }

    // ==================== Edge Cases and Boundary Tests ====================

    @Test
    public void testDocumentWithEmptyStrings() {
        SraRecord record = new SraRecord("");
        record.setTitle("");
        record.setOrganism("");

        SraDocument doc = new SraDocument(record);
        assertEquals("Document should handle empty accession", "Unknown SRA", doc.getName());
    }

    @Test
    public void testDocumentWithVeryLongStrings() {
        SraRecord record = new SraRecord("SRR123456");
        String longString = createLongString(10000);
        record.setTitle(longString);

        SraDocument doc = new SraDocument(record);
        String description = doc.getDescription();

        assertTrue("Description should contain long title", description.contains(longString));
    }

    @Test
    public void testDocumentWithSpecialCharacters() {
        SraRecord record = new SraRecord("SRR123456");
        String specialChars = "Test <>&\"' \n\t\r Special Characters";
        record.setTitle(specialChars);
        record.setOrganism(specialChars);

        SraDocument doc = new SraDocument(record);
        String description = doc.getDescription();

        assertTrue("Description should preserve special characters",
                description.contains(specialChars));
    }

    @Test
    public void testDocumentWithUnicodeCharacters() {
        SraRecord record = new SraRecord("SRR123456");
        String unicode = "Test \u00E9\u00F1\u4E2D\u6587 Unicode";
        record.setTitle(unicode);

        SraDocument doc = new SraDocument(record);
        String description = doc.getDescription();

        assertTrue("Description should preserve unicode", description.contains(unicode));
    }

    @Test
    public void testDocumentWithMaxLongValues() {
        SraRecord record = new SraRecord("SRR123456");
        record.setTotalSpots(Long.MAX_VALUE);
        record.setTotalBases(Long.MAX_VALUE);

        SraDocument doc = new SraDocument(record);
        Object spots = doc.getFieldValue("sra.totalSpots");
        Object bases = doc.getFieldValue("sra.totalBases");

        assertNotNull("Max spots should be stored", spots);
        assertNotNull("Max bases should be stored", bases);
    }

    @Test
    public void testMultipleDocumentsIndependence() {
        SraRecord record1 = new SraRecord("SRR111111");
        record1.setTitle("First Record");

        SraRecord record2 = new SraRecord("SRR222222");
        record2.setTitle("Second Record");

        SraDocument doc1 = new SraDocument(record1);
        SraDocument doc2 = new SraDocument(record2);

        assertEquals("First document should have first accession", "SRR111111", doc1.getSraAccession());
        assertEquals("Second document should have second accession", "SRR222222", doc2.getSraAccession());

        SraRecord retrieved1 = doc1.getSraRecord();
        SraRecord retrieved2 = doc2.getSraRecord();

        assertEquals("First document title should not affect second", "First Record", retrieved1.getTitle());
        assertEquals("Second document title should not affect first", "Second Record", retrieved2.getTitle());
    }

    // ==================== Placeholder Sequence Tests ====================

    @Test
    public void testPlaceholderSequenceLength() {
        String sequence = document.getCharSequence().toString();
        assertTrue("Placeholder sequence should be at least 10 characters", sequence.length() >= 10);
    }

    @Test
    public void testPlaceholderSequenceContent() {
        String sequence = document.getCharSequence().toString();
        for (char c : sequence.toCharArray()) {
            assertEquals("All characters should be N", 'N', c);
        }
    }

    @Test
    public void testPlaceholderSequenceConsistency() {
        SraRecord record = new SraRecord("SRR123456");
        SraDocument doc1 = new SraDocument(record);
        SraDocument doc2 = new SraDocument(record);

        String seq1 = doc1.getCharSequence().toString();
        String seq2 = doc2.getCharSequence().toString();

        assertEquals("Placeholder sequences should be consistent", seq1.length(), seq2.length());
        assertEquals("Placeholder sequences should be identical", seq1, seq2);
    }

    // ==================== Integration Tests ====================

    @Test
    public void testCompleteWorkflowWithFullyPopulatedRecord() {
        SraRecord fullRecord = createFullSraRecord();
        SraDocument doc = new SraDocument(fullRecord);

        // Verify document properties
        assertEquals("Name should match accession", "SRR999999", doc.getName());
        assertEquals("Document type should be correct", "SRA Dataset", doc.getDocumentType());
        assertTrue("Should have SRA record", doc.hasSraRecord());

        // Verify field values
        assertEquals("SRR999999", doc.getFieldValue("sra.accession"));
        assertEquals("Full Organism", doc.getFieldValue("sra.organism"));
        assertEquals("PACBIO", doc.getFieldValue("sra.platform"));

        // Verify displayable fields
        List<DocumentField> fields = doc.getDisplayableFields();
        assertTrue("Should have multiple displayable fields", fields.size() > 10);

        // Verify record retrieval
        SraRecord retrieved = doc.getSraRecord();
        assertEquals("Retrieved record should match original", fullRecord.getAccession(),
                retrieved.getAccession());
    }

    @Test
    public void testSerializationSimulation() {
        // Create document with full record
        SraRecord original = createFullSraRecord();
        SraDocument originalDoc = new SraDocument(original);

        // Store key properties (simulating what would be serialized)
        String storedAccession = (String) originalDoc.getFieldValue("sra.accession");
        String storedOrganism = (String) originalDoc.getFieldValue("sra.organism");

        // Verify properties were stored
        assertNotNull("Accession should be stored", storedAccession);
        assertNotNull("Organism should be stored", storedOrganism);
        assertEquals("Stored accession should match", "SRR999999", storedAccession);
        assertEquals("Stored organism should match", "Full Organism", storedOrganism);

        // Verify record can be reconstructed
        SraRecord reconstructed = originalDoc.getSraRecord();
        assertEquals("Reconstructed accession should match", "SRR999999",
                reconstructed.getAccession());
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a test SraRecord with common test data
     */
    private SraRecord createTestSraRecord() {
        SraRecord record = new SraRecord("SRR123456");
        record.setTitle("Test RNA-Seq");
        record.setOrganism("Homo sapiens");
        record.setStudy("SRP123456");
        record.setSample("SRS123456");
        record.setExperiment("SRX123456");
        record.setRun("SRR123456");
        record.setPlatform("ILLUMINA");
        record.setLibraryStrategy("RNA-Seq");
        record.setLibrarySource("TRANSCRIPTOMIC");
        record.setLibrarySelection("cDNA");
        record.setLibraryLayout("PAIRED");
        record.setCenterName("BROAD");
        record.setBioProject("PRJNA123456");
        record.setBioSample("SAMN123456");
        record.setTotalSpots(1000000L);
        record.setTotalBases(50000000L);
        record.setSubmissionDate(testDate);
        record.setPublicationDate(testDate);
        return record;
    }

    /**
     * Creates a fully populated SraRecord with different test data
     */
    private SraRecord createFullSraRecord() {
        SraRecord record = new SraRecord("SRR999999");
        record.setTitle("Full Test Title");
        record.setOrganism("Full Organism");
        record.setStudy("SRP999999");
        record.setSample("SRS999999");
        record.setExperiment("SRX999999");
        record.setRun("SRR999999");
        record.setPlatform("PACBIO");
        record.setLibraryStrategy("WGS");
        record.setLibrarySource("GENOMIC");
        record.setLibrarySelection("RANDOM");
        record.setLibraryLayout("SINGLE");
        record.setCenterName("TEST_CENTER");
        record.setBioProject("PRJNA999999");
        record.setBioSample("SAMN999999");
        record.setTotalSpots(2000000L);
        record.setTotalBases(100000000L);
        record.setSubmissionDate(new Date());
        record.setPublicationDate(new Date());
        return record;
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
