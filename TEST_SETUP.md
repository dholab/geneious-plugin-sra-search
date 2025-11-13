# Test Infrastructure Setup Guide

## Overview

Comprehensive test infrastructure for the NCBI SRA Search Geneious plugin with JUnit 4, code coverage via JaCoCo, and production-ready reporting capabilities.

## Directory Structure

```
geneious-plugin-sra-search/
├── src/                              # Production source code
│   └── com/biomatters/plugins/ncbisra/
├── test/                             # Test source code
│   └── com/biomatters/plugins/ncbisra/
│       ├── NcbiSraSearchPluginTest.java
│       ├── api/
│       │   └── NcbiEUtilsClientTest.java
│       ├── binary/
│       ├── model/
│       │   └── SraRecordTest.java
│       ├── operations/
│       ├── service/
│       │   └── NcbiSraDatabaseServiceSimpleTest.java
│       └── util/
├── test-resources/                   # Test resource files
│   ├── test-sra-response.xml
│   └── test.properties
├── build/
│   ├── classes/                      # Compiled production classes
│   └── test-classes/                 # Compiled test classes
├── reports/
│   ├── test/                         # JUnit test reports
│   │   └── html/                     # HTML test reports
│   └── coverage/                     # JaCoCo coverage reports
│       ├── html/                     # HTML coverage reports
│       ├── jacoco.xml                # XML coverage data
│       ├── jacoco.csv                # CSV coverage data
│       └── jacoco.exec               # Binary coverage data
└── lib/                              # Dependencies
    ├── junit-4.13.2.jar              # [REQUIRED - DOWNLOAD]
    ├── hamcrest-core-1.3.jar         # [REQUIRED - DOWNLOAD]
    ├── hamcrest-library-2.2.jar      # [OPTIONAL]
    ├── mockito-core-4.11.0.jar       # [OPTIONAL]
    ├── byte-buddy-1.14.5.jar         # [OPTIONAL - for Mockito]
    ├── byte-buddy-agent-1.14.5.jar   # [OPTIONAL - for Mockito]
    ├── objenesis-3.3.jar             # [OPTIONAL - for Mockito]
    └── jacoco/                       # JaCoCo directory [REQUIRED]
        ├── org.jacoco.agent-0.8.11.jar
        ├── org.jacoco.ant-0.8.11.jar
        ├── org.jacoco.core-0.8.11.jar
        └── org.jacoco.report-0.8.11.jar
```

## Required Dependencies

### Core Testing (Required)

Download and place in `lib/` directory:

1. **JUnit 4.13.2**
   - Download: https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
   - File: `lib/junit-4.13.2.jar`

2. **Hamcrest Core 1.3**
   - Download: https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
   - File: `lib/hamcrest-core-1.3.jar`

### Code Coverage (Required)

Create `lib/jacoco/` directory and download:

1. **JaCoCo 0.8.11 Bundle**
   - Download: https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11.jar
   - Download: https://repo1.maven.org/maven2/org/jacoco/org.jacoco.ant/0.8.11/org.jacoco.ant-0.8.11.jar
   - Download: https://repo1.maven.org/maven2/org/jacoco/org.jacoco.core/0.8.11/org.jacoco.core-0.8.11.jar
   - Download: https://repo1.maven.org/maven2/org/jacoco/org.jacoco.report/0.8.11/org.jacoco.report-0.8.11.jar

### Optional Dependencies (Recommended)

For advanced testing with mocking capabilities:

1. **Hamcrest Library 2.2** (Extended matchers)
   - Download: https://repo1.maven.org/maven2/org/hamcrest/hamcrest-library/2.2/hamcrest-library-2.2.jar

2. **Mockito Core 4.11.0** (Mocking framework)
   - Download: https://repo1.maven.org/maven2/org/mockito/mockito-core/4.11.0/mockito-core-4.11.0.jar
   - Requires: byte-buddy, byte-buddy-agent, objenesis

