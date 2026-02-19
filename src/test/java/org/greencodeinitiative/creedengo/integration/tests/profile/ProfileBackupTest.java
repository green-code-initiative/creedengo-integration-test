package org.greencodeinitiative.creedengo.integration.tests.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileBackupTest {

    @TempDir
    Path tempDir;

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Crée un fichier JSON de profil dans le répertoire temporaire et retourne son URI.
     */
    private URI createProfileJson(String name, String language, List<String> ruleKeys) throws IOException {
        String json = new ObjectMapper().writeValueAsString(
            new ProfileMetadata(name, language, ruleKeys)
        );
        Path jsonFile = tempDir.resolve("profile.json");
        Files.writeString(jsonFile, json, StandardCharsets.UTF_8);
        return jsonFile.toUri();
    }

    // -----------------------------------------------------------------------
    // Constante TEMPLATE_PROFIL — format text block (tabulations, inline <rules>)
    // -----------------------------------------------------------------------

    @Test
    void testTemplateProfil_containsNamePlaceholder() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"my-profile", "java", ""});
        assertTrue(xml.contains("<name>my-profile</name>"),
            "Le XML doit contenir la balise <name> avec la valeur");
    }

    @Test
    void testTemplateProfil_containsLanguagePlaceholder() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"profile", "python", ""});
        assertTrue(xml.contains("<language>python</language>"),
            "Le XML doit contenir la balise <language> avec la valeur");
    }

    @Test
    void testTemplateProfil_containsRulesSection() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"profile", "java", "<rule/>"});
        assertTrue(xml.contains("<rules>"),  "Le XML doit contenir la balise <rules>");
        assertTrue(xml.contains("</rules>"), "Le XML doit fermer la balise <rules>");
        assertTrue(xml.contains("<rule/>"),  "Le XML doit contenir les règles injectées");
    }

    @Test
    void testTemplateProfil_containsXmlHeader() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"p", "l", ""});
        assertTrue(xml.startsWith("<?xml"),
            "Le XML doit commencer par la déclaration XML");
    }

    @Test
    void testTemplateProfil_containsProfileTag() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"p", "l", ""});
        assertTrue(xml.contains("<profile>"),  "Le XML doit contenir la balise <profile>");
        assertTrue(xml.contains("</profile>"), "Le XML doit fermer la balise </profile>");
    }

    @Test
    void testTemplateProfil_nameIsIndentedWithTab() throws Exception {
        // Le text block utilise \t pour l'indentation des balises enfants
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"v", "l", ""});
        assertTrue(xml.contains("\t<name>"),
            "TEMPLATE_PROFIL (text block) doit indenter <name> avec une tabulation");
    }

    @Test
    void testTemplateProfil_languageIsIndentedWithTab() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"v", "l", ""});
        assertTrue(xml.contains("\t<language>"),
            "TEMPLATE_PROFIL (text block) doit indenter <language> avec une tabulation");
    }

    @Test
    void testTemplateProfil_rulesIsInlineNotMultiline() throws Exception {
        // Dans le text block : <rules>{2}</rules> est sur une seule ligne
        // → après formatage avec contenu vide, la balise ouvrante et fermante sont adjacentes
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"p", "l", ""});
        assertTrue(xml.contains("<rules></rules>"),
            "TEMPLATE_PROFIL (text block) doit avoir <rules></rules> inline (pas de saut de ligne entre les balises)");
    }

    @Test
    void testTemplateProfil_isTextBlock_doesNotUseConcatenation() throws Exception {
        // Vérifie que le template est bien un text block : il contient des sauts de ligne
        // (un text block produit toujours un String multi-lignes)
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        MessageFormat mf = (MessageFormat) f.get(null);
        String pattern = mf.toPattern();
        assertTrue(pattern.contains("\n"),
            "TEMPLATE_PROFIL doit être un text block multi-lignes");
    }

    // -----------------------------------------------------------------------
    // Constante TEMPLATE_RULE — format text block (tabulations, <parameters />)
    // -----------------------------------------------------------------------

    @Test
    void testTemplateRule_containsRepositoryKey() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"creedengo-java", "GCI1", "CODE_SMELL", "MINOR"});
        assertTrue(xml.contains("<repositoryKey>creedengo-java</repositoryKey>"));
    }

    @Test
    void testTemplateRule_containsKey() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "GCI42", "CODE_SMELL", "MINOR"});
        assertTrue(xml.contains("<key>GCI42</key>"));
    }

    @Test
    void testTemplateRule_containsType() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "BUG", "MAJOR"});
        assertTrue(xml.contains("<type>BUG</type>"));
    }

    @Test
    void testTemplateRule_containsPriority() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "CRITICAL"});
        assertTrue(xml.contains("<priority>CRITICAL</priority>"));
    }

    @Test
    void testTemplateRule_containsParametersTagWithSpace() throws Exception {
        // Le text block utilise "<parameters />" avec un espace avant le slash
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        assertTrue(xml.contains("<parameters />"),
            "TEMPLATE_RULE (text block) doit contenir <parameters /> avec un espace avant />");
    }

    @Test
    void testTemplateRule_parametersDoesNotUseCompactForm() throws Exception {
        // Le text block utilise "<parameters />" et PAS "<parameters/>" (sans espace)
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        // On retire d'abord la forme avec espace pour ne tester que la forme compacte
        String xmlWithoutSpaced = xml.replace("<parameters />", "");
        assertFalse(xmlWithoutSpaced.contains("<parameters/>"),
            "TEMPLATE_RULE ne doit pas contenir la forme compacte <parameters/> sans espace");
    }

    @Test
    void testTemplateRule_repositoryKeyIsIndentedWithTab() throws Exception {
        // Le text block indente chaque balise enfant de <rule> avec \t
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        assertTrue(xml.contains("\t<repositoryKey>"),
            "TEMPLATE_RULE (text block) doit indenter <repositoryKey> avec une tabulation");
    }

    @Test
    void testTemplateRule_startsWithRuleTag() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        assertTrue(xml.startsWith("<rule>"),
            "TEMPLATE_RULE (text block) doit commencer par la balise <rule>");
    }

    @Test
    void testTemplateRule_isTextBlock_doesNotUseConcatenation() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        MessageFormat mf = (MessageFormat) f.get(null);
        String pattern = mf.toPattern();
        assertTrue(pattern.contains("\n"),
            "TEMPLATE_RULE doit être un text block multi-lignes");
    }

    // -----------------------------------------------------------------------
    // language() et name() — lecture du JSON de profil
    // -----------------------------------------------------------------------

    @Test
    void testLanguageReturnsCorrectValue() throws IOException {
        URI profileUri = createProfileJson("my profile", "java", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        assertEquals("java", backup.language());
    }

    @Test
    void testNameReturnsCorrectValue() throws IOException {
        URI profileUri = createProfileJson("creedengo way", "python", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        assertEquals("creedengo way", backup.name());
    }

    @Test
    void testLanguageAndNameWithDifferentLanguage() throws IOException {
        URI profileUri = createProfileJson("my php profile", "php", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        assertEquals("php",            backup.language());
        assertEquals("my php profile", backup.name());
    }

    @Test
    void testMetadataIsCachedAfterFirstCall() throws IOException {
        URI profileUri = createProfileJson("cached profile", "java", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        // Deux appels doivent retourner la même valeur (cache interne)
        assertEquals(backup.language(), backup.language());
        assertEquals(backup.name(),     backup.name());
    }

    // -----------------------------------------------------------------------
    // Erreur lors du chargement du profil JSON
    // -----------------------------------------------------------------------

    @Test
    void testConstructorWithInvalidUriThrowsOnLanguageCall() {
        URI invalidUri = URI.create("file:///non/existent/profile.json");
        ProfileBackup backup = new ProfileBackup(invalidUri);
        RuntimeException ex = assertThrows(RuntimeException.class, backup::language);
        assertTrue(ex.getMessage().contains("Unable to load JSON Profile"),
            "Le message d'erreur doit indiquer 'Unable to load JSON Profile'");
    }

    @Test
    void testConstructorWithInvalidUriThrowsOnNameCall() {
        URI invalidUri = URI.create("file:///non/existent/profile.json");
        ProfileBackup backup = new ProfileBackup(invalidUri);
        assertThrows(RuntimeException.class, backup::name);
    }

    // -----------------------------------------------------------------------
    // profileDataUri() — base64 + format data URI
    // -----------------------------------------------------------------------

    @Test
    void testProfileDataUriIsDataUrl() throws IOException {
        // Profil sans règle (ruleKeys vide) → xmlProfile() ne cherche pas de ressource classpath
        URI profileUri = createProfileJson("test profile", "java", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        URL dataUri = backup.profileDataUri();
        assertNotNull(dataUri);
        assertTrue(dataUri.toString().startsWith("data:text/xml;base64,"),
            "L'URL doit être au format data URI base64");
    }

    @Test
    void testProfileDataUriDecodesCorrectXml() throws IOException {
        URI profileUri = createProfileJson("decoded profile", "js", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        URL dataUri = backup.profileDataUri();

        String raw   = dataUri.toString();
        String b64   = raw.substring("data:text/xml;base64,".length());
        String xml   = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);

        assertTrue(xml.contains("<name>decoded profile</name>"), "Le XML décodé doit contenir le nom du profil");
        assertTrue(xml.contains("<language>js</language>"),      "Le XML décodé doit contenir le langage");
        assertTrue(xml.contains("<rules>"),                       "Le XML décodé doit contenir la section des règles");
    }

    @Test
    void testProfileDataUriForInvalidProfileThrowsRuntimeException() {
        URI invalidUri = URI.create("file:///non/existent/profile.json");
        ProfileBackup backup = new ProfileBackup(invalidUri);
        assertThrows(RuntimeException.class, backup::profileDataUri);
    }

    // -----------------------------------------------------------------------
    // xmlProfile() + loadRule() — avec de vraies ressources classpath de test
    // (src/test/resources/org/green-code-initiative/rules/java/GCI1.json, GCI2.json)
    // -----------------------------------------------------------------------

    @Test
    void testProfileDataUri_withOneRule_containsRuleXml() throws IOException {
        URI profileUri = createProfileJson("java profile", "java", List.of("GCI1"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        URL dataUri = backup.profileDataUri();
        String xml = decodeDataUri(dataUri);

        // repositoryKey = "creedengo-" + language
        assertTrue(xml.contains("<repositoryKey>creedengo-java</repositoryKey>"),
            "Le XML doit contenir le repositoryKey creedengo-java");
        // clé de la règle
        assertTrue(xml.contains("<key>GCI1</key>"),
            "Le XML doit contenir la clé de règle GCI1");
        // type issu du fichier JSON
        assertTrue(xml.contains("<type>CODE_SMELL</type>"),
            "Le XML doit contenir le type CODE_SMELL");
        // sévérité en MAJUSCULES (toUpperCase appliqué sur 'minor')
        assertTrue(xml.contains("<priority>MINOR</priority>"),
            "La sévérité 'minor' doit être convertie en 'MINOR' via toUpperCase()");
    }

    @Test
    void testProfileDataUri_withTwoRules_containsBothRules() throws IOException {
        URI profileUri = createProfileJson("java two rules", "java", List.of("GCI1", "GCI2"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<key>GCI1</key>"), "Le XML doit contenir GCI1");
        assertTrue(xml.contains("<key>GCI2</key>"), "Le XML doit contenir GCI2");
        // GCI2 a type=BUG et severity=major → MAJOR
        assertTrue(xml.contains("<type>BUG</type>"),        "Le XML doit contenir le type BUG pour GCI2");
        assertTrue(xml.contains("<priority>MAJOR</priority>"), "La sévérité 'major' doit être MAJOR");
    }

    @Test
    void testProfileDataUri_withRule_repositoryKeyUsesPythonLanguage() throws IOException {
        // python/GCI5.json doit exister dans les ressources de test
        URI profileUri = createProfileJson("python profile", "python", List.of("GCI5"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<repositoryKey>creedengo-python</repositoryKey>"),
            "Le repositoryKey doit être creedengo-python pour le langage python");
        assertTrue(xml.contains("<key>GCI5</key>"), "Le XML doit contenir la clé GCI5");
        // GCI5 a severity=critical → CRITICAL
        assertTrue(xml.contains("<priority>CRITICAL</priority>"),
            "La sévérité 'critical' doit être convertie en CRITICAL");
    }

    @Test
    void testProfileDataUri_withRule_rulesTagContainsRuleTag() throws IOException {
        URI profileUri = createProfileJson("profile with rule", "java", List.of("GCI1"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<rule>"),  "Le XML doit contenir la balise ouvrante <rule>");
        assertTrue(xml.contains("</rule>"), "Le XML doit contenir la balise fermante </rule>");
        assertTrue(xml.contains("<parameters />"),
            "Le XML doit contenir la balise <parameters />");
    }

    @Test
    void testProfileDataUri_withRule_xmlContainsProfileNameAndLanguage() throws IOException {
        URI profileUri = createProfileJson("mon profil java", "java", List.of("GCI1"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<name>mon profil java</name>"),
            "Le XML doit contenir le nom du profil");
        assertTrue(xml.contains("<language>java</language>"),
            "Le XML doit contenir le langage");
    }

    // -----------------------------------------------------------------------
    // loadRule() — erreur : ressource classpath introuvable
    // -----------------------------------------------------------------------

    @Test
    void testProfileDataUri_withUnknownRule_throwsRuntimeException() throws IOException {
        // La règle RULE_INCONNUE n'existe pas dans le classpath
        URI profileUri = createProfileJson("bad profile", "java", List.of("RULE_INCONNUE"));
        ProfileBackup backup = new ProfileBackup(profileUri);
        assertThrows(RuntimeException.class, backup::profileDataUri,
            "loadRule() doit lever une RuntimeException si la ressource JSON de règle est introuvable");
    }

    // -----------------------------------------------------------------------
    // Cache de profileMetadata() — branche "profileMetadata != null"
    // -----------------------------------------------------------------------

    @Test
    void testProfileMetadataCache_secondCallReturnsSameLanguage() throws IOException {
        URI profileUri = createProfileJson("cached", "java", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        // 1er appel : charge et met en cache
        String first = backup.language();
        // 2e appel : utilise le cache (branche if == null → false)
        String second = backup.language();
        assertEquals(first, second, "Les deux appels doivent retourner la même valeur depuis le cache");
    }

    @Test
    void testProfileMetadataCache_languageThenName_useSameCache() throws IOException {
        URI profileUri = createProfileJson("profil cache", "js", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        // language() charge le cache, name() doit le réutiliser sans relire le fichier
        assertEquals("js",          backup.language());
        assertEquals("profil cache", backup.name());
    }

    // -----------------------------------------------------------------------
    // Helpers privés du test
    // -----------------------------------------------------------------------

    /** Décode un data URI base64 en String XML. */
    private String decodeDataUri(URL dataUri) {
        String raw = dataUri.toString();
        String b64 = raw.substring("data:text/xml;base64,".length());
        return new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
    }

    // -----------------------------------------------------------------------
    // Structure de la classe
    // -----------------------------------------------------------------------

    @Test
    void testProfileBackupIsPublicClass() {
        assertTrue(java.lang.reflect.Modifier.isPublic(ProfileBackup.class.getModifiers()));
    }

    @Test
    void testProfileBackupHasPublicConstructor() throws NoSuchMethodException {
        assertNotNull(ProfileBackup.class.getConstructor(URI.class));
    }
}
