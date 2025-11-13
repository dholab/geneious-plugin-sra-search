# Quick Start Testing Guide

## 1. Download Dependencies (One-Time Setup)

```bash
chmod +x download-test-deps.sh
./download-test-deps.sh
```

This downloads (~8 MB):
- JUnit 4.13.2
- Hamcrest Core 1.3
- JaCoCo 0.8.11 (4 JARs)
- Mockito Core 4.11.0 + dependencies

## 2. Run Tests

### Run All Tests
```bash
ant test
```

### Run With HTML Reports
```bash
ant test-all
```

### Full Build Verification
```bash
ant verify
```

## 3. View Reports

### Test Results
```bash
open reports/test/html/index.html
```

### Coverage Report (after downloading JaCoCo)
```bash
open reports/coverage/html/index.html
```

## Available Ant Targets

| Target | Description |
|--------|-------------|
| `ant compile` | Compile production code |
| `ant compile-tests` | Compile test code |
| `ant test` | Run all unit tests |
| `ant test-report` | Generate HTML test reports |
| `ant test-all` | Run tests + generate reports |
| `ant verify` | Test + package plugin |
| `ant clean` | Clean all build artifacts |

## Test Files Created

- **NcbiSraSearchPluginTest** (11 tests) - Main plugin functionality
- **SraRecordTest** (96 tests) - Model object comprehensive testing
- **NcbiSraDatabaseServiceSimpleTest** (80+ tests) - Service layer with mocking
- **NcbiEUtilsClientTest** (85+ tests) - API client with fixtures

**Total: 272+ tests, 2,258 lines of test code**

## Documentation

- **Full Setup Guide:** [TEST_SETUP.md](TEST_SETUP.md)
- **Implementation Report:** [TEST_INFRASTRUCTURE_REPORT.md](TEST_INFRASTRUCTURE_REPORT.md)

---

For issues, see the Troubleshooting section in TEST_SETUP.md
