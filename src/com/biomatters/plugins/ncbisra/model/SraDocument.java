package com.biomatters.plugins.ncbisra.model;

import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import java.text.NumberFormat;
import java.util.*;

/**
 * Document representing an NCBI SRA dataset reference
 * This shows metadata about the SRA record as a special sequence document
 */
public class SraDocument extends DefaultNucleotideSequence {
    
    private transient SraRecord sraRecord;
    
    // Store key metadata as document properties that survive serialization
    private static final String PROP_SRA_ACCESSION = "sra.accession";
    private static final String PROP_SRA_ORGANISM = "sra.organism";
    private static final String PROP_SRA_PLATFORM = "sra.platform";
    private static final String PROP_SRA_LIBRARY_STRATEGY = "sra.libraryStrategy";
    private static final String PROP_SRA_LIBRARY_LAYOUT = "sra.libraryLayout";
    private static final String PROP_SRA_TITLE = "sra.title";
    private static final String PROP_SRA_STUDY = "sra.study";
    private static final String PROP_SRA_BIOPROJECT = "sra.bioproject";
    private static final String PROP_SRA_BIOSAMPLE = "sra.biosample";
    private static final String PROP_SRA_TOTAL_SPOTS = "sra.totalSpots";
    private static final String PROP_SRA_TOTAL_BASES = "sra.totalBases";
    
    // Define DocumentFields for SRA metadata - these will be registered with Geneious
    public static final DocumentField FIELD_SRA_ACCESSION = DocumentField.createStringField(
        "SRA Accession", "NCBI SRA accession number", PROP_SRA_ACCESSION);
    public static final DocumentField FIELD_SRA_ORGANISM = DocumentField.createStringField(
        "SRA Organism", "Source organism", PROP_SRA_ORGANISM);
    public static final DocumentField FIELD_SRA_PLATFORM = DocumentField.createStringField(
        "SRA Platform", "Sequencing platform", PROP_SRA_PLATFORM);
    public static final DocumentField FIELD_SRA_LIBRARY_STRATEGY = DocumentField.createStringField(
        "SRA Library Strategy", "Library preparation strategy", PROP_SRA_LIBRARY_STRATEGY);
    public static final DocumentField FIELD_SRA_LIBRARY_LAYOUT = DocumentField.createStringField(
        "SRA Library Layout", "Library layout (single/paired)", PROP_SRA_LIBRARY_LAYOUT);
    public static final DocumentField FIELD_SRA_TITLE = DocumentField.createStringField(
        "SRA Title", "Experiment title", PROP_SRA_TITLE);
    public static final DocumentField FIELD_SRA_STUDY = DocumentField.createStringField(
        "SRA Study", "Study accession", PROP_SRA_STUDY);
    public static final DocumentField FIELD_SRA_BIOPROJECT = DocumentField.createStringField(
        "SRA BioProject", "BioProject accession", PROP_SRA_BIOPROJECT);
    public static final DocumentField FIELD_SRA_BIOSAMPLE = DocumentField.createStringField(
        "SRA BioSample", "BioSample accession", PROP_SRA_BIOSAMPLE);
    public static final DocumentField FIELD_SRA_TOTAL_SPOTS = DocumentField.createLongField(
        "SRA Total Spots", "Total number of spots/reads", PROP_SRA_TOTAL_SPOTS, true, false);
    public static final DocumentField FIELD_SRA_TOTAL_BASES = DocumentField.createLongField(
        "SRA Total Bases", "Total number of bases", PROP_SRA_TOTAL_BASES, true, false);
    
    // List of all SRA fields for easy access
    public static final List<DocumentField> SRA_FIELDS = Arrays.asList(
        FIELD_SRA_ACCESSION,
        FIELD_SRA_ORGANISM,
        FIELD_SRA_PLATFORM,
        FIELD_SRA_LIBRARY_STRATEGY,
        FIELD_SRA_LIBRARY_LAYOUT,
        FIELD_SRA_BIOPROJECT,
        FIELD_SRA_BIOSAMPLE,
        FIELD_SRA_STUDY,
        FIELD_SRA_TOTAL_SPOTS,
        FIELD_SRA_TOTAL_BASES
    );
    
    /**
     * Public empty constructor required by Geneious for document serialization
     */
    public SraDocument() {
        super("Unknown SRA", "SRA Dataset", "NNNNNNNNNN", new Date());
        this.sraRecord = null;
    }
    
