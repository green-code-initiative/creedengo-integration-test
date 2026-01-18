Creedengo Integration Test component
======================================

This component is responsible for executing integration tests for the Creedengo project on a local SonarQube instance, temporarily installed on the local machine by this component.
It uses Maven for dependency management and build execution, JUnit for unit testing, and AssertJ for assertions.
It will be used inside Creedengo plugins to check Integration tests.

☕ Compatibility
------------------

| Component Version | Java version |
|-------------------|--------------|
| 0.+               | 17           |


🚀 What does this component do ?
------------------

- Download and install a local SonarQube instance
- Launch the SonarQube instance
- Install the current Creedengo plugin and configure a new profile with all rules activated
- Launch the analysis of an internal test project
- Enable the check of the analysis result : each Integration test must call one of check methods available in this component
- Stop the SonarQube instance

- An option ("test-it.sonarqube.keepRunning" to "true") is available to keep the SonarQube instance running during the test execution. This is useful for debugging purposes. WARNGIN : in this case, IT tests aren't launched.

🧩 Configration keys used by this component ?
------------------

Configuration keys available :
- **test-it.orchestrator.artifactory.url** : string / mandatory (declared by caller) / default : no value - URL of the Maven repository where sonarqube will be downloaded
- **test-it.sonarqube.version** : string / mandatory (declared by caller) / default : no value - Version of `sonarqube` used by integration tests (you can override this value to perform matrix compatibility tests)
- **test-it.sonarqube.keepRunning** : boolean / optional / default : false - To keep SonarQube instance running and to manually use it
- **test-it.sonarqube.port** : integer / optional / no value : blank - SonarQube server listening port. Leave this property blank to automatically choose an available port
- **test-it.plugins** : string / mandatory (declared by caller) / default : no value - Comma separated list of plugins (`groupId|artifactId|version` or `file:///`) to install in SonarQube instance before lunching integration tests
- **test-it.additional-profile-uris** : string / mandatory (declared by caller) / default : no value - Comma separated JSON profile file URIs to load
- **test-it.test-projects** : string / mandatory (declared by caller) / default : no value - Comma separated paths of "test projects" to analyze. Syntaxe: `sonar.projectKey|sonar.projectName|project.pomUri`
- **test-it.test-project-profile-by-language** : string / mandatory (declared by caller) / default : no value - Comma separated list of profiles to associate to each "test project". Syntaxe: `language:profileName`

These keys have to be in pom.xml of creedengo plugin project.
For example, you can use maven profile to modify some key values like port and keepRunning option.
Please check creedengo-java plugin for a full example.

🔧 How to use this component ?
------------------

STEP1: Add the dependency
------------------  
To use this component, add it as a dependency in the `pom.xml` of your Creedengo plugin project.
```xml
<dependency>
    <groupId>org.green-code-initiative</groupId>
    <artifactId>creedengo-integration-test</artifactId>
    <version>0.2.1</version> <!-- use the latest version -->
    <scope>test</scope>
</dependency>
```

STEP2: Configure the component
------------------  
Then configure the required properties in your `pom.xml` in the maven plugin `maven-failsafe-plugin` as described above: 
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.5.3</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
            <configuration>
                <systemPropertyVariables>
                    <test-it.orchestrator.artifactory.url>https://repo1.maven.org/maven2</test-it.orchestrator.artifactory.url>
                    <test-it.sonarqube.version>25.12.0.117093</test-it.sonarqube.version>
                    <test-it.plugins>
                        ${project.baseUri}/target/${project.artifactId}-${project.version}.jar, <!-- The current plugin built -->
                        org.sonarsource.php:sonar-php-plugin:${test-it.sonarphp.version}, <!-- Example of additional plugin for a specific language (here, example for PHP)-->
                    </test-it.plugins>
                    <test-it.additional-profile-uris>
                        <!-- Additional profiles can be added here -->
                        <!-- Example of profile for Creedengo PHP plugin -->
                        ${project.baseUri}/src/main/resources/org/greencodeinitiative/creedengo/php/creedengo_way_profile.json,
                    </test-it.additional-profile-uris>
                    <test-it.test-projects>
                        <!-- Add here the test projects to analyze -->
                        <!-- Example of test project for Creedengo PHP plugin -->
                        org.green-code-initiative:creedengo-php-plugin-test-project|creedengo PHP Sonar Plugin Test Project|${project.baseUri}/src/it/test-projects/creedengo-php-plugin-test-project/pom.xml,
                    </test-it.test-projects>
                    <test-it.test-project-profile-by-language>
                        <!-- Add here the profile to use for each language -->
                        <!-- Example of profile for Creedengo PHP plugin -->
                        php|creedengo way,
                    </test-it.test-project-profile-by-language>
                </systemPropertyVariables>
            </configuration>
        </execution>
    </executions>
</plugin>
```

STEP3: Add a test project in `src/it` resources
------------------
You need to create a test project that will be analyzed during the integration tests.
This project should be placed in the `src/it/test-projects` directory of your Creedengo plugin project.

this project should be a standard Maven project with a `pom.xml` file.
You can create a simple project with some source files that will be analyzed by the Creedengo plugin.
Make sure to include code that will trigger the rules you want to test in your integration tests.


STEP4: Create the integration test class
------------------
Then, create a JUnit test class that uses the `CreedengoItOrchestrator` to run the integration tests.
Here is an example of a simple integration test class using this component:
```java
import org.junit.jupiter.api.Test;
import org.greencodeinitiative.creedengo.integration.tests.GCIRulesBase;

public class MyPluginIT extends GCIRulesBase {

    // example test method for GCI2 rule for Creedengo PHP language
    @Test
    void testGCI2() {

        // the path is relative to the test-project analyzed and refers to a file where issues are expected to be found
        String filePath = "src/AvoidMultipleIfElseStatement.php";
        
        // the rule id to check
        String ruleId = "creedengo-php:GCI2";
        
        // the rule message to check
        String ruleMsg = "Use a switch statement instead of multiple if-else if possible";
        
        // These twe arrays must have the same length
        // the startLines array contains the line numbers where issues are expected to be found
        // the endLines array contains the line numbers where issues are expected to end
        int[] startLines = new int[]{213, 232, 234, 260, 277, 298, 300, 319, 323, 325, 345, 351, 377, 396, 398, 399, 401, 423, 444, 446};
        int[] endLines = new int[]{213, 232, 236, 260, 279, 298, 302, 321, 323, 327, 347, 353, 379, 396, 404, 399, 403, 425, 444, 448};

        // method call to check the issues found in the file
        // this method is provided by GCIRulesBase class
        // SEVERITY, TYPE, EFFORT_5MIN are constants defined in GCIRulesBase class, but you can use your own values or different values provided in GCIRulesBase class 
        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }
    
}
```

STEP5: Run the integration tests
------------------
Finally, run the integration tests using Maven:
```bash
mvn clean verify
```
This will execute the integration tests defined in your test class, using the Creedengo Integration Test component to manage the SonarQube instance and perform the analysis.

If you want to keep the SonarQube instance running for debugging purposes, you can set the `test-it.sonarqube.keepRunning` property to `true` and `test-it.sonarqube.port` in your `pom.xml` or pass them as a command-line argument:
```bash
mvn clean verify -Dtest-it.sonarqube.keepRunning=true -Dtest-it.sonarqube.port=33333
```