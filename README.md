Creedengo Integration Test component
======================================

This component is responsible for executing integration tests for the Creedengo project on a local SonarQube instance, temporarily installed on the local machine by this component.
It uses Maven for dependency management and build execution, JUnit for unit testing, and AssertJ for assertions.
It will be used inside Creedengo plugins to check Integration tests.

☕ Compatibility
------------------

| Component Version | Java version |
|-------------------|--------------|
| 0.1.+             | 11 / 17      |


🚀 What does this component do ?
------------------

- Download and install a local SonarQube instance
- Launch the SonarQube instance
- Install the current Creedengo plugin
- Launch the analysis of an internal test project
- Enable the check of the analysis result : each Integration test must call one of check methods available in this component
- Stop the SonarQube instance

- An option ("test-it.sonarqube.keepRunning" to "true") is available to keep the SonarQube instance running during the test execution. This is useful for debugging purposes.

🧩 How to configure this component ?
------------------

Configuration keys available :
- **test-it.orchestrator.artifactory.url** : string / mandatory / default : no value - URL of the Maven repository where sonarqube will be downloaded
- **test-it.sonarqube.keepRunning** : boolean / optional / default : false - To keep SonarQube instance running and to manually use it
- **test-it.sonarqube.version** : string / mandatory / default : no value - Version of `sonarqube` used by integration tests (you can override this value to perform matrix compatibility tests)
- **test-it.sonarqube.port** : integer / optional / no value : blank - SonarQube server listening port. Leave this property blank to automatically choose an available port
- **test-it.plugins** : string / optional / default : no value - Comma separated list of plugins (`groupId|artifactId|version` or `file:///`) to install in SonarQube instance before lunching integration tests
- **test-it.additional-profile-uris** : string / optional / default : no value - Comma separated JSON profile file URIs to load
- **test-it.test-projects** : string / mandatory / default : no value - Comma separated paths of "test projects" to analyze. Syntaxe: `sonar.projectKey|sonar.projectName|project.pomUri`
- **test-it.test-project-profile-by-language** : string / optional / default : no value - Comma separated list of profiles to associate to each "test project". Syntaxe: `language:profileName`
