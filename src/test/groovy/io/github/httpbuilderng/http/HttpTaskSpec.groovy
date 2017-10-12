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

import com.stehno.ersatz.ErsatzServer
import com.stehno.gradle.testing.UsesGradleBuild
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.AutoCleanup
import spock.lang.Specification

import static com.stehno.ersatz.ContentType.TEXT_PLAIN

class HttpTaskSpec extends Specification implements UsesGradleBuild {

    @Rule TemporaryFolder projectRoot = new TemporaryFolder()

    @AutoCleanup(value = 'stop') private ErsatzServer ersatz = new ErsatzServer()

    def 'usage'() {
        setup:
        ersatz.expectations {
            get('/notify').called(1).responder {
                content 'ok', TEXT_PLAIN
            }
        }

        buildFile(extension: """
            import io.github.httpbuilderng.http.HttpTask

            task goGet(type:HttpTask){
                config {
                    request.uri = '${ersatz.httpUrl}'
                }
                get {
                    request.uri.path = '/notify'
                    response.success { fs, obj ->
                        println 'I received: ' + obj 
                    }
                }
            }
        """)

        when:
        BuildResult result = gradleRunner('goGet').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I received: ok']

        and:
        ersatz.verify()
    }

    @Override
    String getBuildTemplate() {
        '''
            plugins {
                id 'io.github.http-builder-ng.http-plugin'
            }
            repositories {
                jcenter()
            }
            ${config.extension ?: ''}
        '''
    }
}
