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

import spock.lang.Specification
import spock.lang.Unroll

class HttpExtensionSpec extends Specification {

    @Unroll 'usage #library'() {
        setup:
        HttpExtension extension = new HttpExtension(
            library: library
        )
        extension.config {
            'configuration'
        }

        expect:
        extension.library == result
        extension.config.call() == 'configuration'

        where:
        library            || result
        HttpLibrary.CORE   || HttpLibrary.CORE
        HttpLibrary.APACHE || HttpLibrary.APACHE
        HttpLibrary.OKHTTP || HttpLibrary.OKHTTP
        'core'             || HttpLibrary.CORE
        'apache'           || HttpLibrary.APACHE
        'okhttp'           || HttpLibrary.OKHTTP
    }
}
