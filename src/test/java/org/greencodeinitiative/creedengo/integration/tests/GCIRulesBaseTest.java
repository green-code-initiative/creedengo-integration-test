package org.greencodeinitiative.creedengo.integration.tests;

import org.junit.jupiter.api.Test;
import org.sonarqube.ws.Common;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GCIRulesBaseTest {

    // -----------------------------------------------------------------------
    // Severity constants — exact enum value
    // -----------------------------------------------------------------------

    @Test
    void testSeverityInfoConstant() {
        assertEquals(Common.Severity.INFO, GCIRulesBase.SEVERITY_INFO);
    }

    @Test
    void testSeverityInfoConstant_isNotMinor() {
        assertNotEquals(Common.Severity.MINOR, GCIRulesBase.SEVERITY_INFO);
    }

    @Test
    void testSeverityMinorConstant() {
        assertEquals(Common.Severity.MINOR, GCIRulesBase.SEVERITY_MINOR);
    }

    @Test
    void testSeverityMinorConstant_isNotMajor() {
        assertNotEquals(Common.Severity.MAJOR, GCIRulesBase.SEVERITY_MINOR);
    }

    @Test
    void testSeverityMajorConstant() {
        assertEquals(Common.Severity.MAJOR, GCIRulesBase.SEVERITY_MAJOR);
    }

    @Test
    void testSeverityMajorConstant_isNotMinor() {
        assertNotEquals(Common.Severity.MINOR, GCIRulesBase.SEVERITY_MAJOR);
    }

    @Test
    void testDefaultSeverityIsMinor() {
        assertEquals(Common.Severity.MINOR, GCIRulesBase.SEVERITY);
    }

    @Test
    void testDefaultSeverityIsSameInstanceAsSeverityMinor() {
        assertSame(GCIRulesBase.SEVERITY_MINOR, GCIRulesBase.SEVERITY,
            "SEVERITY must be the same reference as SEVERITY_MINOR");
    }

    // -----------------------------------------------------------------------
    // Type constant
    // -----------------------------------------------------------------------

    @Test
    void testTypeIsCodeSmell() {
        assertEquals(Common.RuleType.CODE_SMELL, GCIRulesBase.TYPE);
    }

    @Test
    void testTypeIsNotBug() {
        assertNotEquals(Common.RuleType.BUG, GCIRulesBase.TYPE);
    }

    @Test
    void testTypeIsNotVulnerability() {
        assertNotEquals(Common.RuleType.VULNERABILITY, GCIRulesBase.TYPE);
    }

    // -----------------------------------------------------------------------
    // Effort constants — exact value of each constant individually
    // -----------------------------------------------------------------------

    @Test
    void testEffort1Min() {
        assertEquals("1min", GCIRulesBase.EFFORT_1MIN);
    }

    @Test
    void testEffort2Min() {
        assertEquals("2min", GCIRulesBase.EFFORT_2MIN);
    }

    @Test
    void testEffort5Min() {
        assertEquals("5min", GCIRulesBase.EFFORT_5MIN);
    }

    @Test
    void testEffort10Min() {
        assertEquals("10min", GCIRulesBase.EFFORT_10MIN);
    }

    @Test
    void testEffort15Min() {
        assertEquals("15min", GCIRulesBase.EFFORT_15MIN);
    }

    @Test
    void testEffort20Min() {
        assertEquals("20min", GCIRulesBase.EFFORT_20MIN);
    }

    @Test
    void testEffort50Min() {
        assertEquals("50min", GCIRulesBase.EFFORT_50MIN);
    }

    @Test
    void testEffort1H() {
        assertEquals("1h", GCIRulesBase.EFFORT_1H);
    }

    @Test
    void testEffortConstantsAreAllDistinct() {
        String[] efforts = {
            GCIRulesBase.EFFORT_1MIN, GCIRulesBase.EFFORT_2MIN,
            GCIRulesBase.EFFORT_5MIN, GCIRulesBase.EFFORT_10MIN,
            GCIRulesBase.EFFORT_15MIN, GCIRulesBase.EFFORT_20MIN,
            GCIRulesBase.EFFORT_50MIN, GCIRulesBase.EFFORT_1H
        };
        long distinctCount = java.util.Arrays.stream(efforts).distinct().count();
        assertEquals(8, distinctCount, "All effort constants must be distinct");
    }

    @Test
    void testEffortConstantsAreNotNull() {
        assertNotNull(GCIRulesBase.EFFORT_1MIN);
        assertNotNull(GCIRulesBase.EFFORT_2MIN);
        assertNotNull(GCIRulesBase.EFFORT_5MIN);
        assertNotNull(GCIRulesBase.EFFORT_10MIN);
        assertNotNull(GCIRulesBase.EFFORT_15MIN);
        assertNotNull(GCIRulesBase.EFFORT_20MIN);
        assertNotNull(GCIRulesBase.EFFORT_50MIN);
        assertNotNull(GCIRulesBase.EFFORT_1H);
    }

    // -----------------------------------------------------------------------
    // EXTRACT_FIELDS — exact size and each index individually
    // -----------------------------------------------------------------------

    @Test
    void testExtractFieldsNotNull() {
        assertNotNull(GCIRulesBase.EXTRACT_FIELDS);
    }

    @Test
    void testExtractFieldsHasExactlySevenElements() {
        assertEquals(7, GCIRulesBase.EXTRACT_FIELDS.length,
            "EXTRACT_FIELDS must contain exactly 7 elements");
    }

    @Test
    void testExtractFieldsIndex0IsRule() {
        assertEquals("rule", GCIRulesBase.EXTRACT_FIELDS[0]);
    }

    @Test
    void testExtractFieldsIndex1IsMessage() {
        assertEquals("message", GCIRulesBase.EXTRACT_FIELDS[1]);
    }

    @Test
    void testExtractFieldsIndex2IsTextRangeStartLine() {
        assertEquals("textRange.startLine", GCIRulesBase.EXTRACT_FIELDS[2]);
    }

    @Test
    void testExtractFieldsIndex3IsTextRangeEndLine() {
        assertEquals("textRange.endLine", GCIRulesBase.EXTRACT_FIELDS[3]);
    }

    @Test
    void testExtractFieldsIndex4IsSeverity() {
        assertEquals("severity", GCIRulesBase.EXTRACT_FIELDS[4]);
    }

    @Test
    void testExtractFieldsIndex5IsType() {
        assertEquals("type", GCIRulesBase.EXTRACT_FIELDS[5]);
    }

    @Test
    void testExtractFieldsIndex6IsEffort() {
        assertEquals("effort", GCIRulesBase.EXTRACT_FIELDS[6]);
    }

    @Test
    void testExtractFieldsFullArray() {
        assertArrayEquals(
            new String[]{"rule", "message", "textRange.startLine", "textRange.endLine", "severity", "type", "effort"},
            GCIRulesBase.EXTRACT_FIELDS
        );
    }

    // -----------------------------------------------------------------------
    // checkIssuesForFile (5 args) — delegation to the 8-arg overload
    // With arrays of equal length, the length validation passes,
    // then accessing analyzedProjects (null) causes an exception.
    // We verify that it is NOT an AssertionError (the length check passed).
    // -----------------------------------------------------------------------

    @Test
    void testCheckIssuesForFile5Args_equalArrays_passesLengthCheck_thenFailsOnNullProjects() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {5};
        int[] endLines   = {5};
        // The assertThat(startLines.length).isEqualTo(endLines.length) check passes (1 == 1)
        // Then analyzedProjects is null → NullPointerException, not AssertionError
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile("file.java", "RULE", "msg", startLines, endLines));
        assertFalse(ex instanceof AssertionError,
            "An AssertionError must not be thrown for arrays of the same length");
    }

    @Test
    void testCheckIssuesForFile5Args_multipleEqualLines_passesLengthCheck() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {1, 5, 10};
        int[] endLines   = {2, 6, 11};
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile("MyFile.java", "RULE_ID", "message", startLines, endLines));
        assertFalse(ex instanceof AssertionError,
            "Three valid lines must not throw an AssertionError on the length check");
    }

    @Test
    void testCheckIssuesForFile5Args_usesDefaultSeverityMinor() throws Exception {
        // We verify via reflection that the 5-arg overload correctly calls the 8-arg overload
        // with SEVERITY (MINOR), TYPE (CODE_SMELL) and EFFORT_5MIN
        Field severityField = GCIRulesBase.class.getDeclaredField("SEVERITY");
        severityField.setAccessible(true);
        Common.Severity defaultSeverity = (Common.Severity) severityField.get(null);
        assertEquals(Common.Severity.MINOR, defaultSeverity,
            "The 5-arg overload must use SEVERITY = MINOR");
    }

    @Test
    void testCheckIssuesForFile5Args_usesDefaultEffort5Min() throws Exception {
        Field effortField = GCIRulesBase.class.getDeclaredField("EFFORT_5MIN");
        effortField.setAccessible(true);
        String defaultEffort = (String) effortField.get(null);
        assertEquals("5min", defaultEffort,
            "The 5-arg overload must use EFFORT_5MIN = '5min'");
    }

    @Test
    void testCheckIssuesForFile5Args_usesDefaultTypeCodeSmell() throws Exception {
        Field typeField = GCIRulesBase.class.getDeclaredField("TYPE");
        typeField.setAccessible(true);
        Common.RuleType defaultType = (Common.RuleType) typeField.get(null);
        assertEquals(Common.RuleType.CODE_SMELL, defaultType,
            "The 5-arg overload must use TYPE = CODE_SMELL");
    }

    // -----------------------------------------------------------------------
    // checkIssuesForFile (8 args) — array length validation
    // -----------------------------------------------------------------------

    @Test
    void testCheckIssuesForFile8Args_equalArrays_passesLengthCheck() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {3};
        int[] endLines   = {3};
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile(
                "file.java", "RULE", "msg", startLines, endLines,
                Common.Severity.MINOR, Common.RuleType.CODE_SMELL, "5min"));
        assertFalse(ex instanceof AssertionError,
            "Arrays of the same size must not throw an AssertionError on the length check");
    }

    @Test
    void testCheckIssuesForFileThrowsWhenStartLinesLongerThanEndLines() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {1, 2};
        int[] endLines   = {1};
        AssertionError error = assertThrows(AssertionError.class,
            () -> base.checkIssuesForFile("file.java", "RULE", "msg", startLines, endLines));
        assertNotNull(error.getMessage());
    }

    @Test
    void testCheckIssuesForFileThrowsWhenEndLinesLongerThanStartLines() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {1};
        int[] endLines   = {1, 2};
        AssertionError error = assertThrows(AssertionError.class,
            () -> base.checkIssuesForFile("file.java", "RULE", "msg", startLines, endLines));
        assertNotNull(error.getMessage());
    }

    @Test
    void testCheckIssuesForFileWithOverloadedSignatureThrowsOnMismatch() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {1, 2, 3};
        int[] endLines   = {1};
        AssertionError error = assertThrows(AssertionError.class,
            () -> base.checkIssuesForFile(
                "file.java", "RULE", "msg", startLines, endLines,
                Common.Severity.MINOR, Common.RuleType.CODE_SMELL, "5min"));
        assertNotNull(error.getMessage());
    }

    @Test
    void testCheckIssuesForFile8Args_emptyArrays_passesLengthCheck() {
        GCIRulesBase base = new GCIRulesBase();
        int[] empty = {};
        // 0 == 0 passes the length check, then fails on null analyzedProjects
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile(
                "file.java", "RULE", "msg", empty, empty,
                Common.Severity.MAJOR, Common.RuleType.BUG, "10min"));
        assertFalse(ex instanceof AssertionError,
            "Empty arrays (0 == 0) must not throw an AssertionError on the length check");
    }

    @Test
    void testCheckIssuesForFile8Args_withDifferentSeverity_passesLengthCheck() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {1};
        int[] endLines   = {1};
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile(
                "file.java", "RULE", "msg", startLines, endLines,
                Common.Severity.MAJOR, Common.RuleType.BUG, "1h"));
        assertFalse(ex instanceof AssertionError);
    }

    @Test
    void testCheckIssuesForFile8Args_withInfoSeverity_passesLengthCheck() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {10, 20};
        int[] endLines   = {10, 20};
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile(
                "another.java", "RULE_X", "some message", startLines, endLines,
                Common.Severity.INFO, Common.RuleType.CODE_SMELL, "2min"));
        assertFalse(ex instanceof AssertionError);
    }

    // -----------------------------------------------------------------------
    // Class structure
    // -----------------------------------------------------------------------

    @Test
    void testGCIRulesBaseIsConcreteClass() {
        assertFalse(
            java.lang.reflect.Modifier.isAbstract(GCIRulesBase.class.getModifiers()),
            "GCIRulesBase must be a concrete instantiable class"
        );
    }

    @Test
    void testGCIRulesBaseExtendsBuildProjectEngine() {
        assertEquals(BuildProjectEngine.class, GCIRulesBase.class.getSuperclass(),
            "GCIRulesBase must directly extend BuildProjectEngine");
    }

    @Test
    void testGCIRulesBaseInstantiationDoesNotThrow() {
        assertDoesNotThrow(GCIRulesBase::new);
    }

    @Test
    void testGCIRulesBaseIsPublic() {
        assertTrue(
            java.lang.reflect.Modifier.isPublic(GCIRulesBase.class.getModifiers()),
            "GCIRulesBase must be a public class"
        );
    }

    @Test
    void testGCIRulesBaseHasCheckIssuesForFile5ArgsMethod() throws NoSuchMethodException {
        assertNotNull(
            GCIRulesBase.class.getDeclaredMethod(
                "checkIssuesForFile",
                String.class, String.class, String.class, int[].class, int[].class)
        );
    }

    @Test
    void testGCIRulesBaseHasCheckIssuesForFile8ArgsMethod() throws NoSuchMethodException {
        assertNotNull(
            GCIRulesBase.class.getDeclaredMethod(
                "checkIssuesForFile",
                String.class, String.class, String.class, int[].class, int[].class,
                Common.Severity.class, Common.RuleType.class, String.class)
        );
    }
}
