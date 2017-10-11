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
import groovyx.net.http.HttpBuilder
import groovyx.net.http.HttpConfig
import groovyx.net.http.HttpObjectConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import static groovy.transform.TypeCheckingMode.SKIP

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

        builder.post methodClosure
    }

    @CompileStatic(SKIP)
    private HttpBuilder resolveHttpBuilder(final HttpExtension extension) {
        // this could be done with reflection, but I think this will be ok - performance is not really a concern here as long as it works
        "groovyx.net.http.${extension.library.prefix}Builder".configure(resolveConfigClosure(extension))
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