3. **Byte Buddy 1.14.5**
   - Download: https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.5/byte-buddy-1.14.5.jar

4. **Byte Buddy Agent 1.14.5**
   - Download: https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.5/byte-buddy-agent-1.14.5.jar

5. **Objenesis 3.3**
   - Download: https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar

## Quick Download Script

Create this script as `download-test-deps.sh` to automate downloads:

```bash
#!/bin/bash
set -e

LIB_DIR="lib"
JACOCO_DIR="$LIB_DIR/jacoco"
mkdir -p "$JACOCO_DIR"

# Core dependencies
echo "Downloading JUnit 4.13.2..."
curl -L -o "$LIB_DIR/junit-4.13.2.jar" \
  https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar

echo "Downloading Hamcrest Core 1.3..."
curl -L -o "$LIB_DIR/hamcrest-core-1.3.jar" \
  https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

# JaCoCo
echo "Downloading JaCoCo 0.8.11..."
curl -L -o "$JACOCO_DIR/org.jacoco.agent-0.8.11.jar" \
  https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11.jar
curl -L -o "$JACOCO_DIR/org.jacoco.ant-0.8.11.jar" \
  https://repo1.maven.org/maven2/org/jacoco/org.jacoco.ant/0.8.11/org.jacoco.ant-0.8.11.jar
curl -L -o "$JACOCO_DIR/org.jacoco.core-0.8.11.jar" \
  https://repo1.maven.org/maven2/org/jacoco/org.jacoco.core/0.8.11/org.jacoco.core-0.8.11.jar
curl -L -o "$JACOCO_DIR/org.jacoco.report-0.8.11.jar" \
  https://repo1.maven.org/maven2/org/jacoco/org.jacoco.report/0.8.11/org.jacoco.report-0.8.11.jar

# Optional dependencies
echo "Downloading Mockito and dependencies..."
curl -L -o "$LIB_DIR/mockito-core-4.11.0.jar" \
  https://repo1.maven.org/maven2/org/mockito/mockito-core/4.11.0/mockito-core-4.11.0.jar
curl -L -o "$LIB_DIR/byte-buddy-1.14.5.jar" \
  https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.5/byte-buddy-1.14.5.jar
curl -L -o "$LIB_DIR/byte-buddy-agent-1.14.5.jar" \
  https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.5/byte-buddy-agent-1.14.5.jar
curl -L -o "$LIB_DIR/objenesis-3.3.jar" \
  https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar

echo "All test dependencies downloaded successfully!"
```

Make executable: `chmod +x download-test-deps.sh`

## Ant Build Targets

### Test Execution Targets

#### 1. `compile-tests`
Compiles test source code.

```bash
ant compile-tests
```

**Output:** Compiled test classes in `build/test-classes/`

#### 2. `test`
Runs all unit tests with JaCoCo code coverage.

```bash
ant test
```

**Features:**
- Executes all `*Test.java` and `Test*.java` files
- Generates coverage data in `reports/coverage/jacoco.exec`
- Displays test results in console
- Creates XML test reports in `reports/test/`

**Output:**
- Test results printed to console
- XML test reports: `reports/test/TEST-*.xml`
- Coverage data: `reports/coverage/jacoco.exec`

#### 3. `test-report`
Generates HTML reports from test results.

```bash
ant test-report
```

**Output:** HTML test reports in `reports/test/html/index.html`

#### 4. `coverage-report`
Generates comprehensive coverage reports in multiple formats.

```bash
ant coverage-report
```

**Output:**
- HTML report: `reports/coverage/html/index.html`
- XML report: `reports/coverage/jacoco.xml`
- CSV report: `reports/coverage/jacoco.csv`

#### 5. `coverage-check`
Validates coverage against defined thresholds.

```bash
ant coverage-check
```

**Thresholds:**
- Bundle instruction coverage: 60%
- Maximum missed classes: 5
- Class line coverage: 50%
- Method line coverage: 50%

