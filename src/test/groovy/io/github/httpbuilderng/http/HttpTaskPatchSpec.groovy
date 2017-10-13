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

class HttpTaskPatchSpec extends Specification {

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

    @AutoCleanup(value = 'stop') private ErsatzServer ersatz = new ErsatzServer({
        decoder APPLICATION_JSON, Decoders.parseJson
    })

    def 'single PATCH request'() {
        setup:
        ersatz.expectations {
            patch('/notify') {
                called 1
                body APPLICATION_JSON, id: 42
                responds().code(200)
            }
        }

        gradle.buildFile(
            globalConfig: """
                http {
                    library = 'apache'
                }
            """,
            taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
            }
            patch {
                request.uri.path = '/notify'
                request.body = [id:42]
                request.contentType = 'application/json'
                response.when(200){ 
                    println 'I succeeded'
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I succeeded']

        and:
        ersatz.verify()
    }

    def 'multiple PATCH requests'() {
        setup:
        ersatz.expectations {
            patch('/multiple') {
                called 3
                body APPLICATION_JSON, id: 42
                responds().code(200)
            }
        }

        gradle.buildFile(
            globalConfig: """
                http {
                    library = 'okhttp'
                }
            """,
            taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
                response.success {
                    println 'I succeeded'
                }
            }
            patchAsync {
                request.uri.path = '/multiple'
                request.body = [id:42]
                request.contentType = 'application/json'
            }
            patch {
                request.uri.path = '/multiple'
                request.body = [id:42]
                request.contentType = 'application/json'
            }
            patch {
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
        textContainsLines result.output, ['I succeeded']

        and:
        ersatz.verify()
    }

    def 'multiple PATCH requests (consumer)'() {
        setup:
        ersatz.expectations {
            patch('/multiple') {
                called 2
                body APPLICATION_JSON, id: 42
                responds().code(200)
            }
        }

        gradle.buildFile(
            globalConfig: """
                http {
                    library = 'okhttp'
                }
            """,
            taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
                response.success {
                    println 'I succeeded'
                }
            }
            patchAsync(new Consumer<HttpConfig>() {
                @Override void accept(HttpConfig cfg) {
                    cfg.request.uri.path = '/multiple'
                    cfg.request.body = [id:42]
                    cfg.request.contentType = 'application/json'
                }
            })
            patch(new Consumer<HttpConfig>() {
                @Override void accept(HttpConfig cfg) {
                    cfg.request.uri.path = '/multiple'
                    cfg.request.body = [id:42]
                    cfg.request.contentType = 'application/json'
                }
            })
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I succeeded']

        and:
        ersatz.verify()
    }

    @Unroll 'single PATCH request (external config with #library)'() {
        setup:
        ersatz.expectations {
            patch('/something') {
                called 1
                body APPLICATION_JSON, id: 42
                responds().code(200)
            }
        }

        gradle.buildFile(
            globalConfig: """
                http {
                    library = $library
                    config {
                        request.uri = '${ersatz.httpUrl}'
                    }
                }
            """,
            taskConfig: """
            patch {
                request.uri.path = '/something'
                request.body = [id:42]
                request.contentType = 'application/json'
                response.success { 
                    println 'I succeeded' 
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I succeeded']

        and:
        ersatz.verify()

        where:
        // Note: the core library does not support PATCH
        library << [
            "io.github.httpbuilderng.http.HttpLibrary.APACHE",
            "io.github.httpbuilderng.http.HttpLibrary.OKHTTP",
            "'apache'",
            "'okhttp'",
        ]
    }
}
