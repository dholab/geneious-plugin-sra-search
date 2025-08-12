package com.biomatters.plugins.ncbisra.service;

import com.biomatters.geneious.publicapi.databaseservice.*;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.ActionProvider;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.plugins.ncbisra.model.SraDocument;
import com.biomatters.geneious.publicapi.plugin.Icons;
import com.biomatters.geneious.publicapi.utilities.StandardIcons;
import com.biomatters.plugins.ncbisra.api.NcbiEUtilsClient;
import com.biomatters.plugins.ncbisra.binary.FasterqDumpBinaryManager;
import com.biomatters.plugins.ncbisra.model.SraRecord;
import com.biomatters.plugins.ncbisra.model.SraSearchResult;
import com.biomatters.plugins.ncbisra.operations.SraDownloadOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/**
 * Simplified DatabaseService implementation for NCBI SRA search
 */
public class NcbiSraDatabaseServiceSimple extends DatabaseService {
    
    private static final String SERVICE_NAME = "NCBI SRA";
    private static final String SERVICE_DESCRIPTION = "Search and download NCBI Sequence Read Archive datasets";
    private static final String SERVICE_HELP = "Search the NCBI SRA database and download sequence data directly using fasterq-dump";
    private static final String UNIQUE_ID = "ncbi_sra_service";
    
    private final NcbiEUtilsClient ncbiClient;
    private FasterqDumpBinaryManager binaryManager;
    
    public NcbiSraDatabaseServiceSimple() {
        this.ncbiClient = new NcbiEUtilsClient();
        // Don't initialize binary manager in constructor to avoid blocking
        // It will be initialized lazily when needed
    }
    
    @Override
    public String getName() {
        return SERVICE_NAME;
    }
    
    @Override
    public String getDescription() {
        return SERVICE_DESCRIPTION;
    }
    
    @Override
    public String getHelp() {
        return SERVICE_HELP;
    }
    
    @Override
    public String getUniqueID() {
        return UNIQUE_ID;
    }
    
    @Override
    public Icons getIcons() {
        // Use the standard database icon for consistency with other database services
        return StandardIcons.database.getIcons();
    }
    
    @Override
    public QueryField[] getSearchFields() {
        // Basic search field
        DocumentField basicSearchField = DocumentField.createStringField(
                "search", "Search All", "Search SRA by accession, organism, or free text");
        
        // Advanced search fields
        DocumentField accessionField = DocumentField.createStringField(
                "accession", "SRA Accession", "Search by specific SRA/ENA/DRA accession (e.g. SRR123456, ERR123456)");
        
        DocumentField bioprojectField = DocumentField.createStringField(
                "bioproject", "BioProject ID", "Search by BioProject accession (e.g. PRJNA123456)");
        
        DocumentField biosampleField = DocumentField.createStringField(
                "biosample", "BioSample ID", "Search by BioSample accession (e.g. SAMN12345678)");
        
        DocumentField organismField = DocumentField.createStringField(
                "organism", "Organism", "Search by organism/species name (e.g. Homo sapiens, E. coli)");
        
        DocumentField libraryStrategyField = DocumentField.createStringField(
                "library_strategy", "Library Strategy", "Search by sequencing strategy (e.g. RNA-Seq, WGS, ChIP-Seq, ATAC-seq)");
        
        DocumentField platformField = DocumentField.createStringField(
                "platform", "Sequencing Platform", "Search by sequencing platform (e.g. Illumina, PacBio, Nanopore)");
        
        DocumentField librarySourceField = DocumentField.createStringField(
                "library_source", "Library Source", "Search by library source (e.g. GENOMIC, TRANSCRIPTOMIC, METAGENOMIC)");
        
        return new QueryField[] {
            new QueryField(basicSearchField, new Condition[] { Condition.CONTAINS }),
            new QueryField(accessionField, new Condition[] { Condition.EQUAL, Condition.CONTAINS }),
            new QueryField(bioprojectField, new Condition[] { Condition.EQUAL, Condition.CONTAINS }),
            new QueryField(biosampleField, new Condition[] { Condition.EQUAL, Condition.CONTAINS }),
            new QueryField(organismField, new Condition[] { Condition.CONTAINS }),
            new QueryField(libraryStrategyField, new Condition[] { Condition.CONTAINS, Condition.EQUAL }),
            new QueryField(platformField, new Condition[] { Condition.CONTAINS, Condition.EQUAL }),
            new QueryField(librarySourceField, new Condition[] { Condition.CONTAINS, Condition.EQUAL })
        };
    }
    
