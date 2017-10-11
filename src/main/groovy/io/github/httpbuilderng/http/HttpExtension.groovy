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
import groovyx.net.http.HttpObjectConfig

@CompileStatic
class HttpExtension {

    private Closure configClosure

    HttpLibrary library = HttpLibrary.CORE

    String libraryVersion = '1.0.1' // TODO: this should default to the most current

    void setLibrary(String value) {
        this.library = HttpLibrary.fromName(value)
    }

    /**
     * Provides a global/shared configuration to be used by all HTTP calls that do not provide their own "config" block.
     */
    void config(@DelegatesTo(HttpObjectConfig) Closure closure) {
        configClosure = closure
    }

    protected Closure getConfigClosure() {
        configClosure
    }
}

@CompileStatic
enum HttpLibrary {
    CORE, APACHE, OKHTTP

    static HttpLibrary fromName(final String name) {
        HttpLibrary library = values().find { it.name().equalsIgnoreCase(name) }
        if (library) {
            return library
        } else {
            throw new IllegalArgumentException("Specified library ($name) is unkown.")
        }
    }
}