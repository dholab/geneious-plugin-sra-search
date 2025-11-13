package com.biomatters.plugins.ncbisra;

import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for NcbiSraSearchPlugin
 */
public class NcbiSraSearchPluginTest {

    private NcbiSraSearchPlugin plugin;

    @Before
    public void setUp() {
        plugin = new NcbiSraSearchPlugin();
    }

    @Test
    public void testPluginName() {
        assertThat("Plugin name should be correct",
                plugin.getName(), is("NCBI SRA Search"));
    }

    @Test
    public void testPluginDescription() {
        String description = plugin.getDescription();
        assertNotNull("Description should not be null", description);
        assertTrue("Description should mention SRA",
                description.contains("SRA"));
        assertTrue("Description should mention fasterq-dump",
                description.contains("fasterq-dump"));
    }

    @Test
    public void testPluginHelp() {
        String help = plugin.getHelp();
        assertNotNull("Help text should not be null", help);
        assertTrue("Help should mention NCBI SRA",
                help.contains("NCBI"));
        assertTrue("Help should describe functionality",
                help.length() > 50);
    }

    @Test
    public void testPluginVersion() {
        String version = plugin.getVersion();
        assertNotNull("Version should not be null", version);
        assertThat("Version should follow semantic versioning",
                version, matchesPattern("\\d+\\.\\d+\\.\\d+"));
    }

    @Test
    public void testPluginAuthors() {
        String authors = plugin.getAuthors();
        assertNotNull("Authors should not be null", authors);
        assertFalse("Authors should not be empty", authors.isEmpty());
    }

    @Test
    public void testMinimumApiVersion() {
        String minVersion = plugin.getMinimumApiVersion();
        assertNotNull("Minimum API version should not be null", minVersion);
        assertThat("Minimum API version should be 4.0",
                minVersion, is("4.0"));
    }

    @Test
    public void testMaximumApiVersion() {
        int maxVersion = plugin.getMaximumApiVersion();
        assertThat("Maximum API version should be 4",
                maxVersion, is(4));
    }

    @Test
    public void testGetServices() {
        GeneiousService[] services = plugin.getServices();
        assertNotNull("Services should not be null", services);
        assertThat("Should have at least one service",
                services.length, greaterThanOrEqualTo(1));
    }

    @Test
    public void testGetDocumentOperations() {
        DocumentOperation[] operations = plugin.getDocumentOperations();
        assertNotNull("Operations should not be null", operations);
        assertThat("Should have at least one operation",
                operations.length, greaterThanOrEqualTo(1));
    }

    @Test
    public void testGetDocumentTypes() {
        assertNotNull("Document types should not be null",
                plugin.getDocumentTypes());
    }

    /**
     * Custom matcher for regex pattern matching
     */
    private static org.hamcrest.Matcher<String> matchesPattern(final String regex) {
        return new org.hamcrest.BaseMatcher<String>() {
            @Override
            public boolean matches(Object item) {
                return item != null && item.toString().matches(regex);
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("a string matching pattern: " + regex);
            }
        };
    }

    /**
     * Custom matcher for greater than or equal to
     */
    private static org.hamcrest.Matcher<Integer> greaterThanOrEqualTo(final int value) {
        return new org.hamcrest.BaseMatcher<Integer>() {
            @Override
            public boolean matches(Object item) {
                return item instanceof Integer && ((Integer) item) >= value;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("a value >= " + value);
            }
        };
    }
}
