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
import com.stehno.gradle.testing.GradleBuild
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import static GradleBuild.textContainsLines
import static GradleBuild.totalSuccess
import static com.stehno.ersatz.ContentType.TEXT_PLAIN

class HttpTaskGetSpec extends Specification {

    @Rule GradleBuild gradle = new GradleBuild(
        template: '''
            plugins {
                id 'io.github.http-builder-ng.http-plugin'
            }
            repositories {
                jcenter()
            }
            
            import groovyx.net.http.HttpConfig
            import java.util.function.Consumer
            
            ${config.globalConfig ?: ''}
            
            task makeRequest(type:io.github.httpbuilderng.http.HttpTask){
                ${config.taskConfig ?: ''}
            }
        '''
    )

    @AutoCleanup(value = 'stop') private ErsatzServer ersatz = new ErsatzServer()

    def 'single GET request'() {
        setup:
        ersatz.expectations {
            get('/notify').called(1).responder {
                content 'ok', TEXT_PLAIN
            }
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
            }
            get {
                request.uri.path = '/notify'
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

    def 'multiple GET requests'() {
        setup:
        ersatz.expectations {
            get('/notify').called(1).responder {
                content 'ok', TEXT_PLAIN
            }
            get('/other').called(1).responder {
                content 'good', TEXT_PLAIN
            }
            get('/third').called(1).responder {
                content 'bueno', TEXT_PLAIN
            }
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
                response.success { fs, obj ->
                    println 'I received: ' + obj 
                }
            }
            getAsync {
                request.uri.path = '/third'
            }
            get {
                request.uri.path = '/notify'
            }
            get {
                request.uri.path = '/other'
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

    def 'multiple GET requests (consumer)'() {
        setup:
        ersatz.expectations {
            get('/notify').called(2).responder {
                content 'ok', TEXT_PLAIN
            }
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
                response.success { fs, obj ->
                    println 'I received: ' + obj 
                }
            }
            getAsync(new Consumer<HttpConfig>() {
                @Override void accept(HttpConfig cfg) {
                    cfg.request.uri.path = '/notify'
                }
            })
            get(new Consumer<HttpConfig>() {
                @Override void accept(HttpConfig cfg) {
                    cfg.request.uri.path = '/notify'
                }
            })
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

    @Unroll 'single GET request (external config with #library)'() {
        setup:
        ersatz.expectations {
            get('/notify').called(1).responder {
                content 'ok', TEXT_PLAIN
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
            get {
                request.uri.path = '/notify'
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

    @Unroll 'single GET request (external config with #library as string)'() {
        setup:
        ersatz.expectations {
            get('/notify').called(1).responder {
                content 'ok', TEXT_PLAIN
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
            get {
                request.uri.path = '/notify'
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