    public SraDocument(SraRecord sraRecord) {
        super(sraRecord.getAccession() != null ? sraRecord.getAccession() : "Unknown SRA",
              createDescription(sraRecord),
              createPlaceholderSequence(sraRecord),
              sraRecord.getSubmissionDate() != null ? sraRecord.getSubmissionDate() : new Date());
        this.sraRecord = sraRecord;
        
        // Store metadata as document properties that will survive serialization
        if (sraRecord != null) {
            if (sraRecord.getAccession() != null) {
                setFieldValue(PROP_SRA_ACCESSION, sraRecord.getAccession());
            }
            if (sraRecord.getOrganism() != null) {
                setFieldValue(PROP_SRA_ORGANISM, sraRecord.getOrganism());
            }
            if (sraRecord.getPlatform() != null) {
                setFieldValue(PROP_SRA_PLATFORM, sraRecord.getPlatform());
            }
            if (sraRecord.getLibraryStrategy() != null) {
                setFieldValue(PROP_SRA_LIBRARY_STRATEGY, sraRecord.getLibraryStrategy());
            }
            if (sraRecord.getLibraryLayout() != null) {
                setFieldValue(PROP_SRA_LIBRARY_LAYOUT, sraRecord.getLibraryLayout());
            }
            if (sraRecord.getTitle() != null) {
                setFieldValue(PROP_SRA_TITLE, sraRecord.getTitle());
            }
            if (sraRecord.getStudy() != null) {
                setFieldValue(PROP_SRA_STUDY, sraRecord.getStudy());
            }
            if (sraRecord.getBioProject() != null) {
                setFieldValue(PROP_SRA_BIOPROJECT, sraRecord.getBioProject());
            }
            if (sraRecord.getBioSample() != null) {
                setFieldValue(PROP_SRA_BIOSAMPLE, sraRecord.getBioSample());
            }
            if (sraRecord.getTotalSpots() > 0) {
                setFieldValue(PROP_SRA_TOTAL_SPOTS, sraRecord.getTotalSpots());
            }
            if (sraRecord.getTotalBases() > 0) {
                setFieldValue(PROP_SRA_TOTAL_BASES, sraRecord.getTotalBases());
            }
        }
    }
    
    private static String createDescription(SraRecord sraRecord) {
        StringBuilder desc = new StringBuilder();
        if (sraRecord.getTitle() != null && !sraRecord.getTitle().isEmpty()) {
            desc.append(sraRecord.getTitle()).append("\n\n");
        }
        
        desc.append("NCBI SRA Dataset\n");
        desc.append("Accession: ").append(sraRecord.getAccession()).append("\n");
        
        if (sraRecord.getOrganism() != null) {
            desc.append("Organism: ").append(sraRecord.getOrganism()).append("\n");
        }
        if (sraRecord.getPlatform() != null) {
            desc.append("Platform: ").append(sraRecord.getPlatform()).append("\n");
        }
        if (sraRecord.getLibraryStrategy() != null) {
            desc.append("Library Strategy: ").append(sraRecord.getLibraryStrategy()).append("\n");
        }
        if (sraRecord.getLibraryLayout() != null) {
            desc.append("Library Layout: ").append(sraRecord.getLibraryLayout()).append("\n");
        }
        if (sraRecord.getTotalSpots() > 0) {
            desc.append("Total Spots: ").append(NumberFormat.getInstance().format(sraRecord.getTotalSpots())).append("\n");
        }
        if (sraRecord.getTotalBases() > 0) {
            desc.append("Total Bases: ").append(NumberFormat.getInstance().format(sraRecord.getTotalBases())).append("\n");
        }
        if (sraRecord.getStudy() != null) {
            desc.append("Study: ").append(sraRecord.getStudy()).append("\n");
        }
        if (sraRecord.getBioProject() != null) {
            desc.append("BioProject: ").append(sraRecord.getBioProject()).append("\n");
        }
        if (sraRecord.getBioSample() != null) {
            desc.append("BioSample: ").append(sraRecord.getBioSample()).append("\n");
        }
        
        desc.append("\nThis is an SRA dataset reference. Use 'Download FASTQ' to obtain the actual sequence data.");
        
        return desc.toString();
    }
    
    private static String createPlaceholderSequence(SraRecord sraRecord) {
        // Create a valid nucleotide sequence placeholder
        // Just use N's to represent unknown/placeholder sequence
        // The actual metadata is in the description field
        return "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";
    }
    
