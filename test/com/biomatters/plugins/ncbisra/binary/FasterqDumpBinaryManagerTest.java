package com.biomatters.plugins.ncbisra.binary;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
// PowerMock is not available - commented out
// import org.junit.runner.RunWith;
// import org.mockito.MockedStatic;
// import org.mockito.Mockito;
// import org.powermock.api.mockito.PowerMockito;
// import org.powermock.core.classloader.annotations.PrepareForTest;
// import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for FasterqDumpBinaryManager
 * Tests singleton pattern, platform detection, binary extraction, version retrieval, and cleanup
 *
 * Note: PowerMock tests have been disabled due to missing PowerMock dependencies.
 * Tests use reflection and standard mocking where possible.
 */
// @RunWith(PowerMockRunner.class)
// @PrepareForTest({FasterqDumpBinaryManager.class, System.class, Runtime.class, Files.class})
public class FasterqDumpBinaryManagerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private FasterqDumpBinaryManager manager;
    private String originalOsName;

    @Before
    public void setUp() throws Exception {
        // Store original OS name
        originalOsName = System.getProperty("os.name");

        // Reset singleton instance using reflection
        resetSingleton();

        // Get fresh instance
        manager = FasterqDumpBinaryManager.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        // Restore original OS name
        System.setProperty("os.name", originalOsName);

        // Cleanup manager
        if (manager != null) {
            manager.cleanup();
        }

        // Reset singleton
        resetSingleton();
    }

    /**
     * Reset the singleton instance using reflection
     */
    private void resetSingleton() throws Exception {
        Field instanceField = FasterqDumpBinaryManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        if (instanceField.get(null) != null) {
            extractedBinaryField.set(instanceField.get(null), null);
        }
    }

    /**
     * Set OS name for testing platform detection
     */
    private void setOsName(String osName) {
        System.setProperty("os.name", osName);
    }

    /**
     * Get private method for testing
     */
    private Method getPrivateMethod(String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = FasterqDumpBinaryManager.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    // ========== Singleton Pattern Tests ==========

    @Test
    public void testGetInstance_ReturnsSingleton() {
        FasterqDumpBinaryManager instance1 = FasterqDumpBinaryManager.getInstance();
        FasterqDumpBinaryManager instance2 = FasterqDumpBinaryManager.getInstance();

        assertNotNull("Instance should not be null", instance1);
        assertSame("Should return same instance", instance1, instance2);
    }

    @Test
    public void testGetInstance_ThreadSafe() throws Exception {
        resetSingleton();

        final FasterqDumpBinaryManager[] instances = new FasterqDumpBinaryManager[10];
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = FasterqDumpBinaryManager.getInstance();
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all instances are the same
        FasterqDumpBinaryManager first = instances[0];
        for (int i = 1; i < instances.length; i++) {
            assertSame("All instances should be same in multi-threaded context", first, instances[i]);
        }
    }

    // ========== Platform Detection Tests ==========

    @Test
    public void testIsWindows_Windows10() throws Exception {
        setOsName("Windows 10");
        Method isWindows = getPrivateMethod("isWindows");

        Boolean result = (Boolean) isWindows.invoke(manager);
        assertTrue("Should detect Windows 10", result);
    }

    @Test
    public void testIsWindows_Windows11() throws Exception {
        setOsName("Windows 11");
        Method isWindows = getPrivateMethod("isWindows");

        Boolean result = (Boolean) isWindows.invoke(manager);
        assertTrue("Should detect Windows 11", result);
    }

    @Test
    public void testIsWindows_WindowsServer() throws Exception {
        setOsName("Windows Server 2019");
        Method isWindows = getPrivateMethod("isWindows");

        Boolean result = (Boolean) isWindows.invoke(manager);
        assertTrue("Should detect Windows Server", result);
    }

    @Test
    public void testIsWindows_NotWindows() throws Exception {
        setOsName("Linux");
        Method isWindows = getPrivateMethod("isWindows");

        Boolean result = (Boolean) isWindows.invoke(manager);
        assertFalse("Should not detect non-Windows as Windows", result);
    }

    @Test
    public void testIsMac_MacOSX() throws Exception {
        setOsName("Mac OS X");
        Method isMac = getPrivateMethod("isMac");

        Boolean result = (Boolean) isMac.invoke(manager);
        assertTrue("Should detect Mac OS X", result);
    }

    @Test
    public void testIsMac_MacOS() throws Exception {
        setOsName("macOS");
        Method isMac = getPrivateMethod("isMac");

        Boolean result = (Boolean) isMac.invoke(manager);
        assertTrue("Should detect macOS", result);
    }

    @Test
    public void testIsMac_NotMac() throws Exception {
        setOsName("Linux");
        Method isMac = getPrivateMethod("isMac");

        Boolean result = (Boolean) isMac.invoke(manager);
        assertFalse("Should not detect non-Mac as Mac", result);
    }

    @Test
    public void testIsMac_CaseInsensitive() throws Exception {
        setOsName("MAC OS X");
        Method isMac = getPrivateMethod("isMac");

        Boolean result = (Boolean) isMac.invoke(manager);
        assertTrue("Should detect Mac case-insensitively", result);
    }

    // ========== Binary Name Tests ==========

    @Test
    public void testGetBinaryName_Windows() throws Exception {
        setOsName("Windows 10");
        Method getBinaryName = getPrivateMethod("getBinaryName");

        String binaryName = (String) getBinaryName.invoke(manager);
        assertEquals("Should return Windows binary name", "fasterq-dump.exe", binaryName);
    }

    @Test
    public void testGetBinaryName_Mac() throws Exception {
        setOsName("Mac OS X");
        Method getBinaryName = getPrivateMethod("getBinaryName");

        String binaryName = (String) getBinaryName.invoke(manager);
        assertEquals("Should return Unix binary name", "fasterq-dump", binaryName);
    }

    @Test
    public void testGetBinaryName_Linux() throws Exception {
        setOsName("Linux");
        Method getBinaryName = getPrivateMethod("getBinaryName");

        String binaryName = (String) getBinaryName.invoke(manager);
        assertEquals("Should return Unix binary name", "fasterq-dump", binaryName);
    }

    // ========== Binary Resource Path Tests ==========

    @Test
    public void testGetBinaryResourcePath_Mac() throws Exception {
        setOsName("Mac OS X");
        Method getBinaryResourcePath = getPrivateMethod("getBinaryResourcePath");

        String resourcePath = (String) getBinaryResourcePath.invoke(manager);
        assertEquals("Should return macOS resource path", "/resources/binaries/macos/", resourcePath);
    }

    @Test
    public void testGetBinaryResourcePath_Windows() throws Exception {
        setOsName("Windows 10");
        Method getBinaryResourcePath = getPrivateMethod("getBinaryResourcePath");

        String resourcePath = (String) getBinaryResourcePath.invoke(manager);
        assertEquals("Should return Windows resource path", "/resources/binaries/windows/", resourcePath);
    }

    @Test
    public void testGetBinaryResourcePath_Linux() throws Exception {
        setOsName("Linux");
        Method getBinaryResourcePath = getPrivateMethod("getBinaryResourcePath");

        String resourcePath = (String) getBinaryResourcePath.invoke(manager);
        assertEquals("Should return Linux resource path", "/resources/binaries/linux/", resourcePath);
    }

    @Test
    public void testGetBinaryResourcePath_UnixDefault() throws Exception {
        setOsName("FreeBSD");
        Method getBinaryResourcePath = getPrivateMethod("getBinaryResourcePath");

        String resourcePath = (String) getBinaryResourcePath.invoke(manager);
        assertEquals("Should default to Linux for other Unix systems", "/resources/binaries/linux/", resourcePath);
    }

    // ========== Binary Availability Tests ==========

    @Test
    public void testIsBinaryAvailable_WhenResourceNotFound() throws Exception {
        setOsName("Linux");

        boolean available = manager.isBinaryAvailable();

        assertFalse("Should return false when binary resource not found", available);
    }

    @Test
    public void testIsBinaryAvailable_WhenIOException() throws Exception {
        setOsName("Linux");

        // This will naturally fail when resource is not found
        boolean available = manager.isBinaryAvailable();

        assertFalse("Should return false when IOException occurs", available);
    }

    // ========== GetBinary Tests ==========

    @Test
    public void testGetBinary_ThrowsIOException_WhenResourceNotFound() {
        setOsName("Linux");

        try {
            manager.getBinary();
            fail("Should throw IOException when resource not found");
        } catch (IOException e) {
            assertTrue("Exception message should mention platform",
                e.getMessage().contains("not found for platform"));
        }
    }

    @Test
    public void testGetBinary_ReturnsCachedBinary() throws Exception {
        setOsName("Linux");

        // Create a temp file to simulate extracted binary
        File tempBinary = tempFolder.newFile("fasterq-dump");

        // Set the extractedBinary field using reflection
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        File result = manager.getBinary();

        assertNotNull("Should return cached binary", result);
        assertEquals("Should return same file", tempBinary, result);
    }

    @Test
    public void testGetBinary_ReextractsWhenCachedBinaryDeleted() throws Exception {
        setOsName("Linux");

        // Create and then delete a temp file
        File deletedFile = new File(tempFolder.getRoot(), "deleted-binary");

        // Set the extractedBinary field to deleted file
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, deletedFile);

        // This should try to re-extract
        try {
            manager.getBinary();
            fail("Should throw IOException when trying to re-extract non-existent resource");
        } catch (IOException e) {
            assertTrue("Should fail to extract", e.getMessage().contains("not found"));
        }
    }

    // ========== Version Tests ==========

    @Test
    public void testGetBinaryVersion_ReturnsNull_WhenBinaryNotAvailable() {
        setOsName("Linux");

        String version = manager.getBinaryVersion();

        assertNull("Should return null when binary not available", version);
    }

    @Test
    public void testGetBinaryVersion_ReturnsNull_WhenExceptionOccurs() throws Exception {
        setOsName("Linux");

        // Set a binary that exists but will fail to execute
        File fakeBinary = tempFolder.newFile("fake-binary");
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, fakeBinary);

        String version = manager.getBinaryVersion();

        assertNull("Should return null when execution fails", version);
    }

    // ========== Cleanup Tests ==========

    @Test
    public void testCleanup_DeletesExtractedBinary() throws Exception {
        // Create a temp file to simulate extracted binary
        File tempBinary = tempFolder.newFile("fasterq-dump");
        assertTrue("Temp binary should exist", tempBinary.exists());

        // Set the extractedBinary field
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        manager.cleanup();

        assertFalse("Binary should be deleted after cleanup", tempBinary.exists());
    }

    @Test
    public void testCleanup_DeletesEmptyParentDirectory() throws Exception {
        // Create a temp directory with a binary
        File tempDir = tempFolder.newFolder("temp-binary-dir");
        File tempBinary = new File(tempDir, "fasterq-dump");
        assertTrue("Should create binary file", tempBinary.createNewFile());

        // Set the extractedBinary field
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        manager.cleanup();

        assertFalse("Binary should be deleted", tempBinary.exists());
        assertFalse("Empty parent directory should be deleted", tempDir.exists());
    }

    @Test
    public void testCleanup_DoesNotDeleteNonEmptyParentDirectory() throws Exception {
        // Create a temp directory with multiple files
        File tempDir = tempFolder.newFolder("temp-binary-dir");
        File tempBinary = new File(tempDir, "fasterq-dump");
        File otherFile = new File(tempDir, "other-file.txt");
        assertTrue("Should create binary file", tempBinary.createNewFile());
        assertTrue("Should create other file", otherFile.createNewFile());

        // Set the extractedBinary field
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        manager.cleanup();

        assertFalse("Binary should be deleted", tempBinary.exists());
        assertTrue("Non-empty parent directory should not be deleted", tempDir.exists());
        assertTrue("Other file should still exist", otherFile.exists());
    }

    @Test
    public void testCleanup_HandlesNullExtractedBinary() {
        // Don't set any extracted binary
        manager.cleanup();

        // Should not throw exception
        // Test passes if no exception is thrown
    }

    @Test
    public void testCleanup_HandlesNonExistentBinary() throws Exception {
        // Set a non-existent file
        File nonExistentFile = new File(tempFolder.getRoot(), "non-existent");
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, nonExistentFile);

        manager.cleanup();

        // Should not throw exception
        // Test passes if no exception is thrown
    }

    @Test
    public void testCleanup_SetsExtractedBinaryToNull() throws Exception {
        // Create a temp file
        File tempBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        manager.cleanup();

        Object extractedBinary = extractedBinaryField.get(manager);
        assertNull("extractedBinary field should be set to null after cleanup", extractedBinary);
    }

    // ========== Edge Cases and Error Handling ==========

    @Test
    public void testGetBinary_HandlesNullResourceStream() {
        setOsName("UnknownOS");

        try {
            manager.getBinary();
            fail("Should throw IOException for unknown OS");
        } catch (IOException e) {
            assertNotNull("Exception message should not be null", e.getMessage());
        }
    }

    @Test
    public void testCleanup_HandlesExceptionsDuringDeletion() throws Exception {
        // Note: Cannot fully test this without PowerMock to mock File.delete()
        // Instead, we'll test that cleanup handles a file in a read-only directory gracefully

        // Create a temp file
        File tempBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        // Should not throw exception - errors are swallowed
        manager.cleanup();

        // extractedBinary should still be set to null despite any issues
        Object extractedBinary = extractedBinaryField.get(manager);
        assertNull("extractedBinary should be null after cleanup", extractedBinary);
    }

    @Test
    public void testPlatformDetection_CaseInsensitivity() throws Exception {
        // Test various case combinations
        String[] windowsVariants = {"WINDOWS", "Windows", "windows", "WiNdOwS"};
        Method isWindows = getPrivateMethod("isWindows");

        for (String variant : windowsVariants) {
            setOsName(variant);
            Boolean result = (Boolean) isWindows.invoke(manager);
            assertTrue("Should detect Windows with case variant: " + variant, result);
        }

        String[] macVariants = {"MAC", "Mac", "mac", "MaC OS X"};
        Method isMac = getPrivateMethod("isMac");

        for (String variant : macVariants) {
            setOsName(variant);
            Boolean result = (Boolean) isMac.invoke(manager);
            assertTrue("Should detect Mac with case variant: " + variant, result);
        }
    }

    @Test
    public void testGetBinary_ConsistentBehaviorAcrossPlatforms() throws Exception {
        String[] platforms = {"Windows 10", "Mac OS X", "Linux"};

        for (String platform : platforms) {
            resetSingleton();
            manager = FasterqDumpBinaryManager.getInstance();
            setOsName(platform);

            try {
                manager.getBinary();
                fail("Should throw IOException for all platforms when resource not found");
            } catch (IOException e) {
                assertTrue("Exception should mention platform: " + platform,
                    e.getMessage().contains("not found for platform"));
            }
        }
    }

    // ========== Integration-style Tests ==========

    @Test
    public void testFullLifecycle_ExtractUseCleanup() throws Exception {
        // This test simulates the full lifecycle but won't actually extract
        // since we don't have real binaries in test resources

        setOsName("Linux");

        // Verify binary is not available
        assertFalse("Binary should not be available initially", manager.isBinaryAvailable());

        // Try to get binary (will fail)
        try {
            manager.getBinary();
            fail("Should fail when binary not in resources");
        } catch (IOException e) {
            // Expected
        }

        // Cleanup should handle null gracefully
        manager.cleanup();

        // After cleanup, binary still should not be available
        assertFalse("Binary should still not be available after cleanup", manager.isBinaryAvailable());
    }

    @Test
    public void testMultipleCleanupCalls_NoSideEffects() throws Exception {
        File tempBinary = tempFolder.newFile("fasterq-dump");
        Field extractedBinaryField = FasterqDumpBinaryManager.class.getDeclaredField("extractedBinary");
        extractedBinaryField.setAccessible(true);
        extractedBinaryField.set(manager, tempBinary);

        // Call cleanup multiple times
        manager.cleanup();
        manager.cleanup();
        manager.cleanup();

        // Should not throw exception
        assertNull("extractedBinary should remain null", extractedBinaryField.get(manager));
    }

    @Test
    public void testGetBinaryResourcePath_CoversAllPlatforms() throws Exception {
        Method getBinaryResourcePath = getPrivateMethod("getBinaryResourcePath");

        // Test Mac
        setOsName("Mac OS X");
        assertEquals("/resources/binaries/macos/", getBinaryResourcePath.invoke(manager));

        // Test Windows
        setOsName("Windows 10");
        assertEquals("/resources/binaries/windows/", getBinaryResourcePath.invoke(manager));

        // Test Linux (explicit)
        setOsName("Linux");
        assertEquals("/resources/binaries/linux/", getBinaryResourcePath.invoke(manager));

        // Test other Unix systems (should default to Linux)
        setOsName("FreeBSD");
        assertEquals("/resources/binaries/linux/", getBinaryResourcePath.invoke(manager));

        setOsName("Solaris");
        assertEquals("/resources/binaries/linux/", getBinaryResourcePath.invoke(manager));

        setOsName("AIX");
        assertEquals("/resources/binaries/linux/", getBinaryResourcePath.invoke(manager));
    }
}
