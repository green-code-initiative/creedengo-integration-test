package org.greencodeinitiative.creedengo.integration.tests.profile;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RuleMetadataTest {

    // -----------------------------------------------------------------------
    // Constructeur canonique et accesseurs du record
    // -----------------------------------------------------------------------

    @Test
    void testConstructorAndAccessors() {
        RuleMetadata rule = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        assertEquals("GCI1",       rule.key());
        assertEquals("CODE_SMELL", rule.type());
        assertEquals("MINOR",      rule.defaultSeverity());
    }

    @Test
    void testConstructorWithNullKey() {
        RuleMetadata rule = new RuleMetadata(null, "CODE_SMELL", "MINOR");
        assertNull(rule.key());
    }

    @Test
    void testConstructorWithNullType() {
        RuleMetadata rule = new RuleMetadata("GCI1", null, "MINOR");
        assertNull(rule.type());
    }

    @Test
    void testConstructorWithNullSeverity() {
        RuleMetadata rule = new RuleMetadata("GCI1", "CODE_SMELL", null);
        assertNull(rule.defaultSeverity());
    }

    @Test
    void testConstructorAllNulls() {
        RuleMetadata rule = new RuleMetadata(null, null, null);
        assertNull(rule.key());
        assertNull(rule.type());
        assertNull(rule.defaultSeverity());
    }

    @Test
    void testAllSeverityValues() {
        for (String severity : new String[]{"INFO", "MINOR", "MAJOR", "CRITICAL", "BLOCKER"}) {
            RuleMetadata rule = new RuleMetadata("GCI1", "CODE_SMELL", severity);
            assertEquals(severity, rule.defaultSeverity());
        }
    }

    @Test
    void testAllTypeValues() {
        for (String type : new String[]{"CODE_SMELL", "BUG", "VULNERABILITY", "SECURITY_HOTSPOT"}) {
            RuleMetadata rule = new RuleMetadata("GCI1", type, "MINOR");
            assertEquals(type, rule.type());
        }
    }

    // -----------------------------------------------------------------------
    // equals & hashCode — générés nativement par le record
    // -----------------------------------------------------------------------

    @Test
    void testEqualsSameValues() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        RuleMetadata b = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsDifferentKey() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        RuleMetadata b = new RuleMetadata("GCI2", "CODE_SMELL", "MINOR");
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentType() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        RuleMetadata b = new RuleMetadata("GCI1", "BUG",        "MINOR");
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentSeverity() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        RuleMetadata b = new RuleMetadata("GCI1", "CODE_SMELL", "MAJOR");
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNull() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        assertNotEquals(null, a);
    }

    @Test
    void testEqualsWithDifferentObject() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        assertNotEquals("a string", a);
    }

    @Test
    void testEqualsAndHashCodeContract() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        RuleMetadata b = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode(), "Deux records egaux doivent avoir le meme hashCode");
    }

    @Test
    void testHashCodeConsistency() {
        RuleMetadata a = new RuleMetadata("GCI1", "CODE_SMELL", "MINOR");
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void testHashCodeForNullFields() {
        RuleMetadata a = new RuleMetadata(null, null, null);
        assertDoesNotThrow(a::hashCode);
    }

    // -----------------------------------------------------------------------
    // toString — généré nativement par le record
    // -----------------------------------------------------------------------

    @Test
    void testToStringContainsFieldValues() {
        RuleMetadata rule = new RuleMetadata("GCI99", "BUG", "MAJOR");
        String str = rule.toString();
        assertTrue(str.contains("GCI99"), "toString doit contenir la cle");
        assertTrue(str.contains("BUG"),   "toString doit contenir le type");
        assertTrue(str.contains("MAJOR"), "toString doit contenir la severite");
    }

    @Test
    void testToStringWithNullFields() {
        RuleMetadata rule = new RuleMetadata(null, null, null);
        String str = rule.toString();
        assertNotNull(str);
        assertTrue(str.contains("null"));
    }

    // -----------------------------------------------------------------------
    // Structure du record
    // -----------------------------------------------------------------------

    @Test
    void testIsRecord() {
        assertTrue(RuleMetadata.class.isRecord(), "RuleMetadata doit etre un record Java");
    }

    @Test
    void testRecordComponentCount() {
        assertEquals(3, RuleMetadata.class.getRecordComponents().length,
            "RuleMetadata doit avoir exactement 3 composants");
    }

    @Test
    void testRecordComponentNames() {
        var components = RuleMetadata.class.getRecordComponents();
        assertEquals("key",             components[0].getName());
        assertEquals("type",            components[1].getName());
        assertEquals("defaultSeverity", components[2].getName());
    }

    @Test
    void testRecordHasNoSetters() {
        long setterCount = Arrays.stream(RuleMetadata.class.getMethods())
            .filter(m -> m.getName().startsWith("set"))
            .count();
        assertEquals(0, setterCount, "Un record ne doit pas avoir de setters");
    }
}