**Note:** Fails build if thresholds are not met.

#### 6. `test-all`
Comprehensive test suite - runs tests with all reports and coverage analysis.

```bash
ant test-all
```

**Executes:**
1. Compile tests
2. Run tests with coverage
3. Generate test reports
4. Generate coverage reports
5. Check coverage thresholds

**Recommended for:** CI/CD pipelines, pre-commit checks

#### 7. `verify`
Full build verification including compilation, testing, and packaging.

```bash
ant verify
```

**Executes:** test-all + package

### Build Targets

#### 8. `clean`
Removes all build artifacts, test outputs, and reports.

```bash
ant clean
```

**Deletes:**
- `build/` directory
- `dist/` directory
- `reports/` directory

## Running Tests

### Quick Start

1. **Run all tests:**
   ```bash
   ant test
   ```

2. **Generate reports:**
   ```bash
   ant test-all
   ```

3. **View test results:**
   ```bash
   open reports/test/html/index.html
   ```

4. **View coverage results:**
   ```bash
   open reports/coverage/html/index.html
   ```

### Development Workflow

#### Before Committing Code

```bash
# Clean previous builds
ant clean

# Run comprehensive test suite
ant test-all

# Review coverage reports
open reports/coverage/html/index.html
```

#### Quick Test Cycle

```bash
# Compile and run tests only
ant test

# Check console output for failures
```

#### Coverage-Focused Development

```bash
# Run with coverage check
ant coverage-check

# Generate detailed coverage report
ant coverage-report

# Open coverage report
open reports/coverage/html/index.html
```

## Writing Tests

### Test Naming Conventions

- Test classes: `*Test.java` or `Test*.java`
- Example: `SraRecordTest.java`, `NcbiEUtilsClientTest.java`
- Location: Mirror production package structure in `test/` directory

### Sample Test Class

```java
package com.biomatters.plugins.ncbisra.model;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for SraRecord
 */
public class SraRecordTest {

    private SraRecord record;

    @BeforeClass
    public static void setUpClass() {
        // One-time setup for all tests
    }

    @Before
    public void setUp() {
        // Setup before each test
        record = new SraRecord();
    }

    @Test
    public void testConstructor() {
        assertNotNull("Record should be instantiated", record);
    }

    @Test
    public void testAccessionValidation() {
        record.setAccession("SRR12345678");
        assertThat(record.getAccession(), is("SRR12345678"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAccession() {
        record.setAccession(null);
    }

    @After
    public void tearDown() {
        // Cleanup after each test
        record = null;
    }

    @AfterClass
    public static void tearDownClass() {
        // One-time cleanup after all tests
    }
}
```

### Using Hamcrest Matchers

```java
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

// Object matching
assertThat(value, is(expectedValue));
assertThat(value, not(unexpectedValue));
assertThat(value, nullValue());
assertThat(value, notNullValue());

// Collection matching
assertThat(list, hasItem(item));
assertThat(list, hasItems(item1, item2));

// String matching
assertThat(string, containsString("substring"));
assertThat(string, startsWith("prefix"));
assertThat(string, endsWith("suffix"));

// Type matching
assertThat(object, instanceOf(SomeClass.class));
```

### Using Test Resources

```java
import java.io.InputStream;

@Test
public void testParseXmlResponse() throws Exception {
    InputStream xmlStream = getClass()
        .getResourceAsStream("/test-sra-response.xml");
    
    SraDocument doc = parser.parse(xmlStream);
    
    assertNotNull(doc);
    assertThat(doc.getAccession(), is("SRR12345678"));
}
```

## Coverage Targets

### Recommended Thresholds

| Metric | Target | Minimum |
|--------|--------|---------|
| **Line Coverage** | 85% | 60% |
| **Branch Coverage** | 80% | 50% |
| **Method Coverage** | 90% | 70% |
| **Class Coverage** | 95% | 80% |

### Current Configuration

