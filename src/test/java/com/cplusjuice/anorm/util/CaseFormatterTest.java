package com.cplusjuice.anorm.util;

import org.junit.Test;

import static com.cplusjuice.anorm.util.CaseFormat.CAMEL_CASE;
import static com.cplusjuice.anorm.util.CaseFormat.SNAKE_CASE;
import static org.junit.Assert.*;

public class CaseFormatterTest {

    @Test
    public void testConvert() {
        CaseFormatter caseFormatter = new CaseFormatter("TEST_FORMATTER_TEST");
        assertEquals(caseFormatter.convert(CAMEL_CASE), "testFormatterTest");
        assertEquals(caseFormatter.convert(SNAKE_CASE), "TEST_FORMATTER_TEST");
        assertEquals(caseFormatter.convert(CAMEL_CASE), "testFormatterTest");
    }

    @Test
    public void testCaseException1() {
        try {
            new CaseFormatter(null);
            fail("Excepted IllegalArgumentException:" +
                    " Argument for @NotNull parameter 'input' of CaseFormatter must not be null");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    public void testCaseException2() {
        try {
            new CaseFormatter("");
            fail("Excepted IllegalArgumentException: Can't format an empty line");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    public void testCaseException3() {
        try {
            new CaseFormatter("tEST_FORMAT");
            fail("Excepted IllegalArgumentException: Can't determine the text format");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }
}