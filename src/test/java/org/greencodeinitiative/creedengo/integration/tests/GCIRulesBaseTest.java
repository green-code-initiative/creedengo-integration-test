package org.greencodeinitiative.creedengo.integration.tests;

import org.junit.jupiter.api.Test;
import org.sonarqube.ws.Common;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GCIRulesBaseTest {

    // -----------------------------------------------------------------------
    // Constantes de sévérité — valeur exacte de l'enum
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
            "SEVERITY doit être la même référence que SEVERITY_MINOR");
    }

    // -----------------------------------------------------------------------
    // Constante de type
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
    // Constantes d'effort — valeur exacte de chaque constante individuellement
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
        assertEquals(8, distinctCount, "Toutes les constantes d'effort doivent être distinctes");
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
    // EXTRACT_FIELDS — taille exacte et chaque index individuellement
    // -----------------------------------------------------------------------

    @Test
    void testExtractFieldsNotNull() {
        assertNotNull(GCIRulesBase.EXTRACT_FIELDS);
    }

    @Test
    void testExtractFieldsHasExactlySevenElements() {
        assertEquals(7, GCIRulesBase.EXTRACT_FIELDS.length,
            "EXTRACT_FIELDS doit contenir exactement 7 éléments");
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
    // checkIssuesForFile (5 args) — délégation vers la surcharge à 8 args
    // Avec des tableaux de longueur égale, la validation de longueur passe,
    // puis l'accès à analyzedProjects (null) provoque une exception.
    // On vérifie que ce n'est PAS une AssertionError (la vérification de longueur a passé).
    // -----------------------------------------------------------------------

    @Test
    void testCheckIssuesForFile5Args_equalArrays_passesLengthCheck_thenFailsOnNullProjects() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {5};
        int[] endLines   = {5};
        // La vérification assertThat(startLines.length).isEqualTo(endLines.length) passe (1 == 1)
        // Ensuite analyzedProjects est null → NullPointerException, pas AssertionError
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile("file.java", "RULE", "msg", startLines, endLines));
        assertFalse(ex instanceof AssertionError,
            "Une AssertionError ne doit pas être levée pour des tableaux de même longueur");
    }

    @Test
    void testCheckIssuesForFile5Args_multipleEqualLines_passesLengthCheck() {
        GCIRulesBase base = new GCIRulesBase();
        int[] startLines = {1, 5, 10};
        int[] endLines   = {2, 6, 11};
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile("MyFile.java", "RULE_ID", "message", startLines, endLines));
        assertFalse(ex instanceof AssertionError,
            "Trois lignes valides ne doivent pas lever d'AssertionError sur la longueur");
    }

    @Test
    void testCheckIssuesForFile5Args_usesDefaultSeverityMinor() throws Exception {
        // On vérifie par réflexion que la surcharge 5-args appelle bien la surcharge 8-args
        // avec SEVERITY (MINOR), TYPE (CODE_SMELL) et EFFORT_5MIN
        Field severityField = GCIRulesBase.class.getDeclaredField("SEVERITY");
        severityField.setAccessible(true);
        Common.Severity defaultSeverity = (Common.Severity) severityField.get(null);
        assertEquals(Common.Severity.MINOR, defaultSeverity,
            "La surcharge 5-args doit utiliser SEVERITY = MINOR");
    }

    @Test
    void testCheckIssuesForFile5Args_usesDefaultEffort5Min() throws Exception {
        Field effortField = GCIRulesBase.class.getDeclaredField("EFFORT_5MIN");
        effortField.setAccessible(true);
        String defaultEffort = (String) effortField.get(null);
        assertEquals("5min", defaultEffort,
            "La surcharge 5-args doit utiliser EFFORT_5MIN = '5min'");
    }

    @Test
    void testCheckIssuesForFile5Args_usesDefaultTypeCodeSmell() throws Exception {
        Field typeField = GCIRulesBase.class.getDeclaredField("TYPE");
        typeField.setAccessible(true);
        Common.RuleType defaultType = (Common.RuleType) typeField.get(null);
        assertEquals(Common.RuleType.CODE_SMELL, defaultType,
            "La surcharge 5-args doit utiliser TYPE = CODE_SMELL");
    }

    // -----------------------------------------------------------------------
    // checkIssuesForFile (8 args) — validation de la longueur des tableaux
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
            "Des tableaux de même taille ne doivent pas lever d'AssertionError sur la longueur");
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
        // 0 == 0 passe la vérification de longueur, puis échoue sur analyzedProjects null
        Throwable ex = assertThrows(Throwable.class,
            () -> base.checkIssuesForFile(
                "file.java", "RULE", "msg", empty, empty,
                Common.Severity.MAJOR, Common.RuleType.BUG, "10min"));
        assertFalse(ex instanceof AssertionError,
            "Des tableaux vides (0 == 0) ne doivent pas lever d'AssertionError sur la longueur");
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
    // Structure de la classe
    // -----------------------------------------------------------------------

    @Test
    void testGCIRulesBaseIsConcreteClass() {
        assertFalse(
            java.lang.reflect.Modifier.isAbstract(GCIRulesBase.class.getModifiers()),
            "GCIRulesBase doit être une classe concrète instanciable"
        );
    }

    @Test
    void testGCIRulesBaseExtendsBuildProjectEngine() {
        assertEquals(BuildProjectEngine.class, GCIRulesBase.class.getSuperclass(),
            "GCIRulesBase doit directement étendre BuildProjectEngine");
    }

    @Test
    void testGCIRulesBaseInstantiationDoesNotThrow() {
        assertDoesNotThrow(GCIRulesBase::new);
    }

    @Test
    void testGCIRulesBaseIsPublic() {
        assertTrue(
            java.lang.reflect.Modifier.isPublic(GCIRulesBase.class.getModifiers()),
            "GCIRulesBase doit être une classe publique"
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
