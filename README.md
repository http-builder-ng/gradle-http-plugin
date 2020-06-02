# Gradle HTTP Plugin

[![Build Status](https://travis-ci.org/http-builder-ng/gradle-http-plugin.svg?branch=master)](https://travis-ci.org/http-builder-ng/gradle-http-plugin) [![Coverage Status](https://coveralls.io/repos/github/http-builder-ng/gradle-http-plugin/badge.svg?branch=master)](https://coveralls.io/github/http-builder-ng/gradle-http-plugin?branch=master)

> Dormant - The HttpBuilder-NG project is going dormant. Neither of us use the project any longer nor do we have the extra time to properly maintain it. Please feel free to fork it and move it forward, or contact us (with an issue) to discuss options for taking over the project.

## Quick Links

* Site: https://http-builder-ng.github.io/gradle-http-plugin/
* Project: https://github.com/http-builder-ng/gradle-http-plugin
* Issues: https://github.com/http-builder-ng/gradle-http-plugin/issues
* GroovyDocs: https://http-builder-ng.github.io/gradle-http-plugin/docs/groovydoc/
* User Guide: https://http-builder-ng.github.io/gradle-http-plugin/asciidoc/html5/

## Introduction

A Gradle plugin providing the ability to define tasks to make HTTP requests using the HttpBuilder-NG client library. The resulting tasks have a clean
DSL and will look something like the following:

Groovy
```groovy
task notify(type:HttpTask){
    config {
        request.uri = 'http://something.com'
    }
    post {
        request.uri.path = '/notify'
        request.body = [event: 'activated']
        response.success {
            println 'The event notification was successful'
        }
    }
}
```

Kotlin
```kotlin
tasks {
    val notify by registering(HttpTask::class) {
        config {
            it.request.setUri = "http://something.com"
        }
        post {
            it.request.uri.setPath("/notify")
        }
        response.success { fromServer, body ->
            println("The event notification was successful")
        }
    }
}
```

## Installing

The plugin is available through the [Gradle Plugin Repository](https://plugins.gradle.org/plugin/io.github.http-builder-ng.http-plugin) and may be
applied to your Gradle build with one of the following:

```groovy
plugins {
  id "io.github.http-builder-ng.http-plugin" version "0.1.1"
}
```

or

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.io.github.http-builder-ng:http-plugin:0.1.1"
  }
}

apply plugin: "io.github.http-builder-ng.http-plugin"
```

## Building

The project is build with Gradle using the following command:

    ./gradlew clean build

## License

The Gradle HTTP Plugin is licensed under the [Apache 2](http://www.apache.org/licenses/LICENSE-2.0) open source license.

    Copyright 2017 HttpBuilder-NG Project

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
    in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software distributed under the License
    is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
    or implied. See the License for the specific language governing permissions and limitations under
    the License.

