/*
 * Copyright (C) 2021 HttpBuilder-NG Project
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
import groovyx.net.http.HttpObjectConfig

import java.util.function.Consumer

/**
 * A Gradle Plugin DSL Extension used to configure the HTTP plugin.
 */
@CompileStatic
class HttpExtension {

    private Object config
    private HttpLibrary library = HttpLibrary.CORE

    /**
     * Used to specify the HTTP client library with the HttpLibrary enum. If not specified, the CORE library is used.
     *
     * @param value the library to be used
     */
    void setLibrary(final HttpLibrary value) {
        this.library = value
    }

    /**
     * Used to specify the HTTP client library with a String representing the HttpLibrary enum. If not specified, the CORE library is used.
     *
     * @param value the library to be used
     */
    void setLibrary(final String value) {
        this.library = HttpLibrary.fromName(value)
    }

    /**
     * Retrieves the configured library value as the HttpLibrary enum.
     *
     * @return the configured library value
     */
    HttpLibrary getLibrary() {
        library
    }

    /**
     * Provides a global/shared configuration to be used by all HTTP calls that do not provide their own "config" block. See the
     * <a href="https://http-builder-ng.github.io/http-builder-ng/docs/javadoc/groovyx/net/http/HttpObjectConfig.html">HttpObjectConfig</a> interface
     * in the HttpBuilder-NG JavaDocs for details about the specific configuration.
     *
     * @param closure the configuration closure
     */
    void config(@DelegatesTo(HttpObjectConfig) final Closure closure) {
        config = closure
    }

    void config(final Consumer<HttpObjectConfig> consumer){
        config = consumer
    }

    protected Object getConfig() {
        config
    }
}