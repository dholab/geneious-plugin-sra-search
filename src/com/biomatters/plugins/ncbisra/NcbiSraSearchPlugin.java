package com.biomatters.plugins.ncbisra;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentType;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import com.biomatters.plugins.ncbisra.operations.SraDownloadOperation;
import com.biomatters.plugins.ncbisra.service.NcbiSraDatabaseServiceSimple;

/**
 * Main plugin class for NCBI SRA search functionality with integrated fasterq-dump
 */
public class NcbiSraSearchPlugin extends GeneiousPlugin {
    
    @Override
    public String getName() {
        return "NCBI SRA Search";
    }
    
    @Override
    public String getDescription() {
        return "Search and download NCBI SRA datasets directly within Geneious using bundled fasterq-dump";
    }
    
    @Override
    public String getHelp() {
        return "This plugin provides access to the NCBI Sequence Read Archive (SRA) database. " +
               "You can search for SRA datasets by accession number, organism, study, or other criteria. " +
               "The plugin includes bundled fasterq-dump binaries for downloading SRA files directly " +
               "as FASTQ sequences into Geneious. Supports both single-end and paired-end reads with " +
               "progress tracking and cancellation support.";
    }
    
    @Override
    public String getAuthors() {
        return "DHO";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getMinimumApiVersion() {
        return "4.0";
    }
    
    @Override
    public int getMaximumApiVersion() {
        return 4;
    }
    
    @Override
    public GeneiousService[] getServices() {
        return new GeneiousService[] {
            new NcbiSraDatabaseServiceSimple()
        };
    }
    
    @Override
    public DocumentOperation[] getDocumentOperations() {
        return new DocumentOperation[] {
            new SraDownloadOperation()
        };
    }
    
    @Override
    public DocumentType[] getDocumentTypes() {
        // Return empty array - we're using standard Geneious document types now
        return new DocumentType[0];
    }
}