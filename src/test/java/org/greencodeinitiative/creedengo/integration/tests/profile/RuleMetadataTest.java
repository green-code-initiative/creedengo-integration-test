package org.greencodeinitiative.creedengo.integration.tests.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleMetadataTest {

    // -----------------------------------------------------------------------
    // Getters / Setters (générés par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testGettersAndSetters() {
        RuleMetadata rule = new RuleMetadata();
        rule.setKey("GCI1");
        rule.setType("CODE_SMELL");
        rule.setDefaultSeverity("MINOR");

        assertEquals("GCI1",       rule.getKey());
        assertEquals("CODE_SMELL", rule.getType());
        assertEquals("MINOR",      rule.getDefaultSeverity());
    }

    @Test
    void testDefaultConstructorProducesNullFields() {
        RuleMetadata rule = new RuleMetadata();
        assertNull(rule.getKey());
        assertNull(rule.getType());
        assertNull(rule.getDefaultSeverity());
    }

    @Test
    void testSetKeyNull() {
        RuleMetadata rule = new RuleMetadata();
        rule.setKey(null);
        assertNull(rule.getKey());
    }

    @Test
    void testSetTypeNull() {
        RuleMetadata rule = new RuleMetadata();
        rule.setType(null);
        assertNull(rule.getType());
    }

    @Test
    void testSetDefaultSeverityNull() {
        RuleMetadata rule = new RuleMetadata();
        rule.setDefaultSeverity(null);
        assertNull(rule.getDefaultSeverity());
    }

    @Test
    void testSetAllSeverityValues() {
        RuleMetadata rule = new RuleMetadata();
        for (String severity : new String[]{"INFO", "MINOR", "MAJOR", "CRITICAL", "BLOCKER"}) {
            rule.setDefaultSeverity(severity);
            assertEquals(severity, rule.getDefaultSeverity());
        }
    }

    @Test
    void testSetAllTypeValues() {
        RuleMetadata rule = new RuleMetadata();
        for (String type : new String[]{"CODE_SMELL", "BUG", "VULNERABILITY", "SECURITY_HOTSPOT"}) {
            rule.setType(type);
            assertEquals(type, rule.getType());
        }
    }

    // -----------------------------------------------------------------------
    // equals & hashCode (générés par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testEqualsSameValues() {
        RuleMetadata a = new RuleMetadata();
        a.setKey("GCI1");
        a.setType("CODE_SMELL");
        a.setDefaultSeverity("MINOR");

        RuleMetadata b = new RuleMetadata();
        b.setKey("GCI1");
        b.setType("CODE_SMELL");
        b.setDefaultSeverity("MINOR");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsDifferentKey() {
        RuleMetadata a = new RuleMetadata();
        a.setKey("GCI1");

        RuleMetadata b = new RuleMetadata();
        b.setKey("GCI2");

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentType() {
        RuleMetadata a = new RuleMetadata();
        a.setKey("GCI1");
        a.setType("CODE_SMELL");

        RuleMetadata b = new RuleMetadata();
        b.setKey("GCI1");
        b.setType("BUG");

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentSeverity() {
        RuleMetadata a = new RuleMetadata();
        a.setKey("GCI1");
        a.setDefaultSeverity("MINOR");

        RuleMetadata b = new RuleMetadata();
        b.setKey("GCI1");
        b.setDefaultSeverity("MAJOR");

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNull() {
        RuleMetadata a = new RuleMetadata();
        assertNotEquals(null, a);
    }

    @Test
    void testEqualsWithDifferentType() {
        RuleMetadata a = new RuleMetadata();
        assertNotEquals("a string", a);
    }

    @Test
    void testEqualsSelf() {
        RuleMetadata a = new RuleMetadata();
        a.setKey("GCI1");
        // Réflexivité : un objet est égal à lui-même
        assertTrue(a.equals(a));
    }

    @Test
    void testHashCodeConsistency() {
        RuleMetadata a = new RuleMetadata();
        a.setKey("GCI1");
        a.setType("CODE_SMELL");
        a.setDefaultSeverity("MINOR");
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void testHashCodeForNullFields() {
        RuleMetadata a = new RuleMetadata();
        // ne doit pas lancer de NullPointerException
        assertDoesNotThrow(a::hashCode);
    }

    // -----------------------------------------------------------------------
    // toString (généré par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testToStringContainsFieldValues() {
        RuleMetadata rule = new RuleMetadata();
        rule.setKey("GCI99");
        rule.setType("BUG");
        rule.setDefaultSeverity("MAJOR");

        String str = rule.toString();
        assertTrue(str.contains("GCI99"), "toString doit contenir la clé");
        assertTrue(str.contains("BUG"),   "toString doit contenir le type");
        assertTrue(str.contains("MAJOR"), "toString doit contenir la sévérité");
    }

    @Test
    void testToStringWithNullFields() {
        RuleMetadata rule = new RuleMetadata();
        String str = rule.toString();
        assertNotNull(str);
        // Lombok génère "null" pour les champs null
        assertTrue(str.contains("null"));
    }

    // -----------------------------------------------------------------------
    // canEqual (généré par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testCanEqualSameType() throws Exception {
        RuleMetadata a = new RuleMetadata();
        RuleMetadata b = new RuleMetadata();
        java.lang.reflect.Method canEqual = RuleMetadata.class.getDeclaredMethod("canEqual", Object.class);
        canEqual.setAccessible(true);
        assertTrue((Boolean) canEqual.invoke(a, b));
    }

    @Test
    void testCanEqualDifferentType() throws Exception {
        RuleMetadata a = new RuleMetadata();
        java.lang.reflect.Method canEqual = RuleMetadata.class.getDeclaredMethod("canEqual", Object.class);
        canEqual.setAccessible(true);
        assertFalse((Boolean) canEqual.invoke(a, "a string"));
    }
}

