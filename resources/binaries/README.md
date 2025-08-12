# fasterq-dump Binaries

This directory contains platform-specific fasterq-dump binaries bundled with the plugin.

## Required Binaries

To make this plugin functional, you need to place the fasterq-dump binaries in the appropriate subdirectories:

### macOS (Intel and Apple Silicon)
- Place `fasterq-dump` in the `macos/` directory
- Binary should be executable (`chmod +x fasterq-dump`)

### Windows (64-bit)
- Place `fasterq-dump.exe` in the `windows/` directory

### Linux (64-bit)
- Place `fasterq-dump` in the `linux/` directory
- Binary should be executable (`chmod +x fasterq-dump`)

## Obtaining fasterq-dump

You can download fasterq-dump from the NCBI SRA Toolkit:
https://github.com/ncbi/sra-tools/releases

1. Download the appropriate toolkit version for each platform
2. Extract the toolkit
3. Copy the `fasterq-dump` binary to the corresponding directory
4. Ensure Unix binaries are executable

## Binary Requirements

- fasterq-dump version 3.0.0 or later recommended
- Binaries must be statically linked or have all dependencies available
- Must support the following options:
  - `--split-files` (for paired-end reads)
  - `--progress` (for progress reporting)
  - `--force` (to overwrite existing files)
  - `--outdir` (to specify output directory)

## Testing

You can test if binaries are working by running:
```bash
# macOS/Linux
./fasterq-dump --version

# Windows
fasterq-dump.exe --version
```

This should display version information if the binary is working correctly.