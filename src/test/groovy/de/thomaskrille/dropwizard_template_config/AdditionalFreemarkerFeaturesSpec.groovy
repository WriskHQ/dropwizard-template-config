package de.thomaskrille.dropwizard_template_config

import org.apache.commons.io.IOUtils
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.containsString

class AdditionalFreemarkerFeaturesSpec extends Specification {

    def TestEnvironmentProvider environmentProvider = new TestEnvironmentProvider()

    def TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
            new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                    environmentProvider,
                    new DefaultSystemPropertiesProvider())

    def 'conditionally enable https - on'() {
        given:
        def config = '''
                server:
                  applicationConnectors:
                    - type: http
                      port: ${env.PORT!'8080'}
                <#if env.ENABLE_SSL == 'true'>
                    - type: https
                      port: ${env.SSL_PORT!'8443'}
                      keyStorePath: ${env.SSL_KEYSTORE_PATH}
                      keyStorePassword: ${env.SSL_KEYSTORE_PASS}
                </#if>
                '''

        environmentProvider.put('ENABLE_SSL', 'true')
        environmentProvider.put('SSL_KEYSTORE_PATH', 'example.keystore')
        environmentProvider.put('SSL_KEYSTORE_PASS', 'secret')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('- type: http')
        parsedConfigAsString containsString('port: 8080')
        parsedConfigAsString containsString('- type: https')
        parsedConfigAsString containsString('port: 8443')
        parsedConfigAsString containsString('keyStorePath: example.keystore')
        parsedConfigAsString containsString('keyStorePassword: secret')
    }

    def 'conditionally enable https - off'() {
        given:
        def config = '''
                server:
                  applicationConnectors:
                    - type: http
                      port: ${env.PORT!'8080'}
                <#if env.ENABLE_SSL == 'true'>
                    - type: https
                      port: ${env.SSL_PORT!'8443'}
                      keyStorePath: ${env.SSL_KEYSTORE_PATH}
                      keyStorePassword: ${env.SSL_KEYSTORE_PASS}
                </#if>
                '''

        environmentProvider.put('ENABLE_SSL', 'false')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('- type: http')
        parsedConfigAsString containsString('port: 8080')
    }

    def 'simulating application profiles - production profile'() {
        given:
        def config = '''
                logging:
                <#if env.PROFILE == 'production'>
                  level: WARN
                  loggers:
                    com.example.my_app: INFO
                    org.hibernate.SQL: OFF
                  appenders:
                    - type: syslog
                      host: localhost
                      facility: local0
                <#elseif env.PROFILE == 'development'>
                  level: INFO
                  loggers:
                    com.example.my_app: DEBUG
                    org.hibernate.SQL: DEBUG
                  appenders:
                    - type: console
                </#if>
                '''

        environmentProvider.put('PROFILE', 'production')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('level: WARN')
        parsedConfigAsString containsString('com.example.my_app: INFO')
        parsedConfigAsString containsString('org.hibernate.SQL: OFF')
        parsedConfigAsString containsString('- type: syslog')
        parsedConfigAsString containsString('host: localhost')
        parsedConfigAsString containsString('facility: local0')
    }

    def 'simulating application profiles - development profile'() {
        given:
        def config = '''
                logging:
                <#if env.PROFILE == 'production'>
                  level: WARN
                  loggers:
                    com.example.my_app: INFO
                    org.hibernate.SQL: OFF
                  appenders:
                    - type: syslog
                      host: localhost
                      facility: local0
                <#elseif env.PROFILE == 'development'>
                  level: INFO
                  loggers:
                    com.example.my_app: DEBUG
                    org.hibernate.SQL: DEBUG
                  appenders:
                    - type: console
                </#if>
                '''

        environmentProvider.put('PROFILE', 'development')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('level: INFO')
        parsedConfigAsString containsString('com.example.my_app: DEBUG')
        parsedConfigAsString containsString('org.hibernate.SQL: DEBUG')
        parsedConfigAsString containsString('- type: console')
    }

}