#!/bin/bash
# Download test dependencies for NCBI SRA Search Plugin
# This script downloads JUnit, Hamcrest, Mockito, and JaCoCo dependencies

set -e

LIB_DIR="lib"
JACOCO_DIR="$LIB_DIR/jacoco"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Test Dependencies Download Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Create directories
mkdir -p "$JACOCO_DIR"

# Function to download with progress
download_file() {
    local url=$1
    local output=$2
    local name=$3
    
    echo -e "${GREEN}Downloading $name...${NC}"
    curl -L --progress-bar -o "$output" "$url"
}

# Core testing dependencies
echo -e "${BLUE}Downloading core testing dependencies...${NC}"

download_file \
    "https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar" \
    "$LIB_DIR/junit-4.13.2.jar" \
    "JUnit 4.13.2"

download_file \
    "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" \
    "$LIB_DIR/hamcrest-core-1.3.jar" \
    "Hamcrest Core 1.3"

# JaCoCo coverage dependencies
echo ""
echo -e "${BLUE}Downloading JaCoCo 0.8.11...${NC}"

download_file \
    "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11.jar" \
    "$JACOCO_DIR/org.jacoco.agent-0.8.11.jar" \
    "JaCoCo Agent"

download_file \
    "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.ant/0.8.11/org.jacoco.ant-0.8.11.jar" \
    "$JACOCO_DIR/org.jacoco.ant-0.8.11.jar" \
    "JaCoCo Ant"

download_file \
    "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.core/0.8.11/org.jacoco.core-0.8.11.jar" \
    "$JACOCO_DIR/org.jacoco.core-0.8.11.jar" \
    "JaCoCo Core"

download_file \
    "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.report/0.8.11/org.jacoco.report-0.8.11.jar" \
    "$JACOCO_DIR/org.jacoco.report-0.8.11.jar" \
    "JaCoCo Report"

# Optional: Mockito and dependencies
echo ""
echo -e "${BLUE}Downloading Mockito and dependencies (optional)...${NC}"

download_file \
    "https://repo1.maven.org/maven2/org/mockito/mockito-core/4.11.0/mockito-core-4.11.0.jar" \
    "$LIB_DIR/mockito-core-4.11.0.jar" \
    "Mockito Core 4.11.0"

download_file \
    "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.5/byte-buddy-1.14.5.jar" \
    "$LIB_DIR/byte-buddy-1.14.5.jar" \
    "Byte Buddy 1.14.5"

download_file \
    "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.5/byte-buddy-agent-1.14.5.jar" \
    "$LIB_DIR/byte-buddy-agent-1.14.5.jar" \
    "Byte Buddy Agent 1.14.5"

download_file \
    "https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar" \
    "$LIB_DIR/objenesis-3.3.jar" \
    "Objenesis 3.3"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}All test dependencies downloaded!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Downloaded files:"
echo "  Core Testing:"
echo "    - $LIB_DIR/junit-4.13.2.jar"
echo "    - $LIB_DIR/hamcrest-core-1.3.jar"
echo ""
echo "  JaCoCo Coverage:"
echo "    - $JACOCO_DIR/org.jacoco.agent-0.8.11.jar"
echo "    - $JACOCO_DIR/org.jacoco.ant-0.8.11.jar"
echo "    - $JACOCO_DIR/org.jacoco.core-0.8.11.jar"
echo "    - $JACOCO_DIR/org.jacoco.report-0.8.11.jar"
echo ""
echo "  Mockito (Optional):"
echo "    - $LIB_DIR/mockito-core-4.11.0.jar"
echo "    - $LIB_DIR/byte-buddy-1.14.5.jar"
echo "    - $LIB_DIR/byte-buddy-agent-1.14.5.jar"
echo "    - $LIB_DIR/objenesis-3.3.jar"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Run tests: ant test"
echo "  2. Generate reports: ant test-all"
echo "  3. View coverage: open reports/coverage/html/index.html"
echo ""
