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
import groovy.transform.TupleConstructor

/**
 * Represents the available underlying HTTP client libraries (as supported by HttpBuilder-NG).
 */
@CompileStatic @TupleConstructor
enum HttpLibrary {

    /**
     * The "core" library, using built-in Java HTTP client.
     */
    CORE,

    /**
     * The "apache" library, using the Apache HttpComponents client library.
     */
    APACHE,

    /**
     * The "okhttp" library, using the OkHttp client library.
     */
    OKHTTP

    /**
     * Resolves the enum value from the name, which should be the same as the enum name, case insensitive.
     *
     * @param name the library name
     * @return the enum value representing that specified library
     */
    static HttpLibrary fromName(final String name) {
        HttpLibrary library = values().find { it.name().equalsIgnoreCase(name) }
        if (library) {
            return library
        } else {
            throw new IllegalArgumentException("Specified library ($name) is unknown.")
        }
    }
}