The build is configured with baseline thresholds (lower than recommended):
- Bundle instruction coverage: 60%
- Class line coverage: 50%
- Method line coverage: 50%

**Recommendation:** Gradually increase thresholds as test coverage improves.

### Adjusting Thresholds

Edit `build.xml`, locate the `coverage-check` target:

```xml
<rule element="BUNDLE">
    <limit counter="INSTRUCTION" value="COVEREDRATIO" minimum="0.85"/>
    <limit counter="CLASS" value="MISSEDCOUNT" maximum="2"/>
</rule>
```

## Integration with CI/CD

### GitHub Actions Example

```yaml
name: Test Suite

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Download test dependencies
      run: ./download-test-deps.sh
    
    - name: Run tests with coverage
      run: ant test-all
    
    - name: Upload coverage reports
      uses: actions/upload-artifact@v3
      with:
        name: coverage-reports
        path: reports/coverage/
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: reports/test/
```

### Jenkins Pipeline Example

```groovy
pipeline {
    agent any
    
    tools {
        jdk 'JDK8'
        ant 'Ant 1.10'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup Dependencies') {
            steps {
                sh './download-test-deps.sh'
            }
        }
        
        stage('Test') {
            steps {
                sh 'ant test-all'
            }
        }
        
        stage('Publish Reports') {
            steps {
                junit 'reports/test/*.xml'
                publishHTML([
                    reportDir: 'reports/coverage/html',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report'
                ])
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'reports/**/*', fingerprint: true
        }
    }
}
```

## Test Categories and Patterns

### Unit Tests
- Test individual classes in isolation
- Mock external dependencies
- Fast execution (< 1 second per test)
- Location: `test/` directory

### Integration Tests
- Test component interactions
- May use real dependencies
- Moderate execution time
- Naming: `*IntegrationTest.java`

### Test Utilities
- Helper classes for tests
- Naming: `*TestUtils.java`
- Excluded from test execution

### Abstract Test Classes
- Base classes for test hierarchies
- Naming: `Abstract*.java`
- Excluded from test execution

## Troubleshooting

### Common Issues

#### 1. JaCoCo Not Found

**Error:** `Problem: failed to create task or type antlib:org.jacoco.ant:coverage`

**Solution:**
```bash
# Ensure JaCoCo JARs are in lib/jacoco/
ls -la lib/jacoco/

# Download missing JARs using the script
./download-test-deps.sh
```

#### 2. Tests Not Found

**Error:** `No tests found`

**Solution:**
- Verify test files match naming convention (`*Test.java`)
- Check test files are in correct package structure
- Ensure tests have `@Test` annotations

#### 3. Coverage Report Empty

**Issue:** Coverage report shows 0% coverage

**Solution:**
- Ensure `ant test` runs before `ant coverage-report`
- Check `jacoco.exec` file exists in `reports/coverage/`
- Verify test classes are in `build/test-classes/`

#### 4. Compilation Errors

**Error:** `Cannot find symbol` in test code

**Solution:**
```bash
# Ensure production code compiles first
ant compile

# Check classpath includes required JARs
# Verify test dependencies are downloaded
```

## Best Practices

### Test Design

1. **Test one thing per test method**
   - Clear, focused test cases
   - Easy to identify failures

2. **Use descriptive test names**
   - `testShouldThrowExceptionWhenAccessionIsNull()`
   - `testShouldParseValidXmlResponse()`

3. **Follow AAA pattern**
   - Arrange: Setup test data
   - Act: Execute the code under test
   - Assert: Verify results

4. **Use meaningful assertions**
   - Prefer Hamcrest matchers for readability
   - Include assertion messages

5. **Avoid test interdependencies**
   - Each test should be independent
   - Tests should pass in any order

### Coverage Strategy

1. **Focus on critical paths**
   - Prioritize business logic
   - API interactions
   - Data transformations

2. **Test edge cases**
   - Null inputs
   - Empty collections
   - Boundary values