    /**
     * Get the underlying SRA record
     * If the record is not available (e.g., after deserialization), it reconstructs a basic one from stored properties
     */
    public SraRecord getSraRecord() {
        if (sraRecord == null) {
            // Try to reconstruct from stored properties
            String accession = (String) getFieldValue(PROP_SRA_ACCESSION);
            if (accession == null) {
                // Fall back to document name
                accession = getName();
            }
            
            if (accession != null && !"Unknown SRA".equals(accession)) {
                SraRecord reconstructed = new SraRecord();
                reconstructed.setAccession(accession);
                
                // Restore other properties if available
                Object organism = getFieldValue(PROP_SRA_ORGANISM);
                if (organism != null) reconstructed.setOrganism(organism.toString());
                
                Object platform = getFieldValue(PROP_SRA_PLATFORM);
                if (platform != null) reconstructed.setPlatform(platform.toString());
                
                Object libraryStrategy = getFieldValue(PROP_SRA_LIBRARY_STRATEGY);
                if (libraryStrategy != null) reconstructed.setLibraryStrategy(libraryStrategy.toString());
                
                Object libraryLayout = getFieldValue(PROP_SRA_LIBRARY_LAYOUT);
                if (libraryLayout != null) reconstructed.setLibraryLayout(libraryLayout.toString());
                
                Object title = getFieldValue(PROP_SRA_TITLE);
                if (title != null) reconstructed.setTitle(title.toString());
                
                Object study = getFieldValue(PROP_SRA_STUDY);
                if (study != null) reconstructed.setStudy(study.toString());
                
                Object bioProject = getFieldValue(PROP_SRA_BIOPROJECT);
                if (bioProject != null) reconstructed.setBioProject(bioProject.toString());
                
                Object bioSample = getFieldValue(PROP_SRA_BIOSAMPLE);
                if (bioSample != null) reconstructed.setBioSample(bioSample.toString());
                
                Object totalSpots = getFieldValue(PROP_SRA_TOTAL_SPOTS);
                if (totalSpots != null) {
                    try {
                        reconstructed.setTotalSpots(Long.parseLong(totalSpots.toString()));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                
                Object totalBases = getFieldValue(PROP_SRA_TOTAL_BASES);
                if (totalBases != null) {
                    try {
                        reconstructed.setTotalBases(Long.parseLong(totalBases.toString()));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                
                // Cache the reconstructed record
                this.sraRecord = reconstructed;
                return reconstructed;
            }
        }
        return sraRecord;
    }
    
    /**
     * Check if this document has a valid SRA record
     */
    public boolean hasSraRecord() {
        return getSraRecord() != null;
    }
    
    public String getDocumentType() {
        return "SRA Dataset";
    }
    
    /**
     * Get the SRA accession for this document
     */
    public String getSraAccession() {
        // Try from cached record first
        if (sraRecord != null && sraRecord.getAccession() != null) {
            return sraRecord.getAccession();
        }
        
        // Try from stored property
        Object accession = getFieldValue(PROP_SRA_ACCESSION);
        if (accession != null) {
            return accession.toString();
        }
        
        // Fall back to document name if it's not the default
        String name = getName();
        if (name != null && !"Unknown SRA".equals(name)) {
            return name;
        }
        
        return null;
    }
    
    /**
     * Override to provide SRA-specific metadata fields to Geneious
     * This makes the fields visible and searchable in the UI
     */
    @Override
    public List<DocumentField> getDisplayableFields() {
        // Get the parent fields first
        List<DocumentField> fields = new ArrayList<>(super.getDisplayableFields());
        
        // Add our SRA-specific fields
        fields.addAll(SRA_FIELDS);
        
        return fields;
    }
    
    /**
     * Override to provide values for our custom fields
     */
    @Override
    public Object getFieldValue(String fieldCode) {
        // First try parent implementation
        Object value = super.getFieldValue(fieldCode);
        if (value != null) {
            return value;
        }
        
        // Handle SRA-specific fields
        SraRecord record = getSraRecord();
        if (record == null) {
            return null;
        }
        
        switch (fieldCode) {
            case PROP_SRA_ACCESSION:
                return record.getAccession();
            case PROP_SRA_ORGANISM:
                return record.getOrganism();
            case PROP_SRA_PLATFORM:
                return record.getPlatform();
            case PROP_SRA_LIBRARY_STRATEGY:
                return record.getLibraryStrategy();
            case PROP_SRA_LIBRARY_LAYOUT:
                return record.getLibraryLayout();
            case PROP_SRA_TITLE:
                return record.getTitle();
            case PROP_SRA_STUDY:
                return record.getStudy();
            case PROP_SRA_BIOPROJECT:
                return record.getBioProject();
            case PROP_SRA_BIOSAMPLE:
                return record.getBioSample();
            case PROP_SRA_TOTAL_SPOTS:
                return record.getTotalSpots() > 0 ? (int)record.getTotalSpots() : null;
            case PROP_SRA_TOTAL_BASES:
                return record.getTotalBases() > 0 ? record.getTotalBases() : null;
            default:
                return null;
        }
    }
}