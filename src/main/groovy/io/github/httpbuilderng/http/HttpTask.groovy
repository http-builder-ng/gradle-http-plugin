/*
 * Copyright (C) 2017 HttpBuilder-NG Project
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
import groovyx.net.http.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.lang.reflect.Method

@CompileStatic
class HttpTask extends DefaultTask {

    private Closure configClosure
    private Closure methodClosure

    // TODO: should I enforce a single request per task or allow multiple?

    @Input
    void config(@DelegatesTo(HttpObjectConfig) final Closure closure) {
        configClosure = closure
    }

    @Input
    void get(@DelegatesTo(HttpConfig) final Closure closure) {
        methodClosure = closure
    }

    // FIXME: implement other methods and async versions (but only closure accepting ones - consumer?)

    // TODO: note that the response handlers are what is used to do anything with the response data

    @TaskAction void http() {
        HttpExtension extension = project.extensions.findByType(HttpExtension)

        HttpBuilder builder = resolveHttpBuilder(extension)

        if (!methodClosure) {
            throw new IllegalArgumentException('The request method must be configured.')
        }

        // FIXME: this will need to be based on the configured method (above)
        builder.get methodClosure
    }

    private HttpBuilder resolveHttpBuilder(final HttpExtension extension) {
        switch (extension.library) {
            case HttpLibrary.CORE:
                return JavaHttpBuilder.configure(resolveConfigClosure(extension))
            case HttpLibrary.APACHE:
                return ApacheHttpBuilder.configure(resolveConfigClosure(extension))
            case HttpLibrary.OKHTTP:
                return OkHttpBuilder.configure(resolveConfigClosure(extension))
            default:
                throw new IllegalArgumentException("HttpLibrary (${extension.library}) is not supported.")
        }
    }

    private Closure resolveConfigClosure(final HttpExtension extension) {
        Closure closure = configClosure ?: extension.configClosure
        if (closure) {
            return closure
        } else {
            throw new IllegalArgumentException('A configuration closure must be provided either globally or by the task configuration.')
        }
    }
}