    public boolean isAvailable() {
        // For now, just check if we can create the client
        return ncbiClient != null;
    }
    
    public String getUnavailableReason() {
        if (!isAvailable()) {
            return "NCBI SRA service is not available";
        }
        return null;
    }
    
    @Override
    public void retrieve(Query query, RetrieveCallback callback, URN[] urnsToNotRetrieve) throws DatabaseServiceException {
        try {
            String searchTerm = buildSearchQuery(query);
            
            if (searchTerm.isEmpty()) {
                // No search term provided - return empty results
                return;
            }
            
            // Perform search - get up to 10000 results (NCBI max)
            // Most searches will return far fewer, but this ensures we get all available results
            SraSearchResult searchResult = ncbiClient.search(searchTerm, 0, 10000);
            
            if (searchResult.getRecords() == null || searchResult.getRecords().isEmpty()) {
                // No records found - return empty results
                return;
            }
            
            // Process each SRA record
            for (SraRecord sraRecord : searchResult.getRecords()) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new DatabaseServiceException.Canceled();
                }
                
                // Create document for search results with rich metadata
                AnnotatedPluginDocument mockDocument = createDocumentFromSraRecord(sraRecord);
                callback.add(mockDocument, java.util.Collections.<String,Object>emptyMap());
            }
            
        } catch (IOException e) {
            throw new DatabaseServiceException("Error searching NCBI SRA: " + e.getMessage(), false);
        }
    }
    
    // Note: Custom column display and action providers are not available in this API version
    // The enhanced search fields will still work for better query building
    
    /**
     * Build a proper NCBI search query from the Geneious query object
     */
    private String buildSearchQuery(Query query) {
        if (query instanceof BasicSearchQuery) {
            BasicSearchQuery basicQuery = (BasicSearchQuery) query;
            String searchText = basicQuery.getSearchText();
            return searchText != null ? searchText.trim() : "";
        } else if (query instanceof AdvancedSearchQueryTerm) {
            AdvancedSearchQueryTerm advancedQuery = (AdvancedSearchQueryTerm) query;
            return buildFieldSpecificQuery(advancedQuery);
        } else if (query instanceof CompoundSearchQuery) {
            CompoundSearchQuery compoundQuery = (CompoundSearchQuery) query;
            return buildCompoundQuery(compoundQuery);
        }
        return "";
    }
    
    /**
     * Build field-specific search queries for advanced search
     */
    private String buildFieldSpecificQuery(AdvancedSearchQueryTerm queryTerm) {
        String fieldCode = queryTerm.getField().getCode();
        Object[] values = queryTerm.getValues();
        if (values == null || values.length == 0 || values[0] == null) {
            return "";
        }
        
        String value = values[0].toString().trim();
        if (value.isEmpty()) {
            return "";
        }
        
        // Map Geneious field codes to NCBI search field tags
        switch (fieldCode) {
            case "search":
                return value; // Basic search, no field tag
            case "accession":
                return value + "[Accession]";
            case "bioproject":
                return value + "[Bioproject]";
            case "biosample":
                return value + "[Biosample]";
            case "organism":
                return value + "[Organism]";
            case "library_strategy":
                return value + "[Strategy]";
            case "platform":
                return value + "[Platform]";
            case "library_source":
                return value + "[Source]";
            default:
                return value; // Fallback to basic search
        }
    }
    
    /**
     * Build compound queries with AND/OR operators
     */
    private String buildCompoundQuery(CompoundSearchQuery compoundQuery) {
        List<? extends Query> children = compoundQuery.getChildren();
        if (children == null || children.isEmpty()) {
            return "";
        }
        
        List<String> terms = new ArrayList<>();
        for (Query childQuery : children) {
            String term = buildSearchQuery(childQuery);
            if (!term.isEmpty()) {
                terms.add(term);
            }
        }
        
        if (terms.isEmpty()) {
            return "";
        }
        
        String operator = compoundQuery.getOperator() == CompoundSearchQuery.Operator.AND ? " AND " : " OR ";
        return String.join(operator, terms);
    }
    
    private AnnotatedPluginDocument createDocumentFromSraRecord(SraRecord record) {
        // Create an SRA document that represents the dataset metadata
        SraDocument sraDoc = new SraDocument(record);
        
        // Create annotated plugin document
        AnnotatedPluginDocument doc = DocumentUtilities.createAnnotatedPluginDocument(sraDoc);
        
        // The metadata is already in the SraDocument description
        // which will be displayed in Geneious
        
        return doc;
    }
}