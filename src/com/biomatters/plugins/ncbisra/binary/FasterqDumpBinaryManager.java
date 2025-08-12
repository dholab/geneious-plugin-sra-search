package com.biomatters.plugins.ncbisra.binary;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Manages fasterq-dump binary extraction and execution across platforms
 */
public class FasterqDumpBinaryManager {
    
    private static final String BINARY_NAME_WINDOWS = "fasterq-dump.exe";
    private static final String BINARY_NAME_UNIX = "fasterq-dump";
    
    private static final String RESOURCE_PATH_MACOS = "/resources/binaries/macos/";
    private static final String RESOURCE_PATH_WINDOWS = "/resources/binaries/windows/";
    private static final String RESOURCE_PATH_LINUX = "/resources/binaries/linux/";
    
    private static FasterqDumpBinaryManager instance;
    private File extractedBinary;
    
    private FasterqDumpBinaryManager() {
    }
    
    public static synchronized FasterqDumpBinaryManager getInstance() {
        if (instance == null) {
            instance = new FasterqDumpBinaryManager();
        }
        return instance;
    }
    
    /**
     * Get the platform-appropriate fasterq-dump binary, extracting it if necessary
     */
    public File getBinary() throws IOException {
        if (extractedBinary != null && extractedBinary.exists()) {
            return extractedBinary;
        }
        
        extractedBinary = extractBinary();
        if (extractedBinary == null) {
            throw new IOException("fasterq-dump binary not found for platform: " + System.getProperty("os.name"));
        }
        return extractedBinary;
    }
    
    /**
     * Check if fasterq-dump binary is available for the current platform
     */
    public boolean isBinaryAvailable() {
        try {
            return getBinary() != null && getBinary().exists();
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Get the version of the fasterq-dump binary
     */
    public String getBinaryVersion() {
        try {
            File binary = getBinary();
            if (binary == null || !binary.exists()) {
                return null;
            }
            
            ProcessBuilder pb = new ProcessBuilder(binary.getAbsolutePath(), "--version");
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return output.toString().trim();
                }
            }
            
        } catch (Exception e) {
            // Ignore errors when getting version
        }
        
        return null;
    }
    
    private File extractBinary() throws IOException {
        String resourcePath = getBinaryResourcePath();
        String binaryName = getBinaryName();
        
        // Check if binary exists in resources
        InputStream binaryStream = getClass().getResourceAsStream(resourcePath + binaryName);
        if (binaryStream == null) {
            // Return null if binary doesn't exist - don't throw exception
            return null;
        }
        
        // Create temp directory for extracted binary
        Path tempDir = Files.createTempDirectory("geneious-sra-binary");
        tempDir.toFile().deleteOnExit();
        
        Path binaryPath = tempDir.resolve(binaryName);
        
        try {
            // Extract binary to temp directory
            Files.copy(binaryStream, binaryPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Make executable on Unix systems
            if (!isWindows()) {
                Runtime.getRuntime().exec(new String[]{"chmod", "+x", binaryPath.toString()}).waitFor();
            }
            
            File binaryFile = binaryPath.toFile();
            binaryFile.deleteOnExit();
            
            return binaryFile;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while setting binary permissions", e);
        } finally {
            try {
                binaryStream.close();
            } catch (IOException e) {
                // Ignore close errors
            }
        }
    }
    
    private String getBinaryResourcePath() {
        if (isMac()) {
            return RESOURCE_PATH_MACOS;
        } else if (isWindows()) {
            return RESOURCE_PATH_WINDOWS;
        } else {
            return RESOURCE_PATH_LINUX; // Default to Linux for other Unix systems
        }
    }
    
    private String getBinaryName() {
        return isWindows() ? BINARY_NAME_WINDOWS : BINARY_NAME_UNIX;
    }
    
    /**
     * Clean up extracted binary
     */
    public void cleanup() {
        if (extractedBinary != null && extractedBinary.exists()) {
            try {
                extractedBinary.delete();
                // Also try to delete parent temp directory if empty
                File parentDir = extractedBinary.getParentFile();
                if (parentDir != null && parentDir.isDirectory()) {
                    String[] files = parentDir.list();
                    if (files == null || files.length == 0) {
                        parentDir.delete();
                    }
                }
            } catch (Exception e) {
                // Ignore cleanup errors
            }
            extractedBinary = null;
        }
    }
    
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    private boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}