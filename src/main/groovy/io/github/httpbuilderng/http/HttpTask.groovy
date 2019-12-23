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
import groovy.transform.TupleConstructor
import groovyx.net.http.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.util.function.Consumer

import static groovy.transform.TypeCheckingMode.SKIP

/**
 * Gradle Task used to allow configuration and execution of HTTP requests using the HttpBuilder-NG library.
 *
 * The return values of the HTTP calls are not used in any manner. In order to process and handle responses, the
 * <a href="https://http-builder-ng.github.io/http-builder-ng/asciidoc/html5/#_response">Response Handlers</a> should be used.
 *
 * All HTTP request methods supported by HttpBuilder-NG are supported, except OPTIONS and TRACE.
 */
@CompileStatic @SuppressWarnings('GroovyUnusedDeclaration')
class HttpTask extends DefaultTask {

    private Object config
    private final List<RequestConfig> requests = []

    /**
     * Used to provide the client configuration for the task. This is required if the global configuration is not specified in the <code>http</code>
     * extension configuration.
     *
     * The configuration itself is based on the <code>HttpObjectConfig</code> from HttpBuilder-NG.
     *
     * @param closure the configuration closure
     */
    @Input void config(@DelegatesTo(HttpObjectConfig) final Closure closure) {
        config = closure
    }

    /**
     * Used to provide the client configuration for the task. This is required if the global configuration is not specified in the <code>http</code>
     * extension configuration.
     *
     * The configuration itself is based on the <code>HttpObjectConfig</code> from HttpBuilder-NG.
     *
     * @param consumer the configuration consumer
     */
    @Input void config(final Consumer<HttpObjectConfig> consumer) {
        config = consumer
    }

    @Input void get(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'get', conf
    }

    @Input void getAsync(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'getAsync', conf
    }

    @Input void get(final Consumer<HttpConfig> conf) {
        addRequestConfig 'get', conf
    }

    @Input void getAsync(final Consumer<HttpConfig> conf) {
        addRequestConfig 'getAsync', conf
    }

    @Input void head(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'head', conf
    }

    @Input void headAsync(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'headAsync', conf
    }

    @Input void head(final Consumer<HttpConfig> conf) {
        addRequestConfig 'head', conf
    }

    @Input void headAsync(final Consumer<HttpConfig> conf) {
        addRequestConfig 'headAsync', conf
    }

    @Input void put(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'put', conf
    }

    @Input void putAsync(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'putAsync', conf
    }

    @Input void put(final Consumer<HttpConfig> conf) {
        addRequestConfig 'put', conf
    }

    @Input void putAsync(final Consumer<HttpConfig> conf) {
        addRequestConfig 'putAsync', conf
    }

    @Input void post(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'post', conf
    }

    @Input void postAsync(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'postAsync', conf
    }

    @Input void post(final Consumer<HttpConfig> conf) {
        addRequestConfig 'post', conf
    }

    @Input void postAsync(final Consumer<HttpConfig> conf) {
        addRequestConfig 'postAsync', conf
    }

    @Input void delete(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'delete', conf
    }

    @Input void deleteAsync(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'deleteAsync', conf
    }

    @Input void delete(final Consumer<HttpConfig> conf) {
        addRequestConfig 'delete', conf
    }

    @Input void deleteAsync(final Consumer<HttpConfig> conf) {
        addRequestConfig 'deleteAsync', conf
    }

    @Input void patch(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'patch', conf
    }

    @Input void patchAsync(@DelegatesTo(HttpConfig) final Closure conf) {
        addRequestConfig 'patchAsync', conf
    }

    @Input void patch(final Consumer<HttpConfig> conf) {
        addRequestConfig 'patch', conf
    }

    @Input void patchAsync(final Consumer<HttpConfig> conf) {
        addRequestConfig 'patchAsync', conf
    }

    private void addRequestConfig(final String method, final Object conf) {
        requests << new RequestConfig(method, conf)
    }

    @TaskAction void http() {
        HttpExtension extension = project.extensions.findByType(HttpExtension)
        HttpBuilder builder = resolveHttpBuilder(extension)

        if (!requests) {
            throw new IllegalArgumentException('There are no requests configured.')
        }

        requests.each { RequestConfig rc ->
            executeRequest(builder, rc)
        }
    }

    @CompileStatic(SKIP)
    private void executeRequest(final HttpBuilder builder, final RequestConfig rc) {
        builder."${rc.method}"(rc.config instanceof Closure ? rc.config as Closure : rc.config as Consumer<HttpConfig>)
    }

    @CompileStatic(SKIP)
    private HttpBuilder resolveHttpBuilder(final HttpExtension extension) {
        switch (extension.library) {
            case HttpLibrary.CORE:
                return JavaHttpBuilder.configure(resolveConfig(extension))
            case HttpLibrary.APACHE:
                return ApacheHttpBuilder.configure(resolveConfig(extension))
            case HttpLibrary.OKHTTP:
                return OkHttpBuilder.configure(resolveConfig(extension))
            default:
                throw new IllegalArgumentException("HttpLibrary (${extension.library}) is not supported.")
        }
    }

    private Object resolveConfig(final HttpExtension extension) {
        Object configObject = config ?: extension.config
        if (configObject) {
            return configObject
        } else {
            throw new IllegalArgumentException('A configuration closure or consumer must be provided either globally or by the task configuration.')
        }
    }

    @TupleConstructor
    private static class RequestConfig {

        final String method
        final Object config
    }
}
