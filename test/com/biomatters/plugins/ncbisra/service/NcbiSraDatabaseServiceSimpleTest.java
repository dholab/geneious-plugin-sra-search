package com.biomatters.plugins.ncbisra.service;

import com.biomatters.geneious.publicapi.databaseservice.*;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.plugins.ncbisra.api.NcbiEUtilsClient;
import com.biomatters.plugins.ncbisra.model.SraRecord;
import com.biomatters.plugins.ncbisra.model.SraSearchResult;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for NcbiSraDatabaseServiceSimple
 *
 * Test Coverage:
 * - Metadata methods (name, description, help, unique ID, icons)
 * - Search field configuration and validation (8 fields)
 * - Query building for all search field types
 * - BasicSearchQuery handling with various inputs
 * - AdvancedSearchQueryTerm handling for all field types
 * - CompoundSearchQuery with AND/OR operators
 * - retrieve() method with mocked NcbiEUtilsClient
 * - Document creation from SraRecord
 * - Error handling and edge cases
 * - Thread interruption handling
 * - Large result set processing
 *
 * Expected Coverage: >85% of NcbiSraDatabaseServiceSimple
 */
@RunWith(MockitoJUnitRunner.class)
public class NcbiSraDatabaseServiceSimpleTest {

    private NcbiSraDatabaseServiceSimple service;

    @Mock
    private NcbiEUtilsClient mockClient;

    @Mock
    private RetrieveCallback mockCallback;

    private static final String TEST_ACCESSION = "SRR123456";
    private static final String TEST_BIOPROJECT = "PRJNA123456";
    private static final String TEST_BIOSAMPLE = "SAMN12345678";
    private static final String TEST_ORGANISM = "Homo sapiens";