3. **Don't chase 100% coverage**
   - Focus on meaningful tests
   - Some code is not worth testing (getters/setters)

4. **Review coverage reports regularly**
   - Identify untested code
   - Remove dead code

### Performance

1. **Keep tests fast**
   - Mock external services
   - Use in-memory databases for data tests
   - Avoid Thread.sleep()

2. **Parallelize when possible**
   - Independent test classes can run in parallel
   - Configure Ant for parallel execution if needed

3. **Use test fixtures efficiently**
   - Share expensive setup via `@BeforeClass`
   - Clean up in `@After` and `@AfterClass`

## Advanced Configuration

### Custom Test Filters

Edit `build.xml` to customize test execution:

```xml
<batchtest todir="${test.reports.dir}">
    <fileset dir="${test.dir}">
        <!-- Include patterns -->
        <include name="**/*Test.java"/>
        <include name="**/*Tests.java"/>
        
        <!-- Exclude patterns -->
        <exclude name="**/Abstract*.java"/>
        <exclude name="**/*IntegrationTest.java"/>
        <exclude name="**/*TestUtils.java"/>
    </fileset>
</batchtest>
```

### JVM Arguments for Tests

Add JVM arguments for test execution:

```xml
<junit ...>
    <jvmarg value="-Xmx1024m"/>
    <jvmarg value="-Dtest.mode=unit"/>
    <classpath refid="test.classpath"/>
    ...
</junit>
```

### Parallel Test Execution

Configure parallel test execution:

```xml
<junit ... forkmode="perBatch" threads="4">
    ...
</junit>
```

## Report Formats

### JUnit XML Reports

Location: `reports/test/TEST-*.xml`

Used by CI/CD tools for test result tracking.

### JUnit HTML Reports

Location: `reports/test/html/index.html`

Human-readable test execution summary with:
- Pass/fail statistics
- Execution time
- Failure stack traces

### JaCoCo HTML Reports

Location: `reports/coverage/html/index.html`

Interactive coverage reports showing:
- Package-level coverage
- Class-level coverage
- Line-by-line coverage with color coding
- Branch coverage details

### JaCoCo XML Reports

Location: `reports/coverage/jacoco.xml`

Machine-readable format for:
- SonarQube integration
- Coverage trend analysis
- CI/CD reporting

### JaCoCo CSV Reports

Location: `reports/coverage/jacoco.csv`

Tabular format for:
- Spreadsheet analysis
- Custom reporting tools
- Coverage tracking over time

## Next Steps

1. **Download required dependencies**
   ```bash
   ./download-test-deps.sh
   ```

2. **Run initial test suite**
   ```bash
   ant test-all
   ```

3. **Review coverage reports**
   ```bash
   open reports/coverage/html/index.html
   ```

4. **Write tests for existing code**
   - Start with model classes
   - Add API client tests
   - Test service layer
   - Cover operations and utilities

5. **Integrate with CI/CD**
   - Add test execution to build pipeline
   - Configure coverage thresholds
   - Setup automated reporting

6. **Establish testing standards**
   - Document testing guidelines
   - Set coverage targets
   - Conduct code review for tests

## Resources

### Documentation

- JUnit 4: https://junit.org/junit4/
- Hamcrest: http://hamcrest.org/JavaHamcrest/
- JaCoCo: https://www.jacoco.org/jacoco/
- Mockito: https://site.mockito.org/

### Maven Central

- JUnit: https://mvnrepository.com/artifact/junit/junit
- JaCoCo: https://mvnrepository.com/artifact/org.jacoco
- Hamcrest: https://mvnrepository.com/artifact/org.hamcrest
- Mockito: https://mvnrepository.com/artifact/org.mockito

## Support

For issues or questions:
1. Check this documentation
2. Review build.xml comments
3. Examine sample test classes
4. Consult JUnit/JaCoCo documentation

---

**Version:** 1.0.0  
**Last Updated:** 2025-11-13  
**Java Version:** 8  
**Build Tool:** Apache Ant
