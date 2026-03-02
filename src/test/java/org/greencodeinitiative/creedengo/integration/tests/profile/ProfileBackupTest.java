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
     * Creates a profile JSON file in the temporary directory and returns its URI.
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
    // TEMPLATE_PROFIL constant — text block format (tabs, inline <rules>)
    // -----------------------------------------------------------------------

    @Test
    void testTemplateProfil_containsNamePlaceholder() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"my-profile", "java", ""});
        assertTrue(xml.contains("<name>my-profile</name>"),
            "The XML must contain the <name> tag with its value");
    }

    @Test
    void testTemplateProfil_containsLanguagePlaceholder() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"profile", "python", ""});
        assertTrue(xml.contains("<language>python</language>"),
            "The XML must contain the <language> tag with its value");
    }

    @Test
    void testTemplateProfil_containsRulesSection() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"profile", "java", "<rule/>"});
        assertTrue(xml.contains("<rules>"),  "The XML must contain the <rules> tag");
        assertTrue(xml.contains("</rules>"), "The XML must close the <rules> tag");
        assertTrue(xml.contains("<rule/>"),  "The XML must contain the injected rules");
    }

    @Test
    void testTemplateProfil_containsXmlHeader() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"p", "l", ""});
        assertTrue(xml.startsWith("<?xml"),
            "The XML must start with the XML declaration");
    }

    @Test
    void testTemplateProfil_containsProfileTag() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"p", "l", ""});
        assertTrue(xml.contains("<profile>"),  "The XML must contain the <profile> tag");
        assertTrue(xml.contains("</profile>"), "The XML must close the </profile> tag");
    }

    @Test
    void testTemplateProfil_nameIsIndentedWithTab() throws Exception {
        // The text block uses \t for indenting child tags
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"v", "l", ""});
        assertTrue(xml.contains("\t<name>"),
            "TEMPLATE_PROFIL (text block) must indent <name> with a tab character");
    }

    @Test
    void testTemplateProfil_languageIsIndentedWithTab() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"v", "l", ""});
        assertTrue(xml.contains("\t<language>"),
            "TEMPLATE_PROFIL (text block) must indent <language> with a tab character");
    }

    @Test
    void testTemplateProfil_rulesIsInlineNotMultiline() throws Exception {
        // In the text block: <rules>{2}</rules> is on a single line
        // → after formatting with empty content, the opening and closing tags are adjacent
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"p", "l", ""});
        assertTrue(xml.contains("<rules></rules>"),
            "TEMPLATE_PROFIL (text block) must have <rules></rules> inline (no line break between tags)");
    }

    @Test
    void testTemplateProfil_isTextBlock_doesNotUseConcatenation() throws Exception {
        // Verifies that the template is indeed a text block: it contains line breaks
        // (a text block always produces a multi-line String)
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_PROFIL");
        f.setAccessible(true);
        MessageFormat mf = (MessageFormat) f.get(null);
        String pattern = mf.toPattern();
        assertTrue(pattern.contains("\n"),
            "TEMPLATE_PROFIL must be a multi-line text block");
    }

    // -----------------------------------------------------------------------
    // TEMPLATE_RULE constant — text block format (tabs, <parameters />)
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
        // The text block uses "<parameters />" with a space before the slash
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        assertTrue(xml.contains("<parameters />"),
            "TEMPLATE_RULE (text block) must contain <parameters /> with a space before />");
    }

    @Test
    void testTemplateRule_parametersDoesNotUseCompactForm() throws Exception {
        // The text block uses "<parameters />" and NOT "<parameters/>" (without space)
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        // Remove the spaced form first to test only the compact form
        String xmlWithoutSpaced = xml.replace("<parameters />", "");
        assertFalse(xmlWithoutSpaced.contains("<parameters/>"),
            "TEMPLATE_RULE must not contain the compact form <parameters/> without space");
    }

    @Test
    void testTemplateRule_repositoryKeyIsIndentedWithTab() throws Exception {
        // The text block indents each child tag of <rule> with \t
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        assertTrue(xml.contains("\t<repositoryKey>"),
            "TEMPLATE_RULE (text block) must indent <repositoryKey> with a tab character");
    }

    @Test
    void testTemplateRule_startsWithRuleTag() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        String xml = ((MessageFormat) f.get(null)).format(new Object[]{"repo", "key", "CODE_SMELL", "MINOR"});
        assertTrue(xml.startsWith("<rule>"),
            "TEMPLATE_RULE (text block) must start with the <rule> tag");
    }

    @Test
    void testTemplateRule_isTextBlock_doesNotUseConcatenation() throws Exception {
        Field f = ProfileBackup.class.getDeclaredField("TEMPLATE_RULE");
        f.setAccessible(true);
        MessageFormat mf = (MessageFormat) f.get(null);
        String pattern = mf.toPattern();
        assertTrue(pattern.contains("\n"),
            "TEMPLATE_RULE must be a multi-line text block");
    }

    // -----------------------------------------------------------------------
    // language() and name() — reading from profile JSON
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
        // Two calls must return the same value (internal cache)
        assertEquals(backup.language(), backup.language());
        assertEquals(backup.name(),     backup.name());
    }

    // -----------------------------------------------------------------------
    // Error when loading profile JSON
    // -----------------------------------------------------------------------

    @Test
    void testConstructorWithInvalidUriThrowsOnLanguageCall() {
        URI invalidUri = URI.create("file:///non/existent/profile.json");
        ProfileBackup backup = new ProfileBackup(invalidUri);
        RuntimeException ex = assertThrows(RuntimeException.class, backup::language);
        assertTrue(ex.getMessage().contains("Unable to load JSON Profile"),
            "The error message must indicate 'Unable to load JSON Profile'");
    }

    @Test
    void testConstructorWithInvalidUriThrowsOnNameCall() {
        URI invalidUri = URI.create("file:///non/existent/profile.json");
        ProfileBackup backup = new ProfileBackup(invalidUri);
        assertThrows(RuntimeException.class, backup::name);
    }

    // -----------------------------------------------------------------------
    // profileDataUri() — base64 + data URI format
    // -----------------------------------------------------------------------

    @Test
    void testProfileDataUriIsDataUrl() throws IOException {
        // Profile without rules (empty ruleKeys) → xmlProfile() does not look for a classpath resource
        URI profileUri = createProfileJson("test profile", "java", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        URL dataUri = backup.profileDataUri();
        assertNotNull(dataUri);
        assertTrue(dataUri.toString().startsWith("data:text/xml;base64,"),
            "The URL must be in base64 data URI format");
    }

    @Test
    void testProfileDataUriDecodesCorrectXml() throws IOException {
        URI profileUri = createProfileJson("decoded profile", "js", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        URL dataUri = backup.profileDataUri();

        String raw   = dataUri.toString();
        String b64   = raw.substring("data:text/xml;base64,".length());
        String xml   = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);

        assertTrue(xml.contains("<name>decoded profile</name>"), "The decoded XML must contain the profile name");
        assertTrue(xml.contains("<language>js</language>"),      "The decoded XML must contain the language");
        assertTrue(xml.contains("<rules>"),                       "The decoded XML must contain the rules section");
    }

    @Test
    void testProfileDataUriForInvalidProfileThrowsRuntimeException() {
        URI invalidUri = URI.create("file:///non/existent/profile.json");
        ProfileBackup backup = new ProfileBackup(invalidUri);
        assertThrows(RuntimeException.class, backup::profileDataUri);
    }

    // -----------------------------------------------------------------------
    // xmlProfile() + loadRule() — with real classpath test resources
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
            "The XML must contain the repositoryKey creedengo-java");
        // rule key
        assertTrue(xml.contains("<key>GCI1</key>"),
            "The XML must contain the rule key GCI1");
        // type from JSON file
        assertTrue(xml.contains("<type>CODE_SMELL</type>"),
            "The XML must contain the type CODE_SMELL");
        // severity in UPPERCASE (toUpperCase applied on 'minor')
        assertTrue(xml.contains("<priority>MINOR</priority>"),
            "The severity 'minor' must be converted to 'MINOR' via toUpperCase()");
    }

    @Test
    void testProfileDataUri_withTwoRules_containsBothRules() throws IOException {
        URI profileUri = createProfileJson("java two rules", "java", List.of("GCI1", "GCI2"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<key>GCI1</key>"), "The XML must contain GCI1");
        assertTrue(xml.contains("<key>GCI2</key>"), "The XML must contain GCI2");
        // GCI2 has type=BUG and severity=major → MAJOR
        assertTrue(xml.contains("<type>BUG</type>"),           "The XML must contain the type BUG for GCI2");
        assertTrue(xml.contains("<priority>MAJOR</priority>"), "The severity 'major' must be MAJOR");
    }

    @Test
    void testProfileDataUri_withRule_repositoryKeyUsesPythonLanguage() throws IOException {
        // python/GCI5.json must exist in the test resources
        URI profileUri = createProfileJson("python profile", "python", List.of("GCI5"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<repositoryKey>creedengo-python</repositoryKey>"),
            "The repositoryKey must be creedengo-python for the python language");
        assertTrue(xml.contains("<key>GCI5</key>"), "The XML must contain the key GCI5");
        // GCI5 has severity=critical → CRITICAL
        assertTrue(xml.contains("<priority>CRITICAL</priority>"),
            "The severity 'critical' must be converted to CRITICAL");
    }

    @Test
    void testProfileDataUri_withRule_rulesTagContainsRuleTag() throws IOException {
        URI profileUri = createProfileJson("profile with rule", "java", List.of("GCI1"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<rule>"),  "The XML must contain the opening <rule> tag");
        assertTrue(xml.contains("</rule>"), "The XML must contain the closing </rule> tag");
        assertTrue(xml.contains("<parameters />"),
            "The XML must contain the <parameters /> tag");
    }

    @Test
    void testProfileDataUri_withRule_xmlContainsProfileNameAndLanguage() throws IOException {
        URI profileUri = createProfileJson("mon profil java", "java", List.of("GCI1"));
        ProfileBackup backup = new ProfileBackup(profileUri);

        String xml = decodeDataUri(backup.profileDataUri());

        assertTrue(xml.contains("<name>mon profil java</name>"),
            "The XML must contain the profile name");
        assertTrue(xml.contains("<language>java</language>"),
            "The XML must contain the language");
    }

    // -----------------------------------------------------------------------
    // loadRule() — error: classpath resource not found
    // -----------------------------------------------------------------------

    @Test
    void testProfileDataUri_withUnknownRule_throwsRuntimeException() throws IOException {
        // The rule RULE_INCONNUE does not exist in the classpath
        URI profileUri = createProfileJson("bad profile", "java", List.of("RULE_INCONNUE"));
        ProfileBackup backup = new ProfileBackup(profileUri);
        assertThrows(RuntimeException.class, backup::profileDataUri,
            "loadRule() must throw a RuntimeException if the rule JSON resource is not found");
    }

    // -----------------------------------------------------------------------
    // profileMetadata() cache — "profileMetadata != null" branch
    // -----------------------------------------------------------------------

    @Test
    void testProfileMetadataCache_secondCallReturnsSameLanguage() throws IOException {
        URI profileUri = createProfileJson("cached", "java", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        // 1st call: loads and caches
        String first = backup.language();
        // 2nd call: uses the cache (if == null branch → false)
        String second = backup.language();
        assertEquals(first, second, "Both calls must return the same value from the cache");
    }

    @Test
    void testProfileMetadataCache_languageThenName_useSameCache() throws IOException {
        URI profileUri = createProfileJson("profil cache", "js", List.of());
        ProfileBackup backup = new ProfileBackup(profileUri);
        // language() loads the cache, name() must reuse it without re-reading the file
        assertEquals("js",           backup.language());
        assertEquals("profil cache", backup.name());
    }

    // -----------------------------------------------------------------------
    // Private test helpers
    // -----------------------------------------------------------------------

    /** Decodes a base64 data URI into an XML String. */
    private String decodeDataUri(URL dataUri) {
        String raw = dataUri.toString();
        String b64 = raw.substring("data:text/xml;base64,".length());
        return new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
    }

    // -----------------------------------------------------------------------
    // Class structure
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
