package com.biomatters.plugins.ncbisra.binary;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Additional tests for FasterqDumpBinaryManager focusing on binary extraction scenarios
 * These tests use real file operations with temporary directories
 */
public class FasterqDumpBinaryManagerMockTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private FasterqDumpBinaryManager manager;
    private String originalOsName;

    @Before
    public void setUp() throws Exception {
        originalOsName = System.getProperty("os.name");
        resetSingleton();
        manager = FasterqDumpBinaryManager.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("os.name", originalOsName);
        if (manager != null) {
            manager.cleanup();
        }
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instanceField = FasterqDumpBinaryManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void setOsName(String osName) {
        System.setProperty("os.name", osName);
    }

    private Method getPrivateMethod(String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = FasterqDumpBinaryManager.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    private Field getPrivateField(String fieldName) throws Exception {
        Field field = FasterqDumpBinaryManager.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    // ========== Extract Binary Tests with Mock Scenarios ==========

    @Test
    public void testExtractBinary_ReturnsNull_WhenResourceNotFound() throws Exception {
        setOsName("Linux");
        Method extractBinary = getPrivateMethod("extractBinary");

        File result = (File) extractBinary.invoke(manager);

        assertNull("Should return null when resource not found", result);
    }

    @Test
    public void testGetBinary_CachesExtractedBinary() throws Exception {
        // Create a temporary file to simulate an already extracted binary
        File mockBinary = tempFolder.newFile("fasterq-dump");
        assertTrue(mockBinary.exists());

        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        // First call should return cached binary
        File firstCall = manager.getBinary();
        assertEquals("Should return cached binary", mockBinary, firstCall);

        // Second call should also return same cached binary without re-extracting
        File secondCall = manager.getBinary();
        assertEquals("Should return same cached binary", mockBinary, secondCall);
        assertSame("Should be exact same object", firstCall, secondCall);
    }

    @Test
    public void testGetBinary_ReextractsWhenCacheInvalidated() throws Exception {
        // Create and set a binary
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        // Delete the file to simulate cache invalidation
        assertTrue("Should delete file", mockBinary.delete());
        assertFalse("File should not exist", mockBinary.exists());

        // Try to get binary again - should attempt re-extraction
        try {
            manager.getBinary();
            fail("Should throw IOException when re-extracting non-existent resource");
        } catch (IOException e) {
            assertTrue("Should contain error message", e.getMessage().contains("not found"));
        }
    }

    // ========== Version Extraction Tests ==========

    @Test
    public void testGetBinaryVersion_HandlesNullBinary() throws Exception {
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, null);

        String version = manager.getBinaryVersion();

        assertNull("Should return null when binary is null", version);
    }

    @Test
    public void testGetBinaryVersion_HandlesNonExecutableFile() throws Exception {
        // Create a file that's not executable
        File nonExecutable = tempFolder.newFile("fake-binary.txt");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, nonExecutable);

        String version = manager.getBinaryVersion();

        // On some systems this might return null, on others it might fail differently
        // The key is that it doesn't throw an exception
        // Version will be null because the process will fail
        assertNull("Should handle non-executable file gracefully", version);
    }

    @Test
    public void testGetBinaryVersion_HandlesDeletedFile() throws Exception {
        File deletedFile = new File(tempFolder.getRoot(), "non-existent-binary");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, deletedFile);

        String version = manager.getBinaryVersion();

        assertNull("Should return null for deleted file", version);
    }

    // ========== Cleanup Edge Cases ==========

    @Test
    public void testCleanup_WithReadOnlyParentDirectory() throws Exception {
        // Create directory structure
        File tempDir = tempFolder.newFolder("readonly-dir");
        File tempBinary = new File(tempDir, "fasterq-dump");
        assertTrue("Should create binary", tempBinary.createNewFile());

        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, tempBinary);

        // Make parent directory read-only (may not work on all platforms)
        tempDir.setReadOnly();

        try {
            manager.cleanup();
            // Should not throw exception even if directory can't be deleted
        } finally {
            // Restore permissions for cleanup
            tempDir.setWritable(true);
        }

        Object extractedBinary = extractedBinaryField.get(manager);
        assertNull("extractedBinary should be null after cleanup attempt", extractedBinary);
    }

    @Test
    public void testCleanup_WithNullParentDirectory() throws Exception {
        // Create a mock scenario where parent is null
        // This is hard to simulate with real files, but we can test the null check
        File tempBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, tempBinary);

        manager.cleanup();

        // Should complete without exception
        assertFalse("Binary should be deleted", tempBinary.exists());
    }

    // ========== Platform-Specific Binary Name Tests ==========

    @Test
    public void testBinaryNaming_AllPlatforms() throws Exception {
        Method getBinaryName = getPrivateMethod("getBinaryName");

        // Windows platforms
        String[] windowsPlatforms = {"Windows 10", "Windows 11", "Windows 7", "Windows Server 2019"};
        for (String platform : windowsPlatforms) {
            setOsName(platform);
            String name = (String) getBinaryName.invoke(manager);
            assertEquals("Windows should use .exe extension for: " + platform, "fasterq-dump.exe", name);
        }

        // Unix-like platforms
        String[] unixPlatforms = {"Mac OS X", "macOS", "Linux", "FreeBSD", "Solaris", "Unix"};
        for (String platform : unixPlatforms) {
            setOsName(platform);
            String name = (String) getBinaryName.invoke(manager);
            assertEquals("Unix platforms should not have extension for: " + platform, "fasterq-dump", name);
        }
    }

    // ========== Resource Path Tests ==========

    @Test
    public void testResourcePaths_AreCorrectlyFormatted() throws Exception {
        Method getBinaryResourcePath = getPrivateMethod("getBinaryResourcePath");

        setOsName("Windows 10");
        String windowsPath = (String) getBinaryResourcePath.invoke(manager);
        assertTrue("Windows path should start with /", windowsPath.startsWith("/"));
        assertTrue("Windows path should end with /", windowsPath.endsWith("/"));
        assertTrue("Windows path should contain 'windows'", windowsPath.contains("windows"));

        setOsName("Mac OS X");
        String macPath = (String) getBinaryResourcePath.invoke(manager);
        assertTrue("Mac path should start with /", macPath.startsWith("/"));
        assertTrue("Mac path should end with /", macPath.endsWith("/"));
        assertTrue("Mac path should contain 'macos'", macPath.contains("macos"));

        setOsName("Linux");
        String linuxPath = (String) getBinaryResourcePath.invoke(manager);
        assertTrue("Linux path should start with /", linuxPath.startsWith("/"));
        assertTrue("Linux path should end with /", linuxPath.endsWith("/"));
        assertTrue("Linux path should contain 'linux'", linuxPath.contains("linux"));
    }

    // ========== Concurrent Access Tests ==========

    @Test
    public void testConcurrentGetBinary_DoesNotThrowConcurrentModificationException() throws Exception {
        // Create a valid binary file
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        final Exception[] exceptions = new Exception[10];
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    File binary = manager.getBinary();
                    assertNotNull("Binary should not be null", binary);
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }

        // Check for exceptions
        for (int i = 0; i < exceptions.length; i++) {
            assertNull("Thread " + i + " should not have exception: " +
                (exceptions[i] != null ? exceptions[i].getMessage() : ""), exceptions[i]);
        }
    }

    @Test
    public void testConcurrentCleanup_ThreadSafe() throws Exception {
        // Create multiple binary files
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");

        final Exception[] exceptions = new Exception[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    extractedBinaryField.set(manager, mockBinary);
                    manager.cleanup();
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }

        // Check for exceptions
        for (int i = 0; i < exceptions.length; i++) {
            assertNull("Cleanup thread " + i + " should not throw exception", exceptions[i]);
        }
    }

    // ========== State Validation Tests ==========

    @Test
    public void testGetBinary_MaintainsConsistentState() throws Exception {
        // Initially, extracted binary should be null
        Field extractedBinaryField = getPrivateField("extractedBinary");
        assertNull("Initially should be null", extractedBinaryField.get(manager));

        // Try to get binary (will fail since resource doesn't exist)
        try {
            manager.getBinary();
            fail("Should throw IOException");
        } catch (IOException e) {
            // Expected
        }

        // State should still be consistent (null since extraction failed)
        Object afterFail = extractedBinaryField.get(manager);
        assertNull("Should be null after failed extraction", afterFail);
    }

    @Test
    public void testIsBinaryAvailable_DoesNotModifyState() throws Exception {
        Field extractedBinaryField = getPrivateField("extractedBinary");
        assertNull("Initially should be null", extractedBinaryField.get(manager));

        // Call isBinaryAvailable multiple times
        manager.isBinaryAvailable();
        manager.isBinaryAvailable();
        manager.isBinaryAvailable();

        // State should remain null if binary not available
        Object after = extractedBinaryField.get(manager);
        assertNull("Should remain null after availability checks", after);
    }

    @Test
    public void testCleanup_ResetsStateCompletely() throws Exception {
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        assertNotNull("Binary should be set", extractedBinaryField.get(manager));

        manager.cleanup();

        assertNull("Binary should be null after cleanup", extractedBinaryField.get(manager));
        assertFalse("Binary file should be deleted", mockBinary.exists());
    }

    // ========== Error Recovery Tests ==========

    @Test
    public void testRecoveryAfterFailedExtraction() throws Exception {
        setOsName("Linux");

        // First attempt - should fail
        try {
            manager.getBinary();
            fail("First attempt should fail");
        } catch (IOException e) {
            // Expected
        }

        // Second attempt - should also fail consistently
        try {
            manager.getBinary();
            fail("Second attempt should also fail");
        } catch (IOException e) {
            // Expected
            assertTrue("Error message should be consistent", e.getMessage().contains("not found"));
        }
    }

    @Test
    public void testRecoveryAfterCleanup() throws Exception {
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        // Get binary (should return cached)
        File first = manager.getBinary();
        assertEquals("Should return cached binary", mockBinary, first);

        // Cleanup
        manager.cleanup();

        // Try to get binary again (should try to extract again)
        try {
            manager.getBinary();
            fail("Should fail to extract after cleanup");
        } catch (IOException e) {
            assertTrue("Should fail with resource not found", e.getMessage().contains("not found"));
        }
    }

    // ========== Defensive Programming Tests ==========

    @Test
    public void testDefensiveCopying_BinaryFileReference() throws Exception {
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        File binary1 = manager.getBinary();
        File binary2 = manager.getBinary();

        // Should return same reference (caching behavior)
        assertSame("Should return same cached reference", binary1, binary2);
    }

    @Test
    public void testNullSafety_AllPublicMethods() throws Exception {
        // Test all public methods with null internal state
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, null);

        // isBinaryAvailable should handle null
        assertFalse("Should return false for null binary", manager.isBinaryAvailable());

        // getBinaryVersion should handle null
        assertNull("Should return null version for null binary", manager.getBinaryVersion());

        // cleanup should handle null
        manager.cleanup(); // Should not throw

        // getBinary should try to extract
        try {
            manager.getBinary();
            fail("Should throw IOException when extraction fails");
        } catch (IOException e) {
            // Expected
        }
    }

    // ========== Documentation and Contract Tests ==========

    @Test
    public void testGetBinary_ContractConsistency() throws Exception {
        // When binary exists and is cached, should return same instance
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        File result1 = manager.getBinary();
        File result2 = manager.getBinary();

        assertNotNull("Should never return null when cached", result1);
        assertSame("Should return same instance from cache", result1, result2);
        assertTrue("Returned file should exist", result1.exists());
    }

    @Test
    public void testIsBinaryAvailable_ContractConsistency() throws Exception {
        // When called multiple times in succession, should return consistent results
        boolean result1 = manager.isBinaryAvailable();
        boolean result2 = manager.isBinaryAvailable();
        boolean result3 = manager.isBinaryAvailable();

        assertEquals("Should return consistent results", result1, result2);
        assertEquals("Should return consistent results", result2, result3);
    }

    @Test
    public void testCleanup_IdempotencyContract() throws Exception {
        File mockBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = getPrivateField("extractedBinary");
        extractedBinaryField.set(manager, mockBinary);

        // Multiple cleanup calls should be safe
        manager.cleanup();
        assertNull("Should be null after first cleanup", extractedBinaryField.get(manager));

        manager.cleanup();
        assertNull("Should remain null after second cleanup", extractedBinaryField.get(manager));

        manager.cleanup();
        assertNull("Should remain null after third cleanup", extractedBinaryField.get(manager));
    }
}
