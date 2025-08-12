# Changelog

All notable changes to the NCBI SRA Search Plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-12-XX

### Added
- Initial release of the NCBI SRA Search Plugin
- Direct SRA database search from within Geneious Prime
- NCBI E-utilities API integration for searching
- Advanced search fields (accession, organism, BioProject, etc.)
- SRA dataset downloading using bundled fasterq-dump binaries
- Cross-platform support (Windows, macOS, Linux)
- Automatic paired-end read detection and handling
- Progress tracking for downloads with cancellation support
- Comprehensive metadata display for SRA records
- Built-in error handling and recovery mechanisms

### Features
- **Search Functionality**
  - Free text search across all SRA metadata
  - Specific field searches (accession, organism, BioProject, BioSample)
  - Search result pagination and metadata display
  - Intelligent query optimization for better results

- **Download Functionality**
  - Integrated fasterq-dump binary for all platforms
  - FASTQ file download with quality scores
  - Automatic paired-end read splitting
  - Temporary file management and cleanup
  - Real-time progress reporting

- **Integration**
  - Seamless Geneious Prime integration
  - Standard document operations and workflows
  - Custom SRA document type with rich metadata
  - Plugin-specific menu items and actions

### Technical Details
- Built for Geneious Prime 2024.0+
- Java 8 compatible
- Ant build system
- Comprehensive error handling
- Cross-platform binary management

### Dependencies
- Geneious Public API 4.0+
- NCBI E-utilities API
- Bundled fasterq-dump (NCBI SRA Toolkit)