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

class HttpTaskHeadSpec extends Specification {

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

    def 'single HEAD request'() {
        setup:
        ersatz.expectations {
            head('/notify').called(1).responds().code(200)
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
            }
            head {
                request.uri.path = '/notify'
                response.success { 
                    println 'I have arrived!'
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I have arrived!']

        and:
        ersatz.verify()
    }

    def 'multiple HEAD requests'() {
        setup:
        ersatz.expectations {
            head('/notify').called(1).responds().code(200)
            head('/other').called(1).responds().code(200)
            head('/third').called(1).responds().code(200)
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
            }
            headAsync {
                request.uri.path = '/third'
                response.success { 
                    println 'I have arrived A!' 
                }
            }
            head {
                request.uri.path = '/notify'
                response.success { 
                    println 'I have arrived B!' 
                }
            }
            head {
                request.uri.path = '/other'
                response.success { 
                    println 'I have arrived C!' 
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I have arrived A!', 'I have arrived B!', 'I have arrived C!']

        and:
        ersatz.verify()
    }

    def 'multiple HEAD requests (consumer)'() {
        setup:
        ersatz.expectations {
            head('/notify').called(2).responds().code(200)
        }

        gradle.buildFile(taskConfig: """
            config {
                request.uri = '${ersatz.httpUrl}'
                response.success { 
                    println 'I have arrived!' 
                }
            }
            headAsync(new Consumer<HttpConfig>() {
                @Override void accept(HttpConfig cfg) {
                    cfg.request.uri.path = '/notify'
                }
            })
            head(new Consumer<HttpConfig>() {
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
        textContainsLines result.output, ['I have arrived!']

        and:
        ersatz.verify()
    }

    @Unroll 'single HEAD request (external config with #library)'() {
        setup:
        ersatz.expectations {
            head('/notify').called(1).responds().code(200)
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
            head {
                request.uri.path = '/notify'
                response.success { 
                    println 'I have arrived!' 
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I have arrived!']

        and:
        ersatz.verify()

        where:
        library << HttpLibrary.values()*.name()
    }

    @Unroll 'single HEAD request (external config with #library as string)'() {
        setup:
        ersatz.expectations {
            head('/notify').called(1).responds().code(200)
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
            head {
                request.uri.path = '/notify'
                response.success { 
                    println 'I have arrived!' 
                }
            }
        """)

        when:
        BuildResult result = gradle.runner('makeRequest').build()

        then:
        totalSuccess result

        and:
        textContainsLines result.output, ['I have arrived!']

        and:
        ersatz.verify()

        where:
        library << HttpLibrary.values()*.name()*.toLowerCase()
    }
}
