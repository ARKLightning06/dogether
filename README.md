# Team Project

Please keep this up-to-date with information about your project throughout the term.

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality

By keeping this README up-to-date,
your team will find it easier to prepare for the final presentation
at the end of the term.

## Code quality: Checkstyle

Our style rules are in [`checkstyle.xml`](checkstyle.xml): 4-space indentation, 120-character lines,
naming, imports, braces, whitespace, and common bug checks. Run them from the project root
(requires Java 11+):

```bash
mvn org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:check -Dcheckstyle.config.location=checkstyle.xml -Dcheckstyle.consoleOutput=true -Dcheckstyle.includeTestSourceDirectory=true -Dcheckstyle.failOnViolation=false -Dcheckstyle.violationSeverity=error
```

Problems are listed as `[WARN]` lines and never fail the build. For warnings inside IntelliJ,
install the **CheckStyle-IDEA** plugin and add `checkstyle.xml` as a configuration file
(Settings → Tools → Checkstyle).
