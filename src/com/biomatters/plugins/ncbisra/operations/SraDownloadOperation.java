package com.biomatters.plugins.ncbisra.operations;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.*;
import com.biomatters.geneious.publicapi.documents.sequence.DefaultSequenceListDocument;
import com.biomatters.geneious.publicapi.implementations.PairedReadManager;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.plugin.*;
import com.biomatters.plugins.ncbisra.binary.FasterqDumpBinaryManager;
import com.biomatters.plugins.ncbisra.model.SraDocument;
import com.biomatters.plugins.ncbisra.model.SraRecord;
import jebl.util.ProgressListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DocumentOperation for downloading SRA data using fasterq-dump and importing as FASTQ files
 */
public class SraDownloadOperation extends DocumentOperation {
    
    private static final String HELP_TEXT = 
        "Downloads FASTQ sequence data from NCBI SRA using the fasterq-dump tool. " +
        "This operation will download the raw sequence reads and import them as FASTQ sequence lists into Geneious. " +
        "Supports both single-end and paired-end reads. The download may take some time depending on the " +
        "size of the dataset and your internet connection.";
    
    private static final String OPERATION_NAME = "Download FASTQ Data";
    
    // Option keys
    private static final String OPTION_SPLIT_FILES = "splitFiles";
    
    @Override
    public String getUniqueId() {
        return "sra_download_fastq";
    }
    
