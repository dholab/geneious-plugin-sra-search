# SRA Metadata Display in Geneious

## Current Implementation

The NCBI SRA Search plugin displays SRA metadata in the document name of downloaded sequence lists. The plugin uses standard Geneious `DefaultSequenceListDocument` for full compatibility with the sequence viewer.

### What Works

1. **Document Name** - Contains SRA accession and title
   - Format: `SRR11192680 - 16S rRNA seq of Homo sapiens: prostate cancer patient rectal sample 2`

2. **Sequence Viewer Compatibility** - Using `DefaultSequenceListDocument` directly ensures full compatibility with the Geneious Sequence Viewer without any crashes or errors

3. **Metadata Preservation** - The full SRA metadata is stored in the `SraRecord` object and displayed in:
   - Search results table (all fields visible as columns)
   - The original SRA document before download

### Technical Details

1. **Document Creation** (`/src/com/biomatters/plugins/ncbisra/operations/SraDownloadOperation.java`)
   - Uses `DefaultSequenceListDocument.forNucleotideSequences()` for sequence lists
   - Sets document name using `createDocumentName()` method
   - Handles both paired-end and single-end sequences

2. **Metadata Available in SraRecord**
   - Organism
   - Platform
   - Library Strategy
   - Library Layout
   - Total Spots
   - Total Bases
   - Study
   - BioProject
   - BioSample

### Code Cleanup Performed

1. **Removed Unused Code**:
   - Removed `SraSequenceListDocumentV2.java` - custom document type that caused viewer crashes
   - Removed `SraSequenceListDocument.java` - earlier unused attempt
   - Removed unused `createSraDescription()` method (69 lines)
   - Removed unused imports

2. **Fixed Issues**:
   - Removed unused `ThreadUtilities` import from `NcbiEUtilsClient.java`
   - Cleaned up `NcbiSraSearchPlugin.java` to remove references to non-existent classes
   - Reverted to using standard Geneious document types

### Build Status

The plugin compiles successfully with no errors and only standard Java 8 deprecation warnings.

## Summary

The plugin successfully downloads SRA data and displays it in Geneious with metadata in the document name. While the description field cannot be modified due to Geneious API limitations, the current implementation provides a stable, working solution that avoids viewer crashes while still providing essential metadata visibility.