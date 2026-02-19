package org.greencodeinitiative.creedengo.integration.tests;

import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.MavenLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BuildProjectEngineTest {

    @TempDir
    Path tempDir;

    // -----------------------------------------------------------------------
    // Structure de la classe
    // -----------------------------------------------------------------------

    @Test
    void testClassIsAbstract() {
        assertTrue(Modifier.isAbstract(BuildProjectEngine.class.getModifiers()),
            "BuildProjectEngine doit être abstraite");
    }

    @Test
    void testClassHasDefaultNoArgConstructor() {
        // Java génère un constructeur public sans paramètre pour les classes abstraites
        // sans constructeur explicite. On vérifie qu'il n'y a qu'un seul constructeur, sans paramètre.
        var constructors = BuildProjectEngine.class.getConstructors();
        assertEquals(1, constructors.length,
            "BuildProjectEngine doit avoir exactement un constructeur public (no-arg implicite)");
        assertEquals(0, constructors[0].getParameterCount(),
            "Le constructeur doit être sans paramètre");
    }

    // -----------------------------------------------------------------------
    // splitAndTrim — méthode statique privée, testée par réflexion
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Stream<String> invokeSplitAndTrim(String value, String regex) throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("splitAndTrim", String.class, String.class);
        method.setAccessible(true);
        return (Stream<String>) method.invoke(null, value, regex);
    }

    @Test
    void testSplitAndTrim_commaSeparated() throws Exception {
        Stream<String> result = invokeSplitAndTrim("a,b,c", "\\s*,\\s*");
        assertArrayEquals(new String[]{"a", "b", "c"}, result.toArray(String[]::new));
    }

    @Test
    void testSplitAndTrim_withSpaces() throws Exception {
        Stream<String> result = invokeSplitAndTrim(" a , b , c ", "\\s*,\\s*");
        assertArrayEquals(new String[]{"a", "b", "c"}, result.toArray(String[]::new));
    }

    @Test
    void testSplitAndTrim_pipeSeparated() throws Exception {
        Stream<String> result = invokeSplitAndTrim("x|y|z", "\\s*\\|\\s*");
        assertArrayEquals(new String[]{"x", "y", "z"}, result.toArray(String[]::new));
    }

    @Test
    void testSplitAndTrim_filterEmptyTokens() throws Exception {
        Stream<String> result = invokeSplitAndTrim("a,,b", "\\s*,\\s*");
        // Le token vide entre les deux virgules doit être filtré
        assertArrayEquals(new String[]{"a", "b"}, result.toArray(String[]::new));
    }

    @Test
    void testSplitAndTrim_singleValue() throws Exception {
        Stream<String> result = invokeSplitAndTrim("only", "\\s*,\\s*");
        assertArrayEquals(new String[]{"only"}, result.toArray(String[]::new));
    }

    @Test
    void testSplitAndTrim_colonSeparated() throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("colonSeparatedValues", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Stream<String> result = (Stream<String>) method.invoke(null, "group:artifact:1.0");
        assertArrayEquals(new String[]{"group", "artifact", "1.0"}, result.toArray(String[]::new));
    }

    // -----------------------------------------------------------------------
    // toPluginLocation — MavenLocation (GAV) et FileLocation (file://)
    // -----------------------------------------------------------------------

    private Object invokeToPluginLocation(String location) throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("toPluginLocation", String.class);
        method.setAccessible(true);
        return method.invoke(null, location);
    }

    @Test
    void testToPluginLocation_mavenGAV_returnsMavenLocation() throws Exception {
        Object location = invokeToPluginLocation("org.example:my-plugin:1.2.3");
        assertInstanceOf(MavenLocation.class, location,
            "Un GAV Maven doit produire un MavenLocation");
    }

    @Test
    void testToPluginLocation_mavenGAV_invalidThrows() {
        assertThrows(Exception.class,
            () -> invokeToPluginLocation("org.example:missing-version"),
            "Un GAV Maven incomplet doit lever une exception"
        );
    }

    @Test
    void testToPluginLocation_fileLocation_returnsFileLocation() throws Exception {
        // Crée un fichier temporaire réel pour FileLocation
        Path fakeJar = tempDir.resolve("plugin.jar");
        Files.createFile(fakeJar);
        Object location = invokeToPluginLocation("file://" + fakeJar.toAbsolutePath());
        assertInstanceOf(FileLocation.class, location,
            "Un chemin file:// doit produire un FileLocation");
    }

    @Test
    void testToPluginLocation_emptyStringThrows() {
        assertThrows(Exception.class,
            () -> invokeToPluginLocation(""),
            "Une chaîne vide doit lever une exception"
        );
    }

    // -----------------------------------------------------------------------
    // commaSeparatedValues et pipeSeparatedValues
    // -----------------------------------------------------------------------

    @Test
    void testCommaSeparatedValues_multipleValues() throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("commaSeparatedValues", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Stream<String> result = (Stream<String>) method.invoke(null, "alpha, beta, gamma");
        assertArrayEquals(new String[]{"alpha", "beta", "gamma"}, result.toArray(String[]::new));
    }

    @Test
    void testPipeSeparatedValues_multipleValues() throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("pipeSeparatedValues", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Stream<String> result = (Stream<String>) method.invoke(null, "key1 | value1 | extra");
        assertArrayEquals(new String[]{"key1", "value1", "extra"}, result.toArray(String[]::new));
    }

    // -----------------------------------------------------------------------
    // systemProperty — doit lever IllegalStateException si propriété absente
    // -----------------------------------------------------------------------

    @Test
    void testSystemProperty_throwsWhenMissing() throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("systemProperty", String.class);
        method.setAccessible(true);
        // Propriété inexistante → IllegalStateException wrappée dans InvocationTargetException
        Exception ex = assertThrows(Exception.class,
            () -> method.invoke(null, "test-it.nonexistent.property." + System.nanoTime())
        );
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        assertInstanceOf(IllegalStateException.class, cause,
            "Doit lever IllegalStateException quand la propriété système est absente");
    }

    @Test
    void testSystemProperty_returnsValueWhenPresent() throws Exception {
        String key   = "test-it.tmp.property." + System.nanoTime();
        String value = "test-value";
        System.setProperty(key, value);
        try {
            Method method = BuildProjectEngine.class.getDeclaredMethod("systemProperty", String.class);
            method.setAccessible(true);
            Object result = method.invoke(null, key);
            assertEquals(value, result);
        } finally {
            System.clearProperty(key);
        }
    }

    // -----------------------------------------------------------------------
    // ProjectToAnalyze (classe interne protégée)
    // -----------------------------------------------------------------------

    @Test
    void testProjectToAnalyzeClass_exists() {
        // La présence de la classe interne est validée en tentant de la charger par son nom binaire
        assertDoesNotThrow(
            () -> Class.forName(
                "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
            ),
            "La classe interne ProjectToAnalyze doit exister"
        );
    }

    @Test
    void testProjectToAnalyzeClass_isStatic() throws ClassNotFoundException {
        Class<?> inner = Class.forName(
            "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
        );
        assertTrue(Modifier.isStatic(inner.getModifiers()),
            "ProjectToAnalyze doit être une classe statique interne");
    }

    private Object createProjectToAnalyze(URI pomUri, String key, String name) throws Exception {
        Class<?> inner = Class.forName(
            "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
        );
        var ctor = inner.getDeclaredConstructor(URI.class, String.class, String.class);
        ctor.setAccessible(true);
        return ctor.newInstance(pomUri, key, name);
    }

    @Test
    void testProjectToAnalyze_constructorThrowsForNonExistentPom() {
        URI nonExistent = URI.create("file:///nonexistent/pom.xml");
        assertThrows(Exception.class,
            () -> createProjectToAnalyze(nonExistent, "key", "name")
        );
    }

    @Test
    void testProjectToAnalyze_gettersReturnCorrectValues() throws Exception {
        Path fakePom = tempDir.resolve("pom.xml");
        Files.createFile(fakePom);

        Class<?> inner = Class.forName(
            "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
        );
        Object instance = createProjectToAnalyze(fakePom.toUri(), "my-project-key", "My Project");

        Method getKey  = inner.getMethod("getProjectKey");
        Method getName = inner.getMethod("getProjectName");
        Method getPom  = inner.getMethod("getPom");

        assertEquals("my-project-key", getKey.invoke(instance));
        assertEquals("My Project",     getName.invoke(instance));
        assertEquals(fakePom,          getPom.invoke(instance));
    }

    @Test
    void testProjectToAnalyze_createMavenBuild_doesNotThrow() throws Exception {
        Path fakePom = tempDir.resolve("pom.xml");
        Files.createFile(fakePom);

        Object instance = createProjectToAnalyze(fakePom.toUri(), "proj-key", "Project Name");

        Class<?> inner = Class.forName(
            "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
        );
        Method createMavenBuild = inner.getMethod("createMavenBuild");
        Object mavenBuild = assertDoesNotThrow(() -> createMavenBuild.invoke(instance));
        assertNotNull(mavenBuild, "createMavenBuild() doit retourner un objet non null");
    }
}