    @Override
    public GeneiousActionOptions getActionOptions() {
        return new GeneiousActionOptions(OPERATION_NAME)
                .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools)
                .setInPopupMenu(true)
                .setInMainToolbar(false);
    }
    
    @Override
    public String getHelp() {
        return HELP_TEXT;
    }
    
    @Override
    public DocumentSelectionSignature[] getSelectionSignatures() {
        return new DocumentSelectionSignature[] {
            new DocumentSelectionSignature(SraDocument.class, 1, Integer.MAX_VALUE)
        };
    }
    
    @Override
    public Options getOptions(AnnotatedPluginDocument... documents) throws DocumentOperationException {
        // Check if fasterq-dump is available first
        FasterqDumpBinaryManager binaryManager = FasterqDumpBinaryManager.getInstance();
        if (!binaryManager.isBinaryAvailable()) {
            Options options = new Options(this.getClass());
            options.addLabel("Warning: fasterq-dump binary is not available on this system.", true, true);
            options.addLabel("This operation requires fasterq-dump from NCBI SRA Toolkit.", false, true);
            return options;
        }
        
        // Return null to skip the dialog and use default options
        // This makes the download start immediately without showing a dialog
        return null;
    }
    
    @Override
    public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] documents, 
            ProgressListener progressListener, Options options) throws DocumentOperationException {
        
        // Validate fasterq-dump availability
        FasterqDumpBinaryManager binaryManager = FasterqDumpBinaryManager.getInstance();
        if (!binaryManager.isBinaryAvailable()) {
            throw new DocumentOperationException("fasterq-dump binary is not available. Please install NCBI SRA Toolkit.");
        }
        
        // Get options (use default if options is null - happens when we skip the dialog)
        boolean splitFiles = true; // Default to splitting files for paired-end detection
        if (options != null && options.getValue(OPTION_SPLIT_FILES) != null) {
            splitFiles = (Boolean) options.getValue(OPTION_SPLIT_FILES);
        }
        
        // Create temporary directory for downloads
        File outputDirectory;
        try {
            Path tempPath = Files.createTempDirectory("sra_download_");
            outputDirectory = tempPath.toFile();
            outputDirectory.deleteOnExit(); // Mark for deletion on JVM exit
        } catch (IOException e) {
            throw new DocumentOperationException("Failed to create temporary directory for downloads: " + e.getMessage(), e);
        }
        
        List<AnnotatedPluginDocument> importedDocuments = new ArrayList<>();
        List<File> tempFilesToCleanup = new ArrayList<>();
        
        try {
            progressListener.setMessage("Initializing download...");
            
            for (int i = 0; i < documents.length; i++) {
                AnnotatedPluginDocument document = documents[i];
                
                if (!(document.getDocument() instanceof SraDocument)) {
                    System.out.println("Skipping non-SRA document: " + 
                        (document.getDocument() != null ? document.getDocument().getClass().getName() : "null"));
                    continue; // Skip non-SRA documents
                }
                
                SraDocument sraDoc = (SraDocument) document.getDocument();
                SraRecord sraRecord = sraDoc.getSraRecord();
                
                // If no SraRecord, try to extract accession from document name
                String accession = null;
                if (sraRecord != null && sraRecord.getAccession() != null) {
                    accession = sraRecord.getAccession();
                } else if (sraDoc.getName() != null && !sraDoc.getName().equals("Unknown SRA")) {
                    // Document name should be the accession
                    accession = sraDoc.getName();
                    System.out.println("Using document name as accession: " + accession);
                }
                
                if (accession == null) {
                    System.out.println("Skipping document without valid accession");
                    continue; // Skip documents without valid accession
                }
                
                // accession already set above
                double baseProgress = (double) i / documents.length;
                double nextProgress = (double) (i + 1) / documents.length;
                
                progressListener.setMessage(String.format("Downloading %s (%d of %d)...", 
                        accession, i + 1, documents.length));
                progressListener.setProgress(baseProgress);
                
                try {
                    // Download the SRA data
                    List<File> downloadedFiles = downloadSraData(accession, outputDirectory, 
                            splitFiles, binaryManager, progressListener, baseProgress, nextProgress);
                    
                    if (downloadedFiles.isEmpty()) {
                        throw new DocumentOperationException("No files were downloaded for " + accession);
                    }
                    
                    tempFilesToCleanup.addAll(downloadedFiles);
                    
                    // Import the downloaded FASTQ files as sequence lists
                    progressListener.setMessage(String.format("Importing FASTQ files for %s...", accession));
                    
                    List<AnnotatedPluginDocument> imported = importFastqAsSequenceList(downloadedFiles, accession, sraRecord);
                    importedDocuments.addAll(imported);
                    
                } catch (Exception e) {
                    throw new DocumentOperationException("Failed to download SRA data for " + accession + ": " + e.getMessage(), e);
                }
            }
            
            progressListener.setMessage(String.format("Successfully imported %d sequence list(s)", importedDocuments.size()));
            progressListener.setProgress(1.0);
            
            return importedDocuments;
            
        } finally {
            // Always cleanup temporary files
            cleanupTempFiles(tempFilesToCleanup);
            
            // Also try to delete the temp directory itself
            if (outputDirectory != null && outputDirectory.exists()) {
                outputDirectory.delete();
            }
        }
    }
    
    /**
     * Download SRA data using fasterq-dump
     */
    private List<File> downloadSraData(String accession, File outputDir, boolean splitFiles, 
            FasterqDumpBinaryManager binaryManager, ProgressListener progressListener, 
            double baseProgress, double targetProgress) throws DocumentOperationException {
        
        List<File> downloadedFiles = new ArrayList<>();
        
        try {
            File binary = binaryManager.getBinary();
            
            // Build fasterq-dump command
            List<String> command = new ArrayList<>();
            command.add(binary.getAbsolutePath());
            command.add(accession);
            command.add("--outdir");
            command.add(outputDir.getAbsolutePath());
            
            // Explicitly request FASTQ format (with quality scores)
            command.add("--format");
            command.add("fastq");
            
            if (splitFiles) {
                command.add("--split-files");
            }
            
            // Add additional options for better performance and reliability
            command.add("--progress");
            command.add("--details");
            
            // Skip technical reads (like barcodes) and only get biological reads
            command.add("--skip-technical");
            
            // Log the full command for debugging
            System.out.println("Executing command: " + String.join(" ", command));
            System.out.println("Working directory: " + outputDir.getAbsolutePath());
            
            progressListener.setMessage(String.format("Downloading %s with fasterq-dump...", accession));
            
            // Execute the command
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(outputDir);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // Monitor process output for progress and errors
            AtomicBoolean processCompleted = new AtomicBoolean(false);
            StringBuilder outputLog = new StringBuilder();
            
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                        outputLog.append(line).append("\n");
                        
                        // Update progress message based on fasterq-dump output
                        if (line.contains("spots read") || line.contains("reads read")) {
                            progressListener.setMessage(String.format("%s: %s", accession, line.trim()));
                        }
                    }
                } catch (IOException e) {
                    // Ignore IO exceptions during process monitoring
                } finally {
                    processCompleted.set(true);
                }
            });
            
            outputReader.start();
            
            // Wait for process to complete with timeout
            int exitCode;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
                throw new DocumentOperationException("Download was interrupted");
            } finally {
                outputReader.interrupt();
            }
            
            if (exitCode != 0) {
                String errorMessage = String.format("fasterq-dump failed for %s (exit code: %d)\nOutput: %s", 
                        accession, exitCode, outputLog.toString());
                System.err.println(errorMessage);
                
                // Check if it's a network/download error
                String output = outputLog.toString().toLowerCase();
                if (output.contains("timeout") || output.contains("network") || output.contains("connection")) {
                    throw new DocumentOperationException("Network error downloading " + accession + ". Please check your internet connection and try again.");
                } else if (output.contains("not found") || output.contains("invalid")) {
                    throw new DocumentOperationException("SRA accession " + accession + " was not found or is invalid.");
                } else if (output.contains("permission") || output.contains("access")) {
                    throw new DocumentOperationException("Permission denied accessing SRA data. The dataset may be restricted.");
                }
                throw new DocumentOperationException(errorMessage);
            }
            
            // Find downloaded files
            downloadedFiles = findDownloadedFiles(accession, outputDir, splitFiles);
            
            if (downloadedFiles.isEmpty()) {
                // Log directory contents for debugging
                System.err.println("No FASTQ files found for " + accession + " in " + outputDir.getAbsolutePath());
                File[] files = outputDir.listFiles();
                if (files != null) {
                    System.err.println("Directory contents:");
                    for (File f : files) {
                        System.err.println("  " + f.getName() + " (size: " + f.length() + ")");
                    }
                }
                
                // Check if fasterq-dump created cache files instead
                File sraFile = new File(outputDir, accession);
                if (sraFile.exists() && sraFile.isDirectory()) {
                    throw new DocumentOperationException("fasterq-dump created cache directory instead of FASTQ files. This may indicate incomplete download or SRA toolkit configuration issues.");
                }
                
                throw new DocumentOperationException("No FASTQ files were created by fasterq-dump for " + accession + 
                    ". Output: " + outputLog.toString());
            }
            
            // Verify the files are actually FASTQ format (not FASTA)
            for (File file : downloadedFiles) {
                if (!verifyFastqFormat(file)) {
                    System.err.println("WARNING: File " + file.getName() + " appears to be FASTA format, not FASTQ!");
                    System.err.println("This SRA accession may not have quality scores available.");
                    // Continue anyway - Geneious will handle it as FASTA
                }
            }
            
            progressListener.setProgress(targetProgress);
            
            return downloadedFiles;
            
        } catch (IOException e) {
            throw new DocumentOperationException("Failed to execute fasterq-dump: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verify if a file is in FASTQ format (has quality scores) vs FASTA format
     */
    private boolean verifyFastqFormat(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return false;
            }
            
            // FASTQ starts with @, FASTA starts with >
            if (firstLine.startsWith(">")) {
                return false; // This is FASTA
            }
            
            if (!firstLine.startsWith("@")) {
                return false; // Not FASTQ either
            }
            
            // Read next 3 lines to verify FASTQ structure
            String seqLine = reader.readLine();
            String plusLine = reader.readLine();
            String qualLine = reader.readLine();
            
            if (seqLine == null || plusLine == null || qualLine == null) {
                return false;
            }
            
            // Check if plus line starts with +
            if (!plusLine.startsWith("+")) {
                return false;
            }
            
            // Check if quality line exists and has same length as sequence
            if (qualLine.length() != seqLine.length()) {
                return false;
            }
            
            // Check if quality line contains valid quality characters (ASCII 33-126)
            for (char c : qualLine.toCharArray()) {
                if (c < 33 || c > 126) {
                    return false;
                }
            }
            
            return true; // This is valid FASTQ
            
        } catch (IOException e) {
            System.err.println("Error verifying file format for " + file.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find the FASTQ files created by fasterq-dump
     */
    private List<File> findDownloadedFiles(String accession, File outputDir, boolean splitFiles) {
        List<File> files = new ArrayList<>();
        
        if (splitFiles) {
            // Look for paired-end files: accession_1.fastq and accession_2.fastq
            File file1 = new File(outputDir, accession + "_1.fastq");
            File file2 = new File(outputDir, accession + "_2.fastq");
            
            if (file1.exists()) {
                files.add(file1);
            }
            if (file2.exists()) {
                files.add(file2);
            }
            
            // If no split files found, look for single file
            if (files.isEmpty()) {
                File singleFile = new File(outputDir, accession + ".fastq");
                if (singleFile.exists()) {
                    files.add(singleFile);
                }
            }
        } else {
            // Look for single file: accession.fastq
            File singleFile = new File(outputDir, accession + ".fastq");
            if (singleFile.exists()) {
                files.add(singleFile);
            }
        }
        
        return files;
    }
    
    /**
     * Import FASTQ files using Geneious's native importer to preserve quality scores
     */
    private List<AnnotatedPluginDocument> importFastqAsSequenceList(List<File> fastqFiles, String accession, SraRecord sraRecord) 
            throws IOException, DocumentOperationException {
        
        List<AnnotatedPluginDocument> documents = new ArrayList<>();
        
        if (fastqFiles.isEmpty()) {
            throw new DocumentOperationException("No FASTQ files to import");
        }
        
        // Check if we have paired-end data
        boolean isPairedEnd = fastqFiles.size() == 2 && 
                             fastqFiles.get(0).getName().contains("_1") && 
                             fastqFiles.get(1).getName().contains("_2");
        
        // Verify files are FASTQ format before importing
        boolean hasQualityScores = true;
        for (File file : fastqFiles) {
            if (!verifyFastqFormat(file)) {
                System.out.println("WARNING: File " + file.getName() + " appears to be FASTA format without quality scores");
                hasQualityScores = false;
            } else {
                System.out.println("Verified " + file.getName() + " is valid FASTQ with quality scores");
            }
        }
        
        // Create import options to force FASTQ interpretation
        Map<String, String> importOptions = new HashMap<>();
        if (hasQualityScores) {
            // Force FASTQ format if we verified quality scores exist
            importOptions.put("format", "fastq");
            importOptions.put("quality_type", "sanger"); // Standard Phred+33 encoding
        }
        
        try {
            if (isPairedEnd) {
                // For paired-end, we need to import both files and merge them
                File forwardFile = fastqFiles.get(0).getName().contains("_1") ? fastqFiles.get(0) : fastqFiles.get(1);
                File reverseFile = fastqFiles.get(0).getName().contains("_2") ? fastqFiles.get(0) : fastqFiles.get(1);
                
                // Use Geneious's native FASTQ importer with explicit format options
                List<AnnotatedPluginDocument> forwardDocs;
                List<AnnotatedPluginDocument> reverseDocs;
                try {
                    // Try to get the FASTQ-specific importer
                    DocumentFileImporter fastqImporter = PluginUtilities.getDocumentFileImporter("com.biomatters.plugins.fileimportexport.fastq.FastqImporterPlugin");
                    
                    if (fastqImporter != null && hasQualityScores) {
                        // Use FASTQ-specific importer if available
                        System.out.println("Using FASTQ-specific importer for quality score preservation");
                        SimpleImportCallback forwardCallback = new SimpleImportCallback();
                        SimpleImportCallback reverseCallback = new SimpleImportCallback();
                        
                        fastqImporter.importDocuments(forwardFile, forwardCallback, ProgressListener.EMPTY);
                        fastqImporter.importDocuments(reverseFile, reverseCallback, ProgressListener.EMPTY);
                        forwardDocs = forwardCallback.getDocuments();
                        reverseDocs = reverseCallback.getDocuments();
                    } else {
                        // Fall back to general importer with options
                        forwardDocs = PluginUtilities.importDocuments(forwardFile, importOptions, ProgressListener.EMPTY);
                        reverseDocs = PluginUtilities.importDocuments(reverseFile, importOptions, ProgressListener.EMPTY);
                    }
                } catch (DocumentImportException e) {
                    throw new DocumentOperationException("Failed to import FASTQ files: " + e.getMessage(), e);
                }
                
                // Extract sequences from imported documents
                List<NucleotideSequenceDocument> forwardSeqs = new ArrayList<>();
                List<NucleotideSequenceDocument> reverseSeqs = new ArrayList<>();
                
                for (AnnotatedPluginDocument doc : forwardDocs) {
                    if (doc.getDocument() instanceof SequenceListDocument) {
                        SequenceListDocument list = (SequenceListDocument) doc.getDocument();
                        forwardSeqs.addAll(list.getNucleotideSequences());
                    } else if (doc.getDocument() instanceof NucleotideSequenceDocument) {
                        forwardSeqs.add((NucleotideSequenceDocument) doc.getDocument());
                    }
                }
                
                for (AnnotatedPluginDocument doc : reverseDocs) {
                    if (doc.getDocument() instanceof SequenceListDocument) {
                        SequenceListDocument list = (SequenceListDocument) doc.getDocument();
                        reverseSeqs.addAll(list.getNucleotideSequences());
                    } else if (doc.getDocument() instanceof NucleotideSequenceDocument) {
                        reverseSeqs.add((NucleotideSequenceDocument) doc.getDocument());
                    }
                }
                
                // Ensure we have the same number of sequences in both files
                if (forwardSeqs.size() != reverseSeqs.size()) {
                    throw new DocumentOperationException(String.format(
                        "Paired-end files have different numbers of sequences: %d in %s, %d in %s",
                        forwardSeqs.size(), forwardFile.getName(),
                        reverseSeqs.size(), reverseFile.getName()));
                }
                
                // Interleave the sequences: forward1, reverse1, forward2, reverse2, ...
                List<SequenceDocument> allSeqs = new ArrayList<>();
                for (int i = 0; i < forwardSeqs.size(); i++) {
                    // Add read direction suffix to names for clarity
                    NucleotideSequenceDocument fwd = forwardSeqs.get(i);
                    NucleotideSequenceDocument rev = reverseSeqs.get(i);
                    
                    // Clone sequences to modify names if needed
                    if (!fwd.getName().endsWith("/1") && !fwd.getName().endsWith("/R1")) {
                        if (fwd instanceof DefaultNucleotideSequence) {
                            ((DefaultNucleotideSequence) fwd).setName(fwd.getName() + "/1");
                        } else if (fwd instanceof DefaultNucleotideGraphSequence) {
                            ((DefaultNucleotideGraphSequence) fwd).setName(fwd.getName() + "/1");
                        }
                    }
                    if (!rev.getName().endsWith("/2") && !rev.getName().endsWith("/R2")) {
                        if (rev instanceof DefaultNucleotideSequence) {
                            ((DefaultNucleotideSequence) rev).setName(rev.getName() + "/2");
                        } else if (rev instanceof DefaultNucleotideGraphSequence) {
                            ((DefaultNucleotideGraphSequence) rev).setName(rev.getName() + "/2");
                        }
                    }
                    
                    allSeqs.add(fwd);
                    allSeqs.add(rev);
                }
                
                // Use DefaultSequenceListDocument for viewer compatibility
                List<NucleotideSequenceDocument> nucleotideSeqs = new ArrayList<>();
                List<AminoAcidSequenceDocument> aminoAcidSeqs = new ArrayList<>();
                
                for (SequenceDocument seq : allSeqs) {
                    if (seq instanceof NucleotideSequenceDocument) {
                        nucleotideSeqs.add((NucleotideSequenceDocument) seq);
                    } else if (seq instanceof AminoAcidSequenceDocument) {
                        aminoAcidSeqs.add((AminoAcidSequenceDocument) seq);
                    }
                }
                
                // Create the default sequence list with metadata in the name
                DefaultSequenceListDocument sequenceList;
                if (!nucleotideSeqs.isEmpty() && !aminoAcidSeqs.isEmpty()) {
                    sequenceList = DefaultSequenceListDocument.forBothSequenceTypes(nucleotideSeqs, aminoAcidSeqs);
                } else if (!nucleotideSeqs.isEmpty()) {
                    sequenceList = DefaultSequenceListDocument.forNucleotideSequences(nucleotideSeqs);
                } else if (!aminoAcidSeqs.isEmpty()) {
                    sequenceList = DefaultSequenceListDocument.forAminoAcidSequences(aminoAcidSeqs);
                } else {
                    throw new DocumentOperationException("No valid sequences found");
                }
                
                // Set the document name with SRA metadata
                String documentName = createDocumentName(accession, sraRecord);
                sequenceList.setName(documentName);
                
                // Create PairedReadManager using Builder for interlaced sequences
                PairedReadManager.Builder pairedReadsBuilder = new PairedReadManager.Builder();
                
                // Set up the paired read relationships for interlaced sequences
                for (int i = 0; i < allSeqs.size(); i += 2) {
                    // i is forward read (index i), i+1 is reverse read (index i+1)
                    // Forward read (at index i) has mate at index i+1
                    pairedReadsBuilder.addSequenceWithMate(i + 1, 300);
                    // Reverse read (at index i+1) has mate at index i  
                    pairedReadsBuilder.addSequenceWithMate(i, -300); // Negative distance indicates mate is to the left
                }
                
                // Build and configure the PairedReadManager
                PairedReadManager pairedReadManager = pairedReadsBuilder.toPairedReadManager();
                pairedReadManager.setInterlaced(300, PairedReadManager.Orientation.ForwardReverse);
                
                // Associate the PairedReadManager with the sequence list document
                sequenceList.setPairedReadsManager(pairedReadManager);
                
                // Create annotated document with the sequence list
                AnnotatedPluginDocument annotatedDoc = DocumentUtilities.createAnnotatedPluginDocument(sequenceList);
                
                // The name is already set on the sequence list document
                annotatedDoc.setName(documentName);
                
                documents.add(annotatedDoc);
                
            } else {
                // For single-end, just import the file(s) directly
                for (File fastqFile : fastqFiles) {
                    List<AnnotatedPluginDocument> importedDocs;
                    try {
                        // Try to use FASTQ-specific importer
                        DocumentFileImporter fastqImporter = PluginUtilities.getDocumentFileImporter("com.biomatters.plugins.fileimportexport.fastq.FastqImporterPlugin");
                        
                        if (fastqImporter != null && hasQualityScores) {
                            // Use FASTQ-specific importer if available
                            System.out.println("Using FASTQ-specific importer for single-end file: " + fastqFile.getName());
                            SimpleImportCallback callback = new SimpleImportCallback();
                            
                            fastqImporter.importDocuments(fastqFile, callback, ProgressListener.EMPTY);
                            importedDocs = callback.getDocuments();
                        } else {
                            // Fall back to general importer with options
                            importedDocs = PluginUtilities.importDocuments(fastqFile, importOptions, ProgressListener.EMPTY);
                        }
                    } catch (DocumentImportException e) {
                        throw new DocumentOperationException("Failed to import FASTQ file: " + e.getMessage(), e);
                    }
                    
                    for (AnnotatedPluginDocument doc : importedDocs) {
                        // Set document name to include SRA information
                        String documentName = createDocumentName(accession, sraRecord);
                        doc.setName(documentName);
                        
                        // Try to set the name on the underlying document too if it's a DefaultSequenceListDocument
                        if (doc.getDocument() instanceof DefaultSequenceListDocument) {
                            ((DefaultSequenceListDocument) doc.getDocument()).setName(documentName);
                        }
                        
                        documents.add(doc);
                    }
                }
            }
            
        } catch (IOException e) {
            throw new DocumentOperationException("Failed to import FASTQ files: " + e.getMessage(), e);
        }
        
        if (documents.isEmpty()) {
            throw new DocumentOperationException("No valid sequences found in FASTQ files");
        }
        
        return documents;
    }
    
    /**
     * Create a simple document name with just accession and title
     */
    private String createDocumentName(String accession, SraRecord sraRecord) {
        if (sraRecord != null && sraRecord.getTitle() != null && !sraRecord.getTitle().isEmpty()) {
            String title = sraRecord.getTitle();
            System.out.println("Creating document name with title: " + title);
            return accession + " - " + title;
        }
        System.out.println("No title found in SraRecord, using default name");
        return accession + " - NCBI SRA Dataset";
    }
    
    /**
     * Clean up temporary files
     */
    private void cleanupTempFiles(List<File> filesToDelete) {
        for (File file : filesToDelete) {
            try {
                if (file.exists()) {
                    Files.delete(file.toPath());
                }
            } catch (IOException e) {
                // Ignore cleanup errors - log if logger available
                System.err.println("Warning: Could not delete temporary file: " + file.getAbsolutePath());
            }
        }
    }
    
    /**
     * Simple implementation of ImportCallback for collecting imported documents
     */
    private static class SimpleImportCallback extends DocumentFileImporter.ImportCallback {
        private final List<AnnotatedPluginDocument> documents = new ArrayList<>();
        
        @Override
        public AnnotatedPluginDocument addDocument(PluginDocument document) {
            AnnotatedPluginDocument doc = DocumentUtilities.createAnnotatedPluginDocument(document);
            documents.add(doc);
            return doc;
        }
        
        @Override
        public AnnotatedPluginDocument addDocument(AnnotatedPluginDocument document) {
            documents.add(document);
            return document;
        }
        
        public List<AnnotatedPluginDocument> getDocuments() {
            return documents;
        }
    }
}