/*
 * Copyright (C) 2019 HttpBuilder-NG Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.gradle.testing

import groovy.text.GStringTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder

// FIXME: this needs to be pulled out into its own project and used in my other plugins
@CompileStatic
class GradleBuild extends ExternalResource {

    String template

    @Delegate final TemporaryFolder folder = new TemporaryFolder()

    private final TemplateEngine templateEngine = new GStringTemplateEngine()

    @Override
    protected void before() throws Throwable {
        folder.create()
    }

    void buildFile(final Map<String, Object> config = [:]) {
        File buildFile = newFile('build.gradle')
        buildFile.text = (templateEngine.createTemplate(template).make(config: config) as String).stripIndent()
    }

    GradleRunner runner(final String line) {
        GradleRunner.create().withPluginClasspath().withDebug(true).withProjectDir(root).withArguments(line.split(' '))
    }

    GradleRunner runner(final String... args) {
        GradleRunner.create().withPluginClasspath().withDebug(true).withProjectDir(root).withArguments(args)
    }

    GradleRunner runner(final List<String> args) {
        GradleRunner.create().withPluginClasspath().withDebug(true).withProjectDir(root).withArguments(args)
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

    @Override
    protected void after() {
        folder.delete()
    }
}