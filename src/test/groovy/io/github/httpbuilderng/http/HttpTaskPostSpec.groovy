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

import com.stehno.ersatz.Decoders
import com.stehno.ersatz.ErsatzServer
import com.stehno.gradle.testing.GradleBuild
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import static GradleBuild.textContainsLines
import static GradleBuild.totalSuccess
import static com.stehno.ersatz.ContentType.APPLICATION_JSON
import static com.stehno.ersatz.ContentType.TEXT_PLAIN

class HttpTaskPostSpec extends Specification {

    @Rule GradleBuild gradle = new GradleBuild(
        template: '''
            plugins {
                id 'io.github.http-builder-ng.http-plugin'
            }
            repositories {
                jcenter()
            }
            
            ${config.globalConfig ?: ''}
            
            task makeRequest(type:io.github.httpbuilderng.http.HttpTask){
                ${config.taskConfig ?: ''}
            }
        '''
    )

    @AutoCleanup(value = 'stop') private ErsatzServer ersatz = new ErsatzServer({
        decoder APPLICATION_JSON, Decoders.parseJson
    })

    def 'single POST request'() {
        setup:
        ersatz.expectations {
            post('/notify') {
                called 1
                body APPLICATION_JSON, id: 42
                responder {
                    code 200
                    content 'ok', TEXT_PLAIN
                }
            }
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
            }
            post {
                request.uri.path = '/notify'
                request.body = [id:42]
                request.contentType = 'application/json'
                response.success { fs, obj ->
                    println 'I received: ' + obj 
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I received: ok']

        and:
        ersatz.verify()
    }

    def 'multiple POST requests'() {
        setup:
        ersatz.expectations {
            post('/multiple') {
                called 3
                body APPLICATION_JSON, id: 42
                responder {
                    content 'ok', TEXT_PLAIN
                }
                responder {
                    content 'good', TEXT_PLAIN
                }
                responder {
                    content 'bueno', TEXT_PLAIN
                }
            }
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
                response.success { fs, obj ->
                    println 'I received: ' + obj 
                }
            }
            postAsync {
                request.uri.path = '/multiple'
                request.body = [id:42]
                request.contentType = 'application/json'
            }
            post {
                request.uri.path = '/multiple'
                request.body = [id:42]
                request.contentType = 'application/json'
            }
            post {
                request.uri.path = '/multiple'
                request.body = [id:42]
                request.contentType = 'application/json'
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I received: ok', 'I received: good', 'I received: bueno']

        and:
        ersatz.verify()
    }

    @Unroll 'single POST request (external config with #library)'() {
        setup:
        ersatz.expectations {
            post('/something') {
                called 1
                body APPLICATION_JSON, id: 42
                responder {
                    content 'ok', TEXT_PLAIN
                }
            }
        }

        gradle.buildFile(
            globalConfig: """
                http {
                    library = io.github.httpbuilderng.http.HttpLibrary.$library
                    config {
                        request.uri = '${ersatz.httpUrl}'
                    }
                }
            """,
            taskConfig: """
            post {
                request.uri.path = '/something'
                request.body = [id:42]
                request.contentType = 'application/json'
                response.success { fs, obj ->
                    println 'I received: ' + obj 
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I received: ok']

        and:
        ersatz.verify()

        where:
        library << HttpLibrary.values()*.name()
    }

    @Unroll 'single POST request (external config with #library as string)'() {
        setup:
        ersatz.expectations {
            post('/something') {
                called 1
                body APPLICATION_JSON, id: 42
                responder {
                    content 'ok', TEXT_PLAIN
                }
            }
        }

        gradle.buildFile(
            globalConfig: """
                http {
                    library = '$library'
                    config {
                        request.uri = '${ersatz.httpUrl}'
                    }
                }
            """,
            taskConfig: """
                post {
                    request.uri.path = '/something'
                    request.body = [id:42]
                    request.contentType = 'application/json'
                    response.success { fs, obj ->
                        println 'I received: ' + obj 
                    }
                }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I received: ok']

        and:
        ersatz.verify()

        where:
        library << HttpLibrary.values()*.name()*.toLowerCase()
    }
}
