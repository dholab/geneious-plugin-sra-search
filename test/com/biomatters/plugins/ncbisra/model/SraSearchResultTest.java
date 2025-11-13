package com.biomatters.plugins.ncbisra.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for SraSearchResult model class
 * Tests cover constructors, getters/setters, pagination logic, edge cases, and boundary values
 */
public class SraSearchResultTest {

    private SraSearchResult searchResult;
    private List<SraRecord> testRecords;

    @Before
    public void setUp() {
        searchResult = new SraSearchResult();
        testRecords = createTestRecords(5);
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testDefaultConstructor() {
        SraSearchResult result = new SraSearchResult();
        assertNotNull("Default constructor should create instance", result);
        assertNull("Records should be null by default", result.getRecords());
        assertEquals("Total count should be zero", 0, result.getTotalCount());
        assertEquals("RetStart should be zero", 0, result.getRetStart());
        assertEquals("RetMax should be zero", 0, result.getRetMax());
        assertNull("QueryKey should be null", result.getQueryKey());
        assertNull("WebEnv should be null", result.getWebEnv());
    }

    @Test
    public void testParameterizedConstructor() {
        List<SraRecord> records = createTestRecords(10);
        SraSearchResult result = new SraSearchResult(records, 100, 0, 10);

        assertNotNull("Parameterized constructor should create instance", result);
        assertEquals("Records should match", records, result.getRecords());
        assertEquals("Total count should match", 100, result.getTotalCount());
        assertEquals("RetStart should match", 0, result.getRetStart());
        assertEquals("RetMax should match", 10, result.getRetMax());
    }

    @Test
    public void testConstructorWithNullRecords() {
        SraSearchResult result = new SraSearchResult(null, 100, 0, 10);
        assertNotNull("Constructor should handle null records", result);
        assertNull("Records should be null", result.getRecords());
        assertEquals("Total count should match", 100, result.getTotalCount());
    }

    @Test
    public void testConstructorWithEmptyRecords() {
        List<SraRecord> emptyList = new ArrayList<>();
        SraSearchResult result = new SraSearchResult(emptyList, 0, 0, 10);
        assertNotNull("Constructor should handle empty records", result);
        assertEquals("Records should be empty", 0, result.getRecords().size());
        assertEquals("Total count should be zero", 0, result.getTotalCount());
    }

    @Test
    public void testConstructorWithZeroValues() {
        SraSearchResult result = new SraSearchResult(testRecords, 0, 0, 0);
        assertEquals("Should handle zero total count", 0, result.getTotalCount());
        assertEquals("Should handle zero retStart", 0, result.getRetStart());
        assertEquals("Should handle zero retMax", 0, result.getRetMax());
    }

    @Test
    public void testConstructorWithNegativeValues() {
        // Edge case: negative values are technically allowed
        SraSearchResult result = new SraSearchResult(testRecords, -1, -1, -1);
        assertEquals("Should store negative total count", -1, result.getTotalCount());
        assertEquals("Should store negative retStart", -1, result.getRetStart());
        assertEquals("Should store negative retMax", -1, result.getRetMax());
    }

    // ==================== Records Tests ====================

    @Test
    public void testGetSetRecords() {
        searchResult.setRecords(testRecords);
        assertEquals("Records should match", testRecords, searchResult.getRecords());
        assertEquals("Should have 5 records", 5, searchResult.getRecords().size());
    }

    @Test
    public void testSetRecordsNull() {
        searchResult.setRecords(testRecords);
        searchResult.setRecords(null);
        assertNull("Records should be null", searchResult.getRecords());
    }

    @Test
    public void testSetRecordsEmpty() {
        List<SraRecord> emptyList = new ArrayList<>();
        searchResult.setRecords(emptyList);
        assertNotNull("Records should not be null", searchResult.getRecords());
        assertTrue("Records should be empty", searchResult.getRecords().isEmpty());
    }

    @Test
    public void testSetRecordsLargeList() {
        List<SraRecord> largeList = createTestRecords(10000);
        searchResult.setRecords(largeList);
        assertEquals("Should handle large record list", 10000, searchResult.getRecords().size());
    }

    @Test
    public void testRecordsListModification() {
        searchResult.setRecords(testRecords);
        List<SraRecord> retrievedRecords = searchResult.getRecords();
        retrievedRecords.add(new SraRecord("NEW_RECORD"));

        // Verify that modification affects the original list (not a copy)
        assertEquals("Modification should affect original list", 6, searchResult.getRecords().size());
    }

    // ==================== Total Count Tests ====================

    @Test
    public void testGetSetTotalCount() {
        searchResult.setTotalCount(1000);
        assertEquals("Total count should match", 1000, searchResult.getTotalCount());
    }

    @Test
    public void testSetTotalCountZero() {
        searchResult.setTotalCount(0);
        assertEquals("Total count should be zero", 0, searchResult.getTotalCount());
    }

    @Test
    public void testSetTotalCountMaxValue() {
        searchResult.setTotalCount(Integer.MAX_VALUE);
        assertEquals("Should handle max integer value", Integer.MAX_VALUE, searchResult.getTotalCount());
    }

    @Test
    public void testSetTotalCountNegative() {
        searchResult.setTotalCount(-100);
        assertEquals("Should store negative value", -100, searchResult.getTotalCount());
    }

    // ==================== RetStart Tests ====================

    @Test
    public void testGetSetRetStart() {
        searchResult.setRetStart(50);
        assertEquals("RetStart should match", 50, searchResult.getRetStart());
    }

    @Test
    public void testSetRetStartZero() {
        searchResult.setRetStart(0);
        assertEquals("RetStart should be zero", 0, searchResult.getRetStart());
    }

    @Test
    public void testSetRetStartMaxValue() {
        searchResult.setRetStart(Integer.MAX_VALUE);
        assertEquals("Should handle max integer value", Integer.MAX_VALUE, searchResult.getRetStart());
    }

    @Test
    public void testSetRetStartNegative() {
        searchResult.setRetStart(-10);
        assertEquals("Should store negative value", -10, searchResult.getRetStart());
    }

    // ==================== RetMax Tests ====================

    @Test
    public void testGetSetRetMax() {
        searchResult.setRetMax(20);
        assertEquals("RetMax should match", 20, searchResult.getRetMax());
    }

    @Test
    public void testSetRetMaxZero() {
        searchResult.setRetMax(0);
        assertEquals("RetMax should be zero", 0, searchResult.getRetMax());
    }

    @Test
    public void testSetRetMaxMaxValue() {
        searchResult.setRetMax(Integer.MAX_VALUE);
        assertEquals("Should handle max integer value", Integer.MAX_VALUE, searchResult.getRetMax());
    }

    // ==================== Query Key Tests ====================

    @Test
    public void testGetSetQueryKey() {
        String queryKey = "123456789";
        searchResult.setQueryKey(queryKey);
        assertEquals("Query key should match", queryKey, searchResult.getQueryKey());
    }

    @Test
    public void testSetQueryKeyNull() {
        searchResult.setQueryKey("test");
        searchResult.setQueryKey(null);
        assertNull("Query key should be null", searchResult.getQueryKey());
    }

    @Test
    public void testSetQueryKeyEmpty() {
        searchResult.setQueryKey("");
        assertEquals("Empty query key should be preserved", "", searchResult.getQueryKey());
    }

    @Test
    public void testSetQueryKeySpecialCharacters() {
        String specialKey = "query-key_123.456";
        searchResult.setQueryKey(specialKey);
        assertEquals("Query key with special chars should be preserved", specialKey, searchResult.getQueryKey());
    }

    // ==================== WebEnv Tests ====================

    @Test
    public void testGetSetWebEnv() {
        String webEnv = "NCID_1_12345678_130.14.22.215_9001_1234567890";
        searchResult.setWebEnv(webEnv);
        assertEquals("WebEnv should match", webEnv, searchResult.getWebEnv());
    }

    @Test
    public void testSetWebEnvNull() {
        searchResult.setWebEnv("test");
        searchResult.setWebEnv(null);
        assertNull("WebEnv should be null", searchResult.getWebEnv());
    }

    @Test
    public void testSetWebEnvEmpty() {
        searchResult.setWebEnv("");
        assertEquals("Empty WebEnv should be preserved", "", searchResult.getWebEnv());
    }

    @Test
    public void testSetWebEnvLongString() {
        String longWebEnv = createLongString(1000);
        searchResult.setWebEnv(longWebEnv);
        assertEquals("Long WebEnv should be preserved", longWebEnv, searchResult.getWebEnv());
    }

    // ==================== hasMoreResults() Business Logic Tests ====================

    @Test
    public void testHasMoreResultsWhenMoreExist() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(100);
        searchResult.setRetStart(0);

        assertTrue("Should have more results when retStart + records < totalCount",
                searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWhenNoMoreExist() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(10);
        searchResult.setRetStart(0);

        assertFalse("Should not have more results when retStart + records == totalCount",
                searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWhenAtEnd() {
        searchResult.setRecords(createTestRecords(5));
        searchResult.setTotalCount(100);
        searchResult.setRetStart(95);

        assertFalse("Should not have more results when at end", searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWhenExactlyAtEnd() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(20);
        searchResult.setRetStart(10);

        assertFalse("Should not have more results when exactly at end", searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWithEmptyRecords() {
        searchResult.setRecords(new ArrayList<>());
        searchResult.setTotalCount(100);
        searchResult.setRetStart(0);

        assertTrue("Should have more results even with empty current page",
                searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWithZeroTotalCount() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(0);
        searchResult.setRetStart(0);

        assertFalse("Should not have more results with zero total count",
                searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWithOneRecordRemaining() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(11);
        searchResult.setRetStart(0);

        assertTrue("Should have more results with one record remaining",
                searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsInMiddleOfResults() {
        searchResult.setRecords(createTestRecords(20));
        searchResult.setTotalCount(100);
        searchResult.setRetStart(40);

        assertTrue("Should have more results in middle of pagination",
                searchResult.hasMoreResults());
    }

    @Test
    public void testHasMoreResultsWithLargeOffset() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(1000000);
        searchResult.setRetStart(999990);

        assertFalse("Should not have more results at large offset",
                searchResult.hasMoreResults());
    }

    @Test(expected = NullPointerException.class)
    public void testHasMoreResultsWithNullRecords() {
        searchResult.setRecords(null);
        searchResult.setTotalCount(100);
        searchResult.setRetStart(0);

        // Should throw NullPointerException when calling records.size() on null
        searchResult.hasMoreResults();
    }

    // ==================== getNextStartIndex() Business Logic Tests ====================

    @Test
    public void testGetNextStartIndexFromBeginning() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setRetStart(0);

        assertEquals("Next start index should be 10", 10, searchResult.getNextStartIndex());
    }

    @Test
    public void testGetNextStartIndexFromMiddle() {
        searchResult.setRecords(createTestRecords(20));
        searchResult.setRetStart(40);

        assertEquals("Next start index should be 60", 60, searchResult.getNextStartIndex());
    }

    @Test
    public void testGetNextStartIndexWithEmptyRecords() {
        searchResult.setRecords(new ArrayList<>());
        searchResult.setRetStart(50);

        assertEquals("Next start index should equal retStart with empty records",
                50, searchResult.getNextStartIndex());
    }

    @Test
    public void testGetNextStartIndexWithSingleRecord() {
        searchResult.setRecords(createTestRecords(1));
        searchResult.setRetStart(0);

        assertEquals("Next start index should be 1", 1, searchResult.getNextStartIndex());
    }

    @Test
    public void testGetNextStartIndexWithLargeOffset() {
        searchResult.setRecords(createTestRecords(100));
        searchResult.setRetStart(999900);

        assertEquals("Next start index should be correct", 1000000, searchResult.getNextStartIndex());
    }

    @Test
    public void testGetNextStartIndexWithZeroStart() {
        searchResult.setRecords(createTestRecords(5));
        searchResult.setRetStart(0);

        assertEquals("Next start index should be 5", 5, searchResult.getNextStartIndex());
    }

    @Test
    public void testGetNextStartIndexConsistency() {
        searchResult.setRecords(createTestRecords(15));
        searchResult.setRetStart(30);

        int nextIndex = searchResult.getNextStartIndex();
        assertEquals("Next index should be consistent", 45, nextIndex);

        // Call again to verify consistency
        int nextIndex2 = searchResult.getNextStartIndex();
        assertEquals("Next index should be same on multiple calls", nextIndex, nextIndex2);
    }

    @Test(expected = NullPointerException.class)
    public void testGetNextStartIndexWithNullRecords() {
        searchResult.setRecords(null);
        searchResult.setRetStart(10);

        // Should throw NullPointerException when calling records.size() on null
        searchResult.getNextStartIndex();
    }

    // ==================== toString() Tests ====================

    @Test
    public void testToStringWithPopulatedData() {
        searchResult.setRecords(createTestRecords(10));
        searchResult.setTotalCount(100);
        searchResult.setRetStart(20);
        searchResult.setRetMax(10);

        String result = searchResult.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain recordCount", result.contains("recordCount=10"));
        assertTrue("toString should contain totalCount", result.contains("totalCount=100"));
        assertTrue("toString should contain retStart", result.contains("retStart=20"));
        assertTrue("toString should contain retMax", result.contains("retMax=10"));
    }

    @Test
    public void testToStringWithNullRecords() {
        searchResult.setRecords(null);
        searchResult.setTotalCount(100);

        String result = searchResult.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain recordCount=0 for null records",
                result.contains("recordCount=0"));
    }

    @Test
    public void testToStringWithEmptyRecords() {
        searchResult.setRecords(new ArrayList<>());
        searchResult.setTotalCount(0);

        String result = searchResult.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain recordCount=0", result.contains("recordCount=0"));
        assertTrue("toString should contain totalCount=0", result.contains("totalCount=0"));
    }

    @Test
    public void testToStringWithDefaultValues() {
        String result = searchResult.toString();
        assertNotNull("toString should not return null with default values", result);
        assertTrue("toString should contain SraSearchResult", result.contains("SraSearchResult"));
    }

    // ==================== Integration/Pagination Scenario Tests ====================

    @Test
    public void testPaginationScenarioFirstPage() {
        // Simulate first page of results
        List<SraRecord> records = createTestRecords(20);
        SraSearchResult result = new SraSearchResult(records, 100, 0, 20);

        assertTrue("First page should have more results", result.hasMoreResults());
        assertEquals("Next start should be 20", 20, result.getNextStartIndex());
        assertEquals("Should have 20 records", 20, result.getRecords().size());
    }

    @Test
    public void testPaginationScenarioMiddlePage() {
        // Simulate middle page of results
        List<SraRecord> records = createTestRecords(20);
        SraSearchResult result = new SraSearchResult(records, 100, 40, 20);

        assertTrue("Middle page should have more results", result.hasMoreResults());
        assertEquals("Next start should be 60", 60, result.getNextStartIndex());
    }

    @Test
    public void testPaginationScenarioLastPage() {
        // Simulate last page of results
        List<SraRecord> records = createTestRecords(10);
        SraSearchResult result = new SraSearchResult(records, 100, 90, 20);

        assertFalse("Last page should not have more results", result.hasMoreResults());
        assertEquals("Next start should be 100", 100, result.getNextStartIndex());
    }

    @Test
    public void testPaginationScenarioPartialLastPage() {
        // Simulate partial last page (fewer records than retMax)
        List<SraRecord> records = createTestRecords(5);
        SraSearchResult result = new SraSearchResult(records, 95, 90, 20);

        assertFalse("Partial last page should not have more results", result.hasMoreResults());
        assertEquals("Next start should be 95", 95, result.getNextStartIndex());
    }

    @Test
    public void testPaginationScenarioSinglePage() {
        // All results fit in single page
        List<SraRecord> records = createTestRecords(10);
        SraSearchResult result = new SraSearchResult(records, 10, 0, 20);

        assertFalse("Single page should not have more results", result.hasMoreResults());
        assertEquals("Next start should be 10", 10, result.getNextStartIndex());
    }

    @Test
    public void testPaginationScenarioNoResults() {
        // No results found
        List<SraRecord> records = new ArrayList<>();
        SraSearchResult result = new SraSearchResult(records, 0, 0, 20);

        assertFalse("No results should not have more results", result.hasMoreResults());
        assertEquals("Next start should be 0", 0, result.getNextStartIndex());
    }

    @Test
    public void testSessionDataWithPagination() {
        // Test with query session data
        SraSearchResult result = new SraSearchResult(createTestRecords(20), 100, 0, 20);
        result.setQueryKey("123456789");
        result.setWebEnv("NCID_1_12345678_130.14.22.215_9001_1234567890");

        assertNotNull("Query key should be set", result.getQueryKey());
        assertNotNull("WebEnv should be set", result.getWebEnv());
        assertTrue("Should have more results", result.hasMoreResults());
        assertEquals("Next start should be 20", 20, result.getNextStartIndex());
    }

    @Test
    public void testCompleteResultIteration() {
        // Simulate iterating through all pages
        int totalCount = 100;
        int pageSize = 20;
        int currentStart = 0;

        for (int page = 0; page < 5; page++) {
            int recordsInPage = Math.min(pageSize, totalCount - currentStart);
            List<SraRecord> records = createTestRecords(recordsInPage);
            SraSearchResult result = new SraSearchResult(records, totalCount, currentStart, pageSize);

            if (page < 4) {
                assertTrue("Page " + page + " should have more results", result.hasMoreResults());
            } else {
                assertFalse("Last page should not have more results", result.hasMoreResults());
            }

            currentStart = result.getNextStartIndex();
        }

        assertEquals("Final start index should equal total count", totalCount, currentStart);
    }

    @Test
    public void testBoundaryConditionExactlyOneMorePage() {
        // Test when there's exactly one more full page
        List<SraRecord> records = createTestRecords(20);
        SraSearchResult result = new SraSearchResult(records, 40, 0, 20);

        assertTrue("Should have exactly one more page", result.hasMoreResults());
        assertEquals("Next start should be 20", 20, result.getNextStartIndex());

        // Simulate getting the next page
        List<SraRecord> nextRecords = createTestRecords(20);
        SraSearchResult nextResult = new SraSearchResult(nextRecords, 40, 20, 20);

        assertFalse("Second page should be the last", nextResult.hasMoreResults());
        assertEquals("Final next start should be 40", 40, nextResult.getNextStartIndex());
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testEdgeCaseRecordsExceedTotalCount() {
        // Edge case where records size exceeds expected (data inconsistency)
        List<SraRecord> records = createTestRecords(30);
        SraSearchResult result = new SraSearchResult(records, 20, 0, 20);

        assertFalse("Should not have more results when records exceed total",
                result.hasMoreResults());
        assertEquals("Next start should be calculated correctly", 30, result.getNextStartIndex());
    }

    @Test
    public void testEdgeCaseRetStartExceedsTotalCount() {
        // Edge case where retStart is beyond total count
        List<SraRecord> records = new ArrayList<>();
        SraSearchResult result = new SraSearchResult(records, 100, 150, 20);

        assertFalse("Should not have more results when start exceeds total",
                result.hasMoreResults());
        assertEquals("Next start should be retStart when no records", 150, result.getNextStartIndex());
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a list of test SraRecord objects
     */
    private List<SraRecord> createTestRecords(int count) {
        List<SraRecord> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SraRecord record = new SraRecord("SRR" + String.format("%06d", i));
            record.setTitle("Test Record " + i);
            record.setOrganism("Test Organism");
            records.add(record);
        }
        return records;
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
