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
package io.github.httpbuilderng.http

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle Plugin providing ability to configure and execute HTTP requests using the HttpBuilder-NG client library.
 */
@CompileStatic
class HttpPlugin implements Plugin<Project> {

    /**
     * Applies the configuration for the plugin.
     *
     * @param project a reference to the project
     */
    @Override void apply(final Project project) {
        project.extensions.create('http', HttpExtension) as HttpExtension
    }
}
