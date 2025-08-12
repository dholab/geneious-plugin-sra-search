# NCBI SRA Search Plugin for Geneious Prime

A Geneious Prime plugin that enables direct searching and downloading of NCBI Sequence Read Archive (SRA) datasets within the Geneious interface.

## Features

- **Integrated SRA Search**: Search NCBI SRA directly from Geneious using accession numbers, organism names, or other search terms
- **Direct Download**: Download SRA datasets as FASTQ files using the bundled `fasterq-dump` tool
- **Paired-End Support**: Automatically detects and properly configures paired-end reads
- **Quality Score Preservation**: Downloads include quality scores when available
- **Metadata Display**: Shows comprehensive SRA metadata including organism, platform, library strategy, and more
- **Progress Tracking**: Real-time progress updates during download with cancellation support
- **Cross-Platform**: Includes binaries for Windows, macOS, and Linux

## Requirements

- Geneious Prime 2024.0 or later
- Internet connection for searching and downloading SRA data
- Sufficient disk space for temporary files during download

## Installation

1. Download the latest `NcbiSraSearch.gplugin` from the [Releases](https://github.com/dholab/geneious-plugin-sra-search/releases) page
2. In Geneious Prime, go to **Tools → Plugins**
3. Click **Install plugin from a gplugin file** 
4. Select the downloaded `NcbiSraSearch.gplugin` file
5. Restart Geneious Prime

## Usage

### Searching SRA

1. In the Sources panel, you'll see "NCBI SRA Search" with a database icon
2. Click on it to activate the search interface
3. Enter your search terms:
   - SRA accession (e.g., `SRR11192680`)
   - Organism name (e.g., `Homo sapiens`)
   - Study or project identifiers
   - Any NCBI SRA search query
4. Click Search or press Enter

### Downloading SRA Data

1. Select one or more SRA records from the search results
2. Click "Download FASTQ Data" or right-click and select the option
3. The plugin will:
   - Download the raw sequence data using `fasterq-dump`
   - Import as FASTQ sequence lists
   - Configure paired reads if applicable
   - Display metadata in the document name

## Technical Details

### Bundled Binaries

The plugin includes pre-compiled `fasterq-dump` binaries:
- **Windows**: `fasterq-dump.exe` (2.11.3)
- **macOS**: `fasterq-dump` (Universal binary)
- **Linux**: `fasterq-dump` (x86_64)

### API Integration

Uses NCBI E-utilities API for searching:
- ESearch for query execution
- ESummary for retrieving metadata
- Respects NCBI rate limits (3 requests/second)

### Build Requirements

To build from source:
- Java 8 or later
- Apache Ant
- Geneious Plugin Development Kit

```bash
# Build the plugin
ant clean
ant

# The plugin will be created as build/NcbiSraSearch.gplugin
```

## Known Limitations

- Some older SRA datasets may not have quality scores available
- Large datasets may require significant temporary disk space
- Download speed depends on NCBI servers and internet connection

## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## License

This plugin is provided as-is for research use. The included `fasterq-dump` binaries are from the NCBI SRA Toolkit, which is in the public domain.

## Acknowledgments

- NCBI for the SRA database and toolkit
- Biomatters/Geneious for the plugin API
- The scientific community for making sequence data publicly available

## Support

For issues or questions:
- Open an issue on [GitHub](https://github.com/dholab/geneious-plugin-sra-search/issues)
- Check the [documentation](docs/) for detailed information

## Version History

- **1.0.0** - Initial release
  - Basic SRA search functionality
  - FASTQ download with quality scores
  - Paired-end read support
  - Cross-platform compatibility