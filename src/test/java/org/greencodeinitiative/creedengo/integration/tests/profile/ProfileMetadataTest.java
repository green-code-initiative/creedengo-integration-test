package org.greencodeinitiative.creedengo.integration.tests.profile;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileMetadataTest {

    // -----------------------------------------------------------------------
    // Constructeur canonique et accesseurs du record
    // -----------------------------------------------------------------------

    @Test
    void testConstructorAndAccessors() {
        ProfileMetadata metadata = new ProfileMetadata("creedengo way", "java", List.of("GCI1", "GCI2"));
        assertEquals("creedengo way",            metadata.name());
        assertEquals("java",                     metadata.language());
        assertEquals(List.of("GCI1", "GCI2"),    metadata.ruleKeys());
    }

    @Test
    void testConstructorWithNullName() {
        ProfileMetadata metadata = new ProfileMetadata(null, "java", List.of());
        assertNull(metadata.name());
    }

    @Test
    void testConstructorWithNullLanguage() {
        ProfileMetadata metadata = new ProfileMetadata("profile", null, List.of());
        assertNull(metadata.language());
    }

    @Test
    void testConstructorWithNullRuleKeys() {
        ProfileMetadata metadata = new ProfileMetadata("profile", "java", null);
        assertNull(metadata.ruleKeys());
    }

    @Test
    void testConstructorWithEmptyRuleKeys() {
        ProfileMetadata metadata = new ProfileMetadata("profile", "java", Collections.emptyList());
        assertTrue(metadata.ruleKeys().isEmpty());
    }

    @Test
    void testConstructorWithSingleRuleKey() {
        ProfileMetadata metadata = new ProfileMetadata("profile", "java", List.of("GCI42"));
        assertEquals(1,       metadata.ruleKeys().size());
        assertEquals("GCI42", metadata.ruleKeys().get(0));
    }

    // -----------------------------------------------------------------------
    // equals & hashCode — générés nativement par le record
    // -----------------------------------------------------------------------

    @Test
    void testEqualsSameValues() {
        ProfileMetadata a = new ProfileMetadata("creedengo way", "java", List.of("GCI1", "GCI2"));
        ProfileMetadata b = new ProfileMetadata("creedengo way", "java", List.of("GCI1", "GCI2"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEqualsDifferentName() {
        ProfileMetadata a = new ProfileMetadata("name-A", "java", Collections.emptyList());
        ProfileMetadata b = new ProfileMetadata("name-B", "java", Collections.emptyList());
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentLanguage() {
        ProfileMetadata a = new ProfileMetadata("profile", "java",   Collections.emptyList());
        ProfileMetadata b = new ProfileMetadata("profile", "python", Collections.emptyList());
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsDifferentRuleKeys() {
        ProfileMetadata a = new ProfileMetadata("profile", "java", List.of("GCI1"));
        ProfileMetadata b = new ProfileMetadata("profile", "java", List.of("GCI2"));
        assertNotEquals(a, b);
    }

    @Test
    void testEqualsWithNull() {
        ProfileMetadata a = new ProfileMetadata("profile", "java", List.of());
        assertNotEquals(null, a);
    }

    @Test
    void testEqualsWithDifferentType() {
        ProfileMetadata a = new ProfileMetadata("profile", "java", List.of());
        assertNotEquals("a string", a);
    }

    @Test
    void testEqualsAndHashCodeContract() {
        // Contrat Java : deux objets égaux doivent avoir le même hashCode
        ProfileMetadata a = new ProfileMetadata("x", "java", List.of("GCI1"));
        ProfileMetadata b = new ProfileMetadata("x", "java", List.of("GCI1"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode(),
            "Deux records égaux doivent avoir le même hashCode (contrat Java)");
    }

    @Test
    void testHashCodeConsistency() {
        ProfileMetadata a = new ProfileMetadata("creedengo way", "java", List.of("GCI1"));
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void testHashCodeForNullFields() {
        ProfileMetadata a = new ProfileMetadata(null, null, null);
        assertDoesNotThrow(a::hashCode);
    }

    // -----------------------------------------------------------------------
    // toString — généré nativement par le record
    // -----------------------------------------------------------------------

    @Test
    void testToStringContainsFieldValues() {
        ProfileMetadata metadata = new ProfileMetadata("creedengo way", "java", List.of("GCI1", "GCI2"));
        String str = metadata.toString();
        assertTrue(str.contains("creedengo way"), "toString doit contenir le nom");
        assertTrue(str.contains("java"),           "toString doit contenir le langage");
        assertTrue(str.contains("GCI1"),           "toString doit contenir les ruleKeys");
        assertTrue(str.contains("GCI2"),           "toString doit contenir les ruleKeys");
    }

    @Test
    void testToStringRecordFormat() {
        // Le toString d'un record Java suit le format :
        // NomDuRecord[composant1=valeur1, composant2=valeur2, ...]
        ProfileMetadata metadata = new ProfileMetadata("creedengo way", "java", List.of("GCI1"));
        String str = metadata.toString();
        assertEquals(
            "ProfileMetadata[name=creedengo way, language=java, ruleKeys=[GCI1]]",
            str,
            "Le toString d'un record doit suivre le format NomRecord[champ=valeur, ...]"
        );
    }

    @Test
    void testToStringWithNullFields() {
        ProfileMetadata metadata = new ProfileMetadata(null, null, null);
        String str = metadata.toString();
        assertNotNull(str);
        assertTrue(str.contains("null"));
    }

    // -----------------------------------------------------------------------
    // Structure du record
    // -----------------------------------------------------------------------

    @Test
    void testIsRecord() {
        assertTrue(ProfileMetadata.class.isRecord(),
            "ProfileMetadata doit être un record Java");
    }

    @Test
    void testRecordComponentCount() {
        assertEquals(3, ProfileMetadata.class.getRecordComponents().length,
            "ProfileMetadata doit avoir exactement 3 composants");
    }

    @Test
    void testRecordComponentNames() {
        var components = ProfileMetadata.class.getRecordComponents();
        assertEquals("name",     components[0].getName());
        assertEquals("language", components[1].getName());
        assertEquals("ruleKeys", components[2].getName());
    }

    @Test
    void testRecordHasNoSetters() {
        long setterCount = Arrays.stream(ProfileMetadata.class.getMethods())
            .filter(m -> m.getName().startsWith("set"))
            .count();
        assertEquals(0, setterCount, "Un record ne doit pas avoir de setters");
    }

}


