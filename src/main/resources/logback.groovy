appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%-5level %logger{10} - %msg%n'
    }
}

logger('org.eclipse.jetty', INFO)

root(DEBUG, ['STDOUT'])