# Technical debt

## TD-1 - Consistent Maven selection in CI

### Background and current status

Maven 3.10 no longer provides `maven-shared-utils` and its
`org.apache.maven.shared.utils.logging.MessageUtils` API to core extensions.
`me.qoomon:maven-git-versioning-extension:9.12.1` depends on that API and can fail
when a runner upgrades Maven, even if the top-level build uses a working wrapper.
Tools invoking `mvn`, including dependency submission and Orchestrator analysis
in consuming plugins, can otherwise select a different Maven from `PATH`.

This repository already uses extension **9.12.2**, whose upstream `LogStyle`
supports both the old API and Maven 3.10's `org.apache.maven.jline.MessageUtils`.
The original MessageUtils defect is therefore addressed upstream; it must not be
reported as an unfixed 9.12.2 defect. The wrapper remains **Maven 3.9.9**.
No version upgrade or downgrade is part of this change. CI retains that known
baseline pending full Maven 3.10 qualification, rather than relying on runner updates.

### Actual surfaces and measures

| Surface | Measure | Files |
|---------|---------|-------|
| Build, JDK 17 | Resolve Maven home from `./mvnw -B -v`, require an executable `bin/mvn`, export it through `GITHUB_PATH`, then log `mvn -v` | `.github/workflows/build.yml` |
| Dependency submission | Same deterministic PATH selection; pinned submission action; writable token only for trusted events | `.github/workflows/dependency-submission.yml` |
| Maven Central publishing | Already uses `./mvnw --batch-mode deploy`; no change needed | `.github/workflows/publish_to_maven_central.yml` |
| Wrapper and extension | Keep the existing 3.9.9 / 9.12.2 versions | `.mvn/wrapper/maven-wrapper.properties`, `.mvn/extensions.xml` |
| Consumer analysis | Document compatible Maven selection and optional nested-project isolation; preserve public launcher behavior | `README.md`, `src/main/java/org/greencodeinitiative/creedengo/integration/tests/BuildProjectEngine.java` |

There is one root POM and no checked-in nested Maven test project. The unit tests
create temporary POM files but do not execute Maven on them. Consequently no
comment-only `.mvn/maven.config` or gitignore exception is required here.
The README supplies an explanatory comment-only example for consumers that need
an independent Maven root. Do not add a root `maven.config` to bypass this
repository's required versioning extension.

### Separate classpath issue in consuming plugins

The plugin fixes also excluded a language analyzer from Failsafe's JVM: that
analyzer is installed in SonarQube via `test-it.plugins`, not used by the tests
themselves, and its embedded dependencies can shadow Orchestrator dependencies
(notably `commons-lang3` and `ObjectUtils.getIfNull(Object, Object)` in 3.18+).
This is independent of Maven's MessageUtils change.

This library has no Failsafe configuration and no analyzer plugin dependency to
exclude. Its Orchestrator and other compile dependencies are required by the
published library. Excluding them or changing unit-test classpaths would be an
incorrect port of the plugin workaround; the POM and launcher remain unchanged.

### Dependency graph administration

Use the explicit workflow instead of GitHub's automatic dependency submission,
whose Maven invocation is outside these workflow steps. After integration, an
administrator must keep **Settings > Advanced Security > Dependency graph**
enabled and set **Automatic dependency submission** to **disabled**. No remote
setting is changed by this patch. Fork and Dependabot pull requests are skipped
because their tokens cannot write dependency snapshots; pushes to `main` and
manual runs still submit the graph.

### Cleanup procedure

1. Verify the extension's Maven 3.10 compatibility (9.12.2 already implements the
   MessageUtils fix), then qualify the complete library build, consuming plugins'
   Orchestrator subprocesses, dependency submission, and publishing on the chosen
   Maven version. Do not upgrade the extension solely to reapply an existing fix.
2. Update the wrapper baseline deliberately if qualification succeeds. Remove
   the temporary PATH steps and their version checks only if every external
   `mvn` invocation is guaranteed to use a validated distribution.
3. In consuming plugins, remove any isolation-only `.mvn/maven.config` files
   after confirming inherited extensions work on the selected Maven. Retain
   independent Maven roots when they are still needed for project isolation.
4. Keep analyzer classpath exclusions separate: remove them only after checking
   that embedded dependencies no longer shadow Orchestrator. None exist here.
5. Keep explicit dependency submission unless an administrator deliberately
   restores automatic submission after validating its Maven environment. Avoid
   running both, and update this document and README when retiring the measures.

### References

- Original fix: [green-code-initiative/creedengo-php#164](https://github.com/green-code-initiative/creedengo-php/pull/164)
- [Maven Git Versioning Extension](https://github.com/qoomon/maven-git-versioning-extension)
- [Extension 9.12.2 MessageUtils compatibility implementation](https://github.com/qoomon/maven-git-versioning-extension/blob/v9.12.2/src/main/java/me/qoomon/maven/gitversioning/LogStyle.java)
- [Runner images Maven issue](https://github.com/actions/runner-images/issues/14883)
- [Maven 3.10.0 release notes](https://maven.apache.org/docs/3.10.0/release-notes.html)
