# Gradle HTTP Plugin

A Gradle plugin providing the ability to define tasks to make HTTP requests using the HttpBuilder-NG client library.

> This is in the very early stages of development... probably best to stand back a bit. :-)

The goal is to provide an interface something like:

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