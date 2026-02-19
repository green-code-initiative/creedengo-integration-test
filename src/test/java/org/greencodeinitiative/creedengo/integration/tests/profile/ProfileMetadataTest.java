package org.greencodeinitiative.creedengo.integration.tests.profile;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileMetadataTest {

    // -----------------------------------------------------------------------
    // Getters / Setters (générés par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testGettersAndSetters() {
        ProfileMetadata metadata = new ProfileMetadata();
        metadata.setName("creedengo way");
        metadata.setLanguage("java");
        metadata.setRuleKeys(Arrays.asList("GCI1", "GCI2"));

        assertEquals("creedengo way", metadata.getName());
        assertEquals("java", metadata.getLanguage());
        assertEquals(Arrays.asList("GCI1", "GCI2"), metadata.getRuleKeys());
    }

    @Test
    void testDefaultConstructorProducesNullFields() {
        ProfileMetadata metadata = new ProfileMetadata();
        assertNull(metadata.getName());
        assertNull(metadata.getLanguage());
        assertNull(metadata.getRuleKeys());
    }

    @Test
    void testSetNameNull() {
        ProfileMetadata metadata = new ProfileMetadata();
        metadata.setName(null);
        assertNull(metadata.getName());
    }

    @Test
    void testSetLanguageNull() {
        ProfileMetadata metadata = new ProfileMetadata();
        metadata.setLanguage(null);
        assertNull(metadata.getLanguage());
    }

    @Test
    void testSetRuleKeysEmpty() {
        ProfileMetadata metadata = new ProfileMetadata();
        metadata.setRuleKeys(Collections.emptyList());
        assertTrue(metadata.getRuleKeys().isEmpty());
    }

    @Test
    void testSetRuleKeysSingleItem() {
        ProfileMetadata metadata = new ProfileMetadata();
        metadata.setRuleKeys(List.of("GCI42"));
        assertEquals(1, metadata.getRuleKeys().size());
        assertEquals("GCI42", metadata.getRuleKeys().get(0));
    }

    // -----------------------------------------------------------------------
    // equals & hashCode (générés par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testEqualsSameValues() {
        ProfileMetadata a = new ProfileMetadata();
        a.setName("creedengo way");
        a.setLanguage("java");
        a.setRuleKeys(Arrays.asList("GCI1", "GCI2"));

        ProfileMetadata b = new ProfileMetadata();
        b.setName("creedengo way");
        b.setLanguage("java");
        b.setRuleKeys(Arrays.asList("GCI1", "GCI2"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsDifferentName() {
        ProfileMetadata a = new ProfileMetadata();
        a.setName("name-A");
        a.setLanguage("java");
        a.setRuleKeys(Collections.emptyList());

        ProfileMetadata b = new ProfileMetadata();
        b.setName("name-B");
        b.setLanguage("java");
        b.setRuleKeys(Collections.emptyList());

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentLanguage() {
        ProfileMetadata a = new ProfileMetadata();
        a.setName("profile");
        a.setLanguage("java");
        a.setRuleKeys(Collections.emptyList());

        ProfileMetadata b = new ProfileMetadata();
        b.setName("profile");
        b.setLanguage("python");
        b.setRuleKeys(Collections.emptyList());

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentRuleKeys() {
        ProfileMetadata a = new ProfileMetadata();
        a.setName("profile");
        a.setLanguage("java");
        a.setRuleKeys(List.of("GCI1"));

        ProfileMetadata b = new ProfileMetadata();
        b.setName("profile");
        b.setLanguage("java");
        b.setRuleKeys(List.of("GCI2"));

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNull() {
        ProfileMetadata a = new ProfileMetadata();
        assertNotEquals(null, a);
    }

    @Test
    void testEqualsWithDifferentType() {
        ProfileMetadata a = new ProfileMetadata();
        assertNotEquals("a string", a);
    }

    @Test
    void testEqualsSelf() {
        ProfileMetadata a = new ProfileMetadata();
        a.setName("x");
        // Réflexivité : un objet est égal à lui-même
        assertTrue(a.equals(a));
    }

    @Test
    void testHashCodeConsistency() {
        ProfileMetadata a = new ProfileMetadata();
        a.setName("creedengo way");
        a.setLanguage("java");
        a.setRuleKeys(List.of("GCI1"));
        assertEquals(a.hashCode(), a.hashCode());
    }

    // -----------------------------------------------------------------------
    // toString (généré par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testToStringContainsFieldValues() {
        ProfileMetadata metadata = new ProfileMetadata();
        metadata.setName("creedengo way");
        metadata.setLanguage("java");
        metadata.setRuleKeys(Arrays.asList("GCI1", "GCI2"));

        String str = metadata.toString();
        assertTrue(str.contains("creedengo way"), "toString doit contenir le nom");
        assertTrue(str.contains("java"),           "toString doit contenir le langage");
        assertTrue(str.contains("GCI1"),           "toString doit contenir les ruleKeys");
        assertTrue(str.contains("GCI2"),           "toString doit contenir les ruleKeys");
    }

    @Test
    void testToStringWithNullFields() {
        ProfileMetadata metadata = new ProfileMetadata();
        String str = metadata.toString();
        assertNotNull(str);
        // Lombok génère "null" pour les champs null
        assertTrue(str.contains("null"));
    }

    // -----------------------------------------------------------------------
    // canEqual (généré par Lombok @Data)
    // -----------------------------------------------------------------------

    @Test
    void testCanEqualSameType() throws Exception {
        ProfileMetadata a = new ProfileMetadata();
        ProfileMetadata b = new ProfileMetadata();
        java.lang.reflect.Method canEqual = ProfileMetadata.class.getDeclaredMethod("canEqual", Object.class);
        canEqual.setAccessible(true);
        assertTrue((Boolean) canEqual.invoke(a, b));
    }

    @Test
    void testCanEqualDifferentType() throws Exception {
        ProfileMetadata a = new ProfileMetadata();
        java.lang.reflect.Method canEqual = ProfileMetadata.class.getDeclaredMethod("canEqual", Object.class);
        canEqual.setAccessible(true);
        assertFalse((Boolean) canEqual.invoke(a, "a string"));
    }
}

