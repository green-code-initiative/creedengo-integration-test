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
    // Class structure
    // -----------------------------------------------------------------------

    @Test
    void testClassIsAbstract() {
        assertTrue(Modifier.isAbstract(BuildProjectEngine.class.getModifiers()),
            "BuildProjectEngine must be abstract");
    }

    @Test
    void testClassHasDefaultNoArgConstructor() {
        // Java generates a public no-arg constructor for abstract classes
        // without an explicit constructor. We verify there is only one constructor, with no parameters.
        var constructors = BuildProjectEngine.class.getConstructors();
        assertEquals(1, constructors.length,
            "BuildProjectEngine must have exactly one public constructor (implicit no-arg)");
        assertEquals(0, constructors[0].getParameterCount(),
            "The constructor must have no parameters");
    }

    // -----------------------------------------------------------------------
    // splitAndTrim — private static method, tested via reflection
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
        // The empty token between the two commas must be filtered out
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
    // toPluginLocation — MavenLocation (GAV) and FileLocation (file://)
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
            "A Maven GAV must produce a MavenLocation");
    }

    @Test
    void testToPluginLocation_mavenGAV_invalidThrows() {
        assertThrows(Exception.class,
            () -> invokeToPluginLocation("org.example:missing-version"),
            "An incomplete Maven GAV must throw an exception"
        );
    }

    @Test
    void testToPluginLocation_fileLocation_returnsFileLocation() throws Exception {
        // Create a real temporary file for FileLocation
        Path fakeJar = tempDir.resolve("plugin.jar");
        Files.createFile(fakeJar);
        Object location = invokeToPluginLocation("file://" + fakeJar.toAbsolutePath());
        assertInstanceOf(FileLocation.class, location,
            "A file:// path must produce a FileLocation");
    }

    @Test
    void testToPluginLocation_emptyStringThrows() {
        assertThrows(Exception.class,
            () -> invokeToPluginLocation(""),
            "An empty string must throw an exception"
        );
    }

    // -----------------------------------------------------------------------
    // commaSeparatedValues and pipeSeparatedValues
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
    // systemProperty — must throw IllegalStateException if property is missing
    // -----------------------------------------------------------------------

    @Test
    void testSystemProperty_throwsWhenMissing() throws Exception {
        Method method = BuildProjectEngine.class.getDeclaredMethod("systemProperty", String.class);
        method.setAccessible(true);
        // Non-existent property → IllegalStateException wrapped in InvocationTargetException
        Exception ex = assertThrows(Exception.class,
            () -> method.invoke(null, "test-it.nonexistent.property." + System.nanoTime())
        );
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        assertInstanceOf(IllegalStateException.class, cause,
            "Must throw IllegalStateException when the system property is absent");
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
    // ProjectToAnalyze (protected inner class)
    // -----------------------------------------------------------------------

    @Test
    void testProjectToAnalyzeClass_exists() {
        // The presence of the inner class is validated by attempting to load it by its binary name
        assertDoesNotThrow(
            () -> Class.forName(
                "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
            ),
            "The inner class ProjectToAnalyze must exist"
        );
    }

    @Test
    void testProjectToAnalyzeClass_isStatic() throws ClassNotFoundException {
        Class<?> inner = Class.forName(
            "org.greencodeinitiative.creedengo.integration.tests.BuildProjectEngine$ProjectToAnalyze"
        );
        assertTrue(Modifier.isStatic(inner.getModifiers()),
            "ProjectToAnalyze must be a static inner class");
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
        assertNotNull(mavenBuild, "createMavenBuild() must return a non-null object");
    }
}

