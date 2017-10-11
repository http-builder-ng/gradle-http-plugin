package com.stehno.gradle.testing

import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.rules.TemporaryFolder

/**
 * Created by cjstehno on 9/23/16.
 */
// FIXME: since now I have copied this into four different plugins its time to pull it out into its own library and share it!
trait UsesGradleBuild {

    String getBuildTemplate() { '' }

    abstract TemporaryFolder getProjectRoot()

    private final TemplateEngine templateEngine = new GStringTemplateEngine()

    void buildFile(final Map<String, Object> config = [:]) {
        File buildFile = projectRoot.newFile('build.gradle')
        buildFile.text = (templateEngine.createTemplate(buildTemplate).make(config: config) as String).stripIndent()
    }

    GradleRunner gradleRunner(final String line) {
        GradleRunner.create().withPluginClasspath().withDebug(true).withProjectDir(projectRoot.root).withArguments(line.split(' '))
    }

    GradleRunner gradleRunner(final String... args) {
        GradleRunner.create().withPluginClasspath().withDebug(true).withProjectDir(projectRoot.root).withArguments(args)
    }

    GradleRunner gradleRunner(final List<String> args) {
        GradleRunner.create().withPluginClasspath().withDebug(true).withProjectDir(projectRoot.root).withArguments(args)
    }

    static boolean totalSuccess(final BuildResult result) {
        result.tasks.every { BuildTask task -> task.outcome != TaskOutcome.FAILED }
    }

    static boolean textContainsLines(final String text, final Collection<String> lines, final boolean trimmed = true) {
        lines.every { String line ->
            text.contains(trimmed ? line.trim() : line)
        }
    }

    static boolean textDoesNotContainLines(final String text, final Collection<String> lines, final boolean trimmed = true) {
        lines.every { String line ->
            !text.contains(trimmed ? line.trim() : line)
        }
    }
}