    @Before
    public void setUp() {
        service = new NcbiSraDatabaseServiceSimple();
        // Use reflection to inject the mock client
        try {
            java.lang.reflect.Field clientField = NcbiSraDatabaseServiceSimple.class.getDeclaredField("ncbiClient");
            clientField.setAccessible(true);
            clientField.set(service, mockClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock client", e);
        }
    }

    @After
    public void tearDown() {
        service = null;
        mockClient = null;
        mockCallback = null;
    }

    // ========================================
    // Metadata Method Tests
    // ========================================

    @Test
    public void testGetName() {
        assertEquals("Service name should be NCBI SRA", "NCBI SRA", service.getName());
    }

    @Test
    public void testGetDescription() {
        String description = service.getDescription();
        assertNotNull("Description should not be null", description);
        assertTrue("Description should mention NCBI", description.contains("NCBI"));
        assertTrue("Description should mention SRA", description.contains("Sequence Read Archive"));
    }

    @Test
    public void testGetHelp() {
        String help = service.getHelp();
        assertNotNull("Help text should not be null", help);
        assertTrue("Help should mention search", help.contains("Search") || help.contains("search"));
    }

    @Test
    public void testGetUniqueID() {
        String uniqueId = service.getUniqueID();
        assertNotNull("Unique ID should not be null", uniqueId);
        assertEquals("Unique ID should match expected value", "ncbi_sra_service", uniqueId);
    }

    @Test
    public void testGetIcons() {
        assertNotNull("Icons should not be null", service.getIcons());
    }

    // ========================================
    // Availability Tests
    // ========================================

    @Test
    public void testIsAvailable() {
        assertTrue("Service should be available when client is initialized", service.isAvailable());
    }

    @Test
    public void testGetUnavailableReason_WhenAvailable() {
        assertNull("Unavailable reason should be null when service is available",
                   service.getUnavailableReason());
    }

    // ========================================
    // Search Fields Configuration Tests
    // ========================================

    @Test
    public void testGetSearchFields_NotNull() {
        QueryField[] fields = service.getSearchFields();
        assertNotNull("Search fields should not be null", fields);
    }

    @Test
    public void testGetSearchFields_HasExpectedCount() {
        QueryField[] fields = service.getSearchFields();
        assertEquals("Should have 8 search fields", 8, fields.length);
    }

    @Test
    public void testGetSearchFields_BasicSearchField() {
        QueryField[] fields = service.getSearchFields();
        QueryField basicSearch = findFieldByCode(fields, "search");

        assertNotNull("Basic search field should exist", basicSearch);
        assertEquals("Basic search should have CONTAINS condition",
                    1, basicSearch.conditions.length);
        assertEquals("Basic search should use CONTAINS condition",
                    Condition.CONTAINS, basicSearch.conditions[0]);
    }

    @Test
    public void testGetSearchFields_AccessionField() {
        QueryField[] fields = service.getSearchFields();
        QueryField accessionField = findFieldByCode(fields, "accession");

        assertNotNull("Accession field should exist", accessionField);
        assertEquals("Accession field should have 2 conditions",
                    2, accessionField.conditions.length);
        assertTrue("Accession should support EQUAL", hasCondition(accessionField, Condition.EQUAL));
        assertTrue("Accession should support CONTAINS", hasCondition(accessionField, Condition.CONTAINS));
    }

    @Test
    public void testGetSearchFields_BioprojectField() {
        QueryField[] fields = service.getSearchFields();
        QueryField field = findFieldByCode(fields, "bioproject");

        assertNotNull("BioProject field should exist", field);
        assertTrue("BioProject should support EQUAL", hasCondition(field, Condition.EQUAL));
        assertTrue("BioProject should support CONTAINS", hasCondition(field, Condition.CONTAINS));
    }

    @Test
    public void testGetSearchFields_BiosampleField() {
        QueryField[] fields = service.getSearchFields();
        QueryField field = findFieldByCode(fields, "biosample");

        assertNotNull("BioSample field should exist", field);
        assertTrue("BioSample should support EQUAL", hasCondition(field, Condition.EQUAL));
        assertTrue("BioSample should support CONTAINS", hasCondition(field, Condition.CONTAINS));
    }

    @Test
    public void testGetSearchFields_OrganismField() {
        QueryField[] fields = service.getSearchFields();
        QueryField field = findFieldByCode(fields, "organism");

        assertNotNull("Organism field should exist", field);
        assertTrue("Organism should support CONTAINS", hasCondition(field, Condition.CONTAINS));
    }

    @Test
    public void testGetSearchFields_LibraryStrategyField() {
        QueryField[] fields = service.getSearchFields();
        QueryField field = findFieldByCode(fields, "library_strategy");

        assertNotNull("Library strategy field should exist", field);
        assertTrue("Library strategy should support CONTAINS", hasCondition(field, Condition.CONTAINS));
        assertTrue("Library strategy should support EQUAL", hasCondition(field, Condition.EQUAL));
    }

    @Test
    public void testGetSearchFields_PlatformField() {
        QueryField[] fields = service.getSearchFields();
        QueryField field = findFieldByCode(fields, "platform");

        assertNotNull("Platform field should exist", field);
        assertTrue("Platform should support CONTAINS", hasCondition(field, Condition.CONTAINS));
        assertTrue("Platform should support EQUAL", hasCondition(field, Condition.EQUAL));
    }

    @Test
    public void testGetSearchFields_LibrarySourceField() {
        QueryField[] fields = service.getSearchFields();
        QueryField field = findFieldByCode(fields, "library_source");

        assertNotNull("Library source field should exist", field);
        assertTrue("Library source should support CONTAINS", hasCondition(field, Condition.CONTAINS));
        assertTrue("Library source should support EQUAL", hasCondition(field, Condition.EQUAL));
    }

    // ========================================
    // BasicSearchQuery Tests
    // ========================================

    @Test
    public void testRetrieve_BasicSearchQuery() throws Exception {
        String searchText = "RNA-Seq";
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn(searchText);

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        List<SraRecord> records = Arrays.asList(record);
        SraSearchResult result = new SraSearchResult(records, 1, 0, 10);

        when(mockClient.search(eq(searchText), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(searchText), eq(0), eq(10000));
        verify(mockCallback, times(1)).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_BasicSearchQuery_EmptySearchText() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("");

        service.retrieve(query, mockCallback, new URN[0]);

        // Should not call client for empty search
        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
        verify(mockCallback, never()).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_BasicSearchQuery_NullSearchText() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn(null);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
        verify(mockCallback, never()).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_BasicSearchQuery_MultipleResults() throws Exception {
        String searchText = "Illumina";
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn(searchText);

        List<SraRecord> records = Arrays.asList(
            createTestSraRecord("SRR111111"),
            createTestSraRecord("SRR222222"),
            createTestSraRecord("SRR333333")
        );
        SraSearchResult result = new SraSearchResult(records, 3, 0, 10);

        when(mockClient.search(eq(searchText), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(searchText), eq(0), eq(10000));
        verify(mockCallback, times(3)).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    // ========================================
    // AdvancedSearchQueryTerm Tests
    // ========================================

    @Test
    public void testRetrieve_AdvancedSearchQuery_Accession() throws Exception {
        DocumentField field = DocumentField.createStringField("accession", "Accession", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{TEST_ACCESSION});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(TEST_ACCESSION + "[Accession]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(TEST_ACCESSION + "[Accession]"), eq(0), eq(10000));
        verify(mockCallback, times(1)).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_BioProject() throws Exception {
        DocumentField field = DocumentField.createStringField("bioproject", "BioProject", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{TEST_BIOPROJECT});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setBioProject(TEST_BIOPROJECT);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(TEST_BIOPROJECT + "[Bioproject]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(TEST_BIOPROJECT + "[Bioproject]"), eq(0), eq(10000));
        verify(mockCallback, times(1)).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_BioSample() throws Exception {
        DocumentField field = DocumentField.createStringField("biosample", "BioSample", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{TEST_BIOSAMPLE});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setBioSample(TEST_BIOSAMPLE);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(TEST_BIOSAMPLE + "[Biosample]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(TEST_BIOSAMPLE + "[Biosample]"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_Organism() throws Exception {
        DocumentField field = DocumentField.createStringField("organism", "Organism", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{TEST_ORGANISM});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setOrganism(TEST_ORGANISM);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(TEST_ORGANISM + "[Organism]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(TEST_ORGANISM + "[Organism]"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_LibraryStrategy() throws Exception {
        String strategy = "RNA-Seq";
        DocumentField field = DocumentField.createStringField("library_strategy", "Library Strategy", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{strategy});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setLibraryStrategy(strategy);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(strategy + "[Strategy]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(strategy + "[Strategy]"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_Platform() throws Exception {
        String platform = "Illumina";
        DocumentField field = DocumentField.createStringField("platform", "Platform", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{platform});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setPlatform(platform);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(platform + "[Platform]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(platform + "[Platform]"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_LibrarySource() throws Exception {
        String source = "TRANSCRIPTOMIC";
        DocumentField field = DocumentField.createStringField("library_source", "Library Source", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{source});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setLibrarySource(source);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(source + "[Source]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(source + "[Source]"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_NullValues() throws Exception {
        DocumentField field = DocumentField.createStringField("accession", "Accession", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(null);

        service.retrieve(query, mockCallback, new URN[0]);

        // Should not call client for null values
        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_EmptyValues() throws Exception {
        DocumentField field = DocumentField.createStringField("accession", "Accession", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[0]);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_EmptyString() throws Exception {
        DocumentField field = DocumentField.createStringField("accession", "Accession", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{""});

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
    }

    @Test
    public void testRetrieve_AdvancedSearchQuery_WhitespaceString() throws Exception {
        DocumentField field = DocumentField.createStringField("accession", "Accession", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{"   "});

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
    }

    // ========================================
    // CompoundSearchQuery Tests (AND/OR)
    // ========================================

    @Test
    public void testRetrieve_CompoundQuery_AND() throws Exception {
        DocumentField organismField = DocumentField.createStringField("organism", "Organism", "");
        DocumentField strategyField = DocumentField.createStringField("library_strategy", "Library Strategy", "");

        AdvancedSearchQueryTerm organismQuery = mock(AdvancedSearchQueryTerm.class);
        when(organismQuery.getField()).thenReturn(organismField);
        when(organismQuery.getValues()).thenReturn(new Object[]{TEST_ORGANISM});

        AdvancedSearchQueryTerm strategyQuery = mock(AdvancedSearchQueryTerm.class);
        when(strategyQuery.getField()).thenReturn(strategyField);
        when(strategyQuery.getValues()).thenReturn(new Object[]{"RNA-Seq"});

        List<Query> children = new ArrayList<Query>();
        children.add(organismQuery);
        children.add(strategyQuery);

        CompoundSearchQuery compoundQuery = mock(CompoundSearchQuery.class);
        // Fix: Use doReturn().when() pattern which works better with generic wildcards
        doReturn(children).when(compoundQuery).getChildren();
        when(compoundQuery.getOperator()).thenReturn(CompoundSearchQuery.Operator.AND);

        String expectedQuery = TEST_ORGANISM + "[Organism] AND RNA-Seq[Strategy]";

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setOrganism(TEST_ORGANISM);
        record.setLibraryStrategy("RNA-Seq");
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(expectedQuery), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(compoundQuery, mockCallback, new URN[0]);

        verify(mockClient).search(eq(expectedQuery), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_CompoundQuery_OR() throws Exception {
        DocumentField platformField = DocumentField.createStringField("platform", "Platform", "");

        AdvancedSearchQueryTerm illuminaQuery = mock(AdvancedSearchQueryTerm.class);
        when(illuminaQuery.getField()).thenReturn(platformField);
        when(illuminaQuery.getValues()).thenReturn(new Object[]{"Illumina"});

        AdvancedSearchQueryTerm pacbioQuery = mock(AdvancedSearchQueryTerm.class);
        when(pacbioQuery.getField()).thenReturn(platformField);
        when(pacbioQuery.getValues()).thenReturn(new Object[]{"PacBio"});

        List<Query> children = new ArrayList<Query>();
        children.add(illuminaQuery);
        children.add(pacbioQuery);

        CompoundSearchQuery compoundQuery = mock(CompoundSearchQuery.class);
        // Fix: Use doReturn().when() pattern which works better with generic wildcards
        doReturn(children).when(compoundQuery).getChildren();
        when(compoundQuery.getOperator()).thenReturn(CompoundSearchQuery.Operator.OR);

        String expectedQuery = "Illumina[Platform] OR PacBio[Platform]";

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setPlatform("Illumina");
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(expectedQuery), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(compoundQuery, mockCallback, new URN[0]);

        verify(mockClient).search(eq(expectedQuery), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_CompoundQuery_WithBasicQuery() throws Exception {
        BasicSearchQuery basicQuery = mock(BasicSearchQuery.class);
        when(basicQuery.getSearchText()).thenReturn("cancer");

        DocumentField organismField = DocumentField.createStringField("organism", "Organism", "");
        AdvancedSearchQueryTerm organismQuery = mock(AdvancedSearchQueryTerm.class);
        when(organismQuery.getField()).thenReturn(organismField);
        when(organismQuery.getValues()).thenReturn(new Object[]{TEST_ORGANISM});

        List<Query> children = new ArrayList<Query>();
        children.add(basicQuery);
        children.add(organismQuery);

        CompoundSearchQuery compoundQuery = mock(CompoundSearchQuery.class);
        // Fix: Use doReturn().when() pattern which works better with generic wildcards
        doReturn(children).when(compoundQuery).getChildren();
        when(compoundQuery.getOperator()).thenReturn(CompoundSearchQuery.Operator.AND);

        String expectedQuery = "cancer AND " + TEST_ORGANISM + "[Organism]";

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(expectedQuery), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(compoundQuery, mockCallback, new URN[0]);

        verify(mockClient).search(eq(expectedQuery), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_CompoundQuery_EmptyChildren() throws Exception {
        CompoundSearchQuery compoundQuery = mock(CompoundSearchQuery.class);
        // Fix: Use doReturn().when() pattern with empty list
        doReturn(Collections.<Query>emptyList()).when(compoundQuery).getChildren();
        when(compoundQuery.getOperator()).thenReturn(CompoundSearchQuery.Operator.AND);

        service.retrieve(compoundQuery, mockCallback, new URN[0]);

        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
    }

    @Test
    public void testRetrieve_CompoundQuery_NullChildren() throws Exception {
        CompoundSearchQuery compoundQuery = mock(CompoundSearchQuery.class);
        when(compoundQuery.getChildren()).thenReturn(null);
        when(compoundQuery.getOperator()).thenReturn(CompoundSearchQuery.Operator.AND);

        service.retrieve(compoundQuery, mockCallback, new URN[0]);

        verify(mockClient, never()).search(anyString(), anyInt(), anyInt());
    }

    // ========================================
    // Error Handling Tests
    // ========================================

    @Test(expected = DatabaseServiceException.class)
    public void testRetrieve_IOException() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("test");

        when(mockClient.search(anyString(), anyInt(), anyInt()))
            .thenThrow(new IOException("Network error"));

        service.retrieve(query, mockCallback, new URN[0]);
    }

    @Test
    public void testRetrieve_NoResults() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("nonexistent");

        SraSearchResult emptyResult = new SraSearchResult(new ArrayList<SraRecord>(), 0, 0, 10);
        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(emptyResult);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockCallback, never()).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_NullRecords() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("test");

        SraSearchResult result = new SraSearchResult();
        result.setRecords(null);
        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockCallback, never()).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test(expected = DatabaseServiceException.Canceled.class)
    public void testRetrieve_ThreadInterrupted() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("test");

        List<SraRecord> records = Arrays.asList(
            createTestSraRecord("SRR111111"),
            createTestSraRecord("SRR222222")
        );
        SraSearchResult result = new SraSearchResult(records, 2, 0, 10);
        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(result);

        // Interrupt the thread before processing
        Thread.currentThread().interrupt();

        try {
            service.retrieve(query, mockCallback, new URN[0]);
        } finally {
            // Clear the interrupt flag
            Thread.interrupted();
        }
    }

    // ========================================
    // Document Creation Tests
    // ========================================

    @Test
    public void testRetrieve_CreatesValidDocuments() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("test");

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        record.setTitle("Test RNA-Seq experiment");
        record.setOrganism(TEST_ORGANISM);
        record.setPlatform("Illumina");
        record.setLibraryStrategy("RNA-Seq");
        record.setLibraryLayout("PAIRED");
        record.setBioProject(TEST_BIOPROJECT);
        record.setBioSample(TEST_BIOSAMPLE);
        record.setTotalSpots(1000000);
        record.setTotalBases(150000000);

        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);
        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(result);

        ArgumentCaptor<AnnotatedPluginDocument> docCaptor = ArgumentCaptor.forClass(AnnotatedPluginDocument.class);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockCallback).add(docCaptor.capture(), anyMap());

        AnnotatedPluginDocument capturedDoc = docCaptor.getValue();
        assertNotNull("Document should not be null", capturedDoc);
        assertEquals("Document name should match accession", TEST_ACCESSION, capturedDoc.getName());
    }

    @Test
    public void testRetrieve_ProcessesMultipleRecords() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("test");

        List<SraRecord> records = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            records.add(createTestSraRecord("SRR" + i));
        }

        SraSearchResult result = new SraSearchResult(records, 5, 0, 10);
        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockCallback, times(5)).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    // ========================================
    // Query Building Edge Cases
    // ========================================

    @Test
    public void testRetrieve_UnknownFieldCode() throws Exception {
        DocumentField unknownField = DocumentField.createStringField("unknown_field", "Unknown", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(unknownField);
        when(query.getValues()).thenReturn(new Object[]{"test"});

        // Should fallback to basic search (no field tag)
        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq("test"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq("test"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_SearchField_NoFieldTag() throws Exception {
        DocumentField searchField = DocumentField.createStringField("search", "Search", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(searchField);
        when(query.getValues()).thenReturn(new Object[]{"cancer"});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq("cancer"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq("cancer"), eq(0), eq(10000));
    }

    @Test
    public void testRetrieve_TrimsWhitespace() throws Exception {
        DocumentField field = DocumentField.createStringField("accession", "Accession", "");
        AdvancedSearchQueryTerm query = mock(AdvancedSearchQueryTerm.class);
        when(query.getField()).thenReturn(field);
        when(query.getValues()).thenReturn(new Object[]{"  " + TEST_ACCESSION + "  "});

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(eq(TEST_ACCESSION + "[Accession]"), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(eq(TEST_ACCESSION + "[Accession]"), eq(0), eq(10000));
    }

    // ========================================
    // Large Result Set Tests
    // ========================================

    @Test
    public void testRetrieve_LargeResultSet() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("RNA-Seq");

        // Create 100 test records
        List<SraRecord> records = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            records.add(createTestSraRecord("SRR" + i));
        }

        SraSearchResult result = new SraSearchResult(records, 100, 0, 100);
        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        verify(mockClient).search(anyString(), eq(0), eq(10000));
        verify(mockCallback, times(100)).add(any(AnnotatedPluginDocument.class), anyMap());
    }

    @Test
    public void testRetrieve_MaxResults_10000() throws Exception {
        BasicSearchQuery query = mock(BasicSearchQuery.class);
        when(query.getSearchText()).thenReturn("Illumina");

        SraRecord record = createTestSraRecord(TEST_ACCESSION);
        SraSearchResult result = new SraSearchResult(Arrays.asList(record), 1, 0, 10);

        when(mockClient.search(anyString(), eq(0), eq(10000))).thenReturn(result);

        service.retrieve(query, mockCallback, new URN[0]);

        // Should request max 10000 results
        verify(mockClient).search(anyString(), eq(0), eq(10000));
    }

    // ========================================
    // Helper Methods
    // ========================================

    private QueryField findFieldByCode(QueryField[] fields, String code) {
        for (QueryField field : fields) {
            if (code.equals(field.field.getCode())) {
                return field;
            }
        }
        return null;
    }

    private boolean hasCondition(QueryField field, Condition condition) {
        for (Condition c : field.conditions) {
            if (c == condition) {
                return true;
            }
        }
        return false;
    }

    private SraRecord createTestSraRecord(String accession) {
        SraRecord record = new SraRecord(accession);
        record.setTitle("Test SRA Record");
        record.setOrganism("Test organism");
        record.setPlatform("Test platform");
        record.setLibraryStrategy("Test strategy");
        record.setLibraryLayout("SINGLE");
        record.setSubmissionDate(new Date());
        return record;
    }
}
