# NCBI SRA Search Plugin for Geneious Prime

[![Build Status](https://github.com/dholab/geneious-plugin-sra-search/workflows/Build%20Geneious%20SRA%20Search%20Plugin/badge.svg)](https://github.com/dholab/geneious-plugin-sra-search/actions)
[![Version](https://img.shields.io/badge/version-1.3.0-blue.svg)](https://github.com/dholab/geneious-plugin-sra-search/releases)
[![Geneious Prime](https://img.shields.io/badge/Geneious%20Prime-2024.0+-green.svg)](https://www.geneious.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey.svg)](#system-requirements)

One of the best parts of the LLM revolution is that it can scratch niche itches. We've used Geneious in our lab to visualize sequence data for about a decade. One of its most convenient features is the ability to search NCBI databases to retrieve sequence files from Genbank and other NCBI databases. However, Geneious has never offered a database search interface to SRA. This is a Geneious Prime plugin that enables direct searching and downloading of sequencing data from NCBI's Sequence Read Archive (SRA) without leaving the Geneious workspace.

Tested on an Apple Silicon M4 Macbook Pro and an X64 iMac Pro. It *should* work on Windows and Linux.

## Table of Contents

- [Key Features](#key-features)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Usage Guide](#usage-guide)
- [Technical Details](#technical-details)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Support](#support)
- [License](#license)

## Key Features

- 🔍 **Integrated SRA Search**: Search NCBI SRA directly from Geneious using accession numbers, organism names, or other search terms
- 🚀 **Performance Tuned**: Optimized with 8 threads and 1GB memory limit for faster downloads
- ⚡ **Two-Phase Download**: Uses prefetch + fasterq-dump for 2-3x faster downloads
- 🧬 **Paired-End Support**: Automatically detects and properly configures paired-end reads
- 📊 **Quality Score Preservation**: Downloads include quality scores when available
- 📋 **Metadata Display**: Shows comprehensive SRA metadata including organism, platform, library strategy, and more
- 📈 **Enhanced Progress Tracking**: Real-time progress updates with detailed phase information (download → conversion)
- 🌍 **Cross-Platform**: Includes binaries for Windows, macOS, and Linux

## System Requirements

### Minimum Requirements
- **Geneious Prime**: Version 2024.0 or later
- **Operating System**: 
  - Windows 10 or later (64-bit)
  - macOS 10.14 (Mojave) or later
  - Linux (64-bit distributions)
- **RAM**: 4 GB minimum
- **Storage**: Sufficient space for temporary files during download
- **Internet**: Stable connection for NCBI SRA database access

### Recommended Specifications
- **RAM**: 8 GB or more for large datasets
- **Storage**: SSD with ample free space (downloads can be large)
- **Network**: Broadband connection for faster downloads

## Installation

### Method 1: Download from Releases (Recommended)

1. Download the latest `NcbiSraSearch.gplugin` from the [Releases](https://github.com/dholab/geneious-plugin-sra-search/releases) page
2. In Geneious Prime, go to **Tools → Plugins**
3. Click **Install plugin from a gplugin file** 
4. Select the downloaded `NcbiSraSearch.gplugin` file
5. Restart Geneious Prime when prompted

### Method 2: Build from Source

```bash
# Clone the repository
git clone https://github.com/dholab/geneious-plugin-sra-search.git
cd geneious-plugin-sra-search

# Build the plugin
ant clean
ant create-plugin

# The plugin will be created as dist/NcbiSraSearch.gplugin
```

After building, follow the installation steps from Method 1 using your locally built `.gplugin` file.

## Quick Start

### Basic SRA Search and Download

1. **Access the Plugin**: In the Sources panel, locate "NCBI SRA Search" with a database icon
2. **Search for Data**: 
   - Click on the SRA Search to activate
   - Enter search terms (e.g., `SRR11192680`, `Homo sapiens`, or any NCBI query)
   - Press Enter or click Search
3. **Download Results**:
   - Select desired SRA records from results
   - Click "Download FASTQ Data"
   - Monitor progress in the Operations panel

### Example Search Queries

- **Specific Accession**: `SRR11192680`
- **Organism**: `Escherichia coli`
- **Study**: `PRJNA613958`
- **Technology**: `Illumina AND RNA-seq`
- **Date Range**: `Homo sapiens AND 2024[PDAT]`

## Usage Guide

### Searching SRA

The plugin supports all standard NCBI SRA search syntax:

1. **Simple Searches**: Enter organism names, accession numbers, or keywords
2. **Boolean Operators**: Use AND, OR, NOT for complex queries
3. **Field Tags**: Use `[ORGN]`, `[ACCN]`, `[TITL]` for specific field searches
4. **Wildcards**: Use `*` for partial matches

### Understanding Search Results

Results display includes:
- **Accession**: SRA run accession number
- **Title**: Experiment title
- **Organism**: Source organism
- **Platform**: Sequencing platform used
- **Layout**: Single or paired-end
- **Spots**: Number of reads
- **Bases**: Total base count
- **Published**: Release date

### Download Options

When downloading SRA data:
- **Automatic Paired-End Detection**: Properly imports paired reads
- **Quality Scores**: Preserves original quality information
- **Metadata Integration**: Attaches all available metadata to sequences
- **Progress Monitoring**: Real-time download status with ability to cancel

## Technical Details

### Bundled Binaries

The plugin includes pre-compiled binaries from NCBI SRA Toolkit v3.1.1:

| Platform | Binaries | Version | Architecture |
|----------|----------|---------|--------------|
| Windows | `fasterq-dump.exe`, `prefetch.exe` | 3.1.1 | x86_64 |
| macOS | `fasterq-dump`, `prefetch` | 3.1.1 | Universal (x86_64 + arm64) |
| Linux | `fasterq-dump`, `prefetch` | 3.1.1 | x86_64 |

#### Performance Optimization

The plugin includes several performance optimizations:
- **8 threads** by default (vs 6 in standard fasterq-dump) for faster processing
- **1GB memory limit** for efficient sorting operations
- **Persistent binary caching** (~90% faster plugin startup after first run)

**Two-Phase Download Strategy** (enabled by default):
The plugin uses a two-phase download strategy that provides 2-3x faster performance:
1. **Phase 1 - Prefetch**: Downloads SRA file to temp directory (network I/O)
2. **Phase 2 - Conversion**: Converts SRA file to FASTQ (CPU/disk I/O)

This separation of network I/O from CPU processing significantly improves overall download performance and provides better progress tracking with distinct phases.

### API Integration

The plugin uses NCBI E-utilities API:
- **ESearch**: Query execution against SRA database
- **ESummary**: Metadata retrieval for search results
- **Rate Limiting**: Respects NCBI's 3 requests/second limit
- **Error Handling**: Automatic retry with exponential backoff

### Build Requirements

To build from source:
- Java 8 or later
- Apache Ant
- Geneious Plugin Development Kit (included in `lib/`)

## Troubleshooting

### Common Issues

#### Plugin Not Appearing
- Ensure Geneious Prime 2024.0 or later is installed
- Verify plugin installation in Tools → Plugins
- Restart Geneious Prime after installation

#### Download Failures
- Check internet connection stability
- Verify sufficient disk space for temporary files
- Some older SRA entries may be unavailable
- Try downloading fewer files simultaneously

#### Performance Issues
- Large datasets require more RAM (increase in Geneious preferences)
- SSD storage recommended for better performance
- Download speed limited by NCBI servers

### Getting Help

1. Check existing [GitHub Issues](https://github.com/dholab/geneious-plugin-sra-search/issues)23. Create a new issue with:
   - Geneious Prime version
   - Plugin version
   - Operating system
   - Error messages or logs
   - Steps to reproduce

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

The included `fasterq-dump` binaries are from the NCBI SRA Toolkit, which is in the public domain.

## Acknowledgments

- **NCBI** for the SRA database and toolkit
- **Biomatters/Geneious** for the plugin API
- **Contributors** and the scientific community for making sequence data publicly available

## Version History

### Version 1.3.0 (2024-11-19)
- **Streaming Import Architecture**: Complete redesign for constant memory usage regardless of file size
- **Incremental Document Loading**: Sequences appear in Geneious immediately as they're read from disk
- **Memory Optimization**: Eliminates out-of-memory errors even with 100M+ read files
- **Improved User Experience**: Can browse and work with sequences while import continues
- Uses callback-based `OperationCallback.addDocument()` API for real-time document availability
- Implements forwarding `ImportCallback` to stream directly: File → Importer → Geneious (no accumulation)
- Fixes progress message overflow in Geneious UI
- Removes invalid import options warnings
- Successfully handles large datasets that previously caused JVM heap exhaustion

### Version 1.2.0 (2024-11-18)
- **Two-Phase Download**: Enabled prefetch + fasterq-dump strategy for 2-3x faster downloads
- **Performance Improvements**: Increased default thread count to 8 (from 6) for faster processing
- Added 1GB memory limit (`--mem 1024M`) for improved sorting operations
- Enhanced progress tracking with real-time spot count display and distinct download phases
- Bundled standalone `prefetch` binaries for all platforms (Windows, macOS, Linux)
- Updated all binaries to NCBI SRA Toolkit v3.1.1 standalone versions
- Performance optimizations result in significantly faster downloads compared to v1.0.x

### Version 1.0.1 (2024-08-12)
- Fixed macOS compatibility issue with universal binary
- Now supports both Intel (x86_64) and Apple Silicon (arm64) Macs
- Updated macOS fasterq-dump to version 3.1.1

### Version 1.0.0 (2024-08-12)
- Initial release
- Basic SRA search functionality
- FASTQ download with quality scores
- Paired-end read support
- Cross-platform compatibility (Windows, macOS, Linux)
- Bundled fasterq-dump binaries
- Real-time progress tracking
- Comprehensive metadata display

---

**For the latest updates and releases, visit our [GitHub repository](https://github.com/dholab/geneious-plugin-sra-search)**