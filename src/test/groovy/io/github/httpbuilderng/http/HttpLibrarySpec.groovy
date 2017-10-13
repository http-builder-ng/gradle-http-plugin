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

import spock.lang.Specification

class HttpLibrarySpec extends Specification {

    def 'http library'() {
        expect:
        HttpLibrary.fromName(name) == result

        where:
        name     || result
        'core'   || HttpLibrary.CORE
        'apache' || HttpLibrary.APACHE
        'okhttp' || HttpLibrary.OKHTTP
        'CORE'   || HttpLibrary.CORE
        'APACHE' || HttpLibrary.APACHE
        'OKHTTP' || HttpLibrary.OKHTTP
    }

    def 'invalid library'(){
        when:
        HttpLibrary.fromName('blah')

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'Specified library (blah) is unknown.'
    }
}
