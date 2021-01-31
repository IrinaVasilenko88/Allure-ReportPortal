# Обучение в Нетологии.

## Домашнее задание по курсу Автоматизированное тестирование

## Тема: Репортинг: Report Portal

- Настройка Allure, интегрированного с Selenide
- Настройка ReportPortal (инструкция ниже)



# Краткая инструкция по установке ReportPortal
1. Создать проект в **IDEA** на базе **Gradle**
1. **build.gradle** должен выглядеть как 
```
plugins {
    id 'java'
    id "io.freefair.lombok" version "5.3.0"
    
}

group 'ru.netology'
version '1.0-SNAPSHOT'


compileJava.options.encoding = "UTF-8"
compileTestJava.options.encoding = "UTF-8"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven { url "http://dl.bintray.com/epam/reportportal" }

}


allure{
    autoconfigure=true
    version='2.13.8'

    useJUnit5{
        version='2.13.8'
    }
}
sourceCompatibility = 1.8

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.6.1'
    testImplementation 'com.github.javafaker:javafaker:1.0.2'
    testImplementation 'com.codeborne:selenide:5.16.2'
    testRuntimeOnly 'org.slf4j:slf4j-simple:1.7.30'
    implementation 'com.epam.reportportal:agent-java-junit5:5.0.0'
    implementation 'com.epam.reportportal:logger-java-logback:5.0.2'
    implementation 'ch.qos.logback:logback-classic:1.2.3'
    implementation 'com.epam.reportportal:logger-java-log4j:5.0.2'
    compileOnly 'log4j:log4j:1.2.17'
    implementation 'org.apache.logging.log4j:log4j-api:2.13.3'
    implementation 'org.apache.logging.log4j:log4j-core:2.13.3'
}
test {
    testLogging.showStandardStreams = true
    useJUnitPlatform()
    systemProperty 'selenide.headless', System.getProperty('selenide.headless')
    systemProperty 'selenide.browser', 'chrome'
    systemProperty 'junit.jupiter.extensions.autodetection.enabled', true
}
```
(В данном файле также интегрирован **selenide**)

1. Создать файл **docker-compose.yml** и скопировать файл из [docker-compose.yml для windows](https://github.com/reportportal/reportportal/blob/master/docker-compose.yml) 
1. Раскомментировать строки 'for windows host'
```
  volumes:
      # For windows host
      - postgres:/var/lib/postgresql/data
      # For unix host
      # - ./data/postgres:/var/lib/postgresql/data
 ```
    
3. Раскомментировать строки
    
```
     # Docker volume for Windows host
volumes:
  postgres:
```

4. Создать папку  **/META-INF/services** в **resources**
5. Положить туда файл, названный **org.junit.jupiter.api.extension.Extension**
6. В данный файл прописать имплементацию одной строкой 

 ```
 com.epam.reportportal.junit5.ReportPortalExtension
 ```
 
 7. Создать файл **log4j2.xml** file в папке **resources** и прописать 
 ```
 <?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </Console>
        <ReportPortalLog4j2Appender name="ReportPortalAppender">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </ReportPortalLog4j2Appender>
    </Appenders>
    <Loggers>
        <Root level="DEBUG">
            <AppenderRef ref="ConsoleAppender"/>
            <AppenderRef ref="ReportPortalAppender"/>
        </Root>
    </Loggers>
</Configuration>
```
 8. Создать файл **logback.xml** file в папке **resources** и прописать 
 
 ```
 <?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- Send debug messages to System.out -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- By default, encoders are assigned the type ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{5} - %thread - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="RP" class="com.epam.reportportal.logback.appender.ReportPortalAppender">
        <encoder>
            <!--Best practice: don't put time and logging level to the final message. Appender do this for you-->
            <pattern>%d{HH:mm:ss.SSS} [%t] %-5level - %msg%n</pattern>
            <pattern>[%t] - %msg%n</pattern>
        </encoder>
    </appender>

    <!--'additivity' flag is important! Without it logback will double-log log messages-->
    <logger name="binary_data_logger" level="TRACE" additivity="false">
        <appender-ref ref="RP"/>
    </logger>

    <logger name="com.epam.reportportal.service" level="WARN"/>
    <logger name="com.epam.reportportal.utils" level="WARN"/>

    <!-- By default, the level of the root level is set to DEBUG -->
    <root level="TRACE">
        <appender-ref ref="RP"/>
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```

9. Для загрузки и запуска **ReportPortal** прописать в терминале команду 

```
docker-compose -p reportportal up -d --force-recreate
```

10. После того, как все файлы и контейнеры загрузятся открыть в браузере <http://localhost:8080>
11. Необходимо залогиниться в качестве админа, используя данные с официального сайта 
```
Логин superadmin
Пароль erebus
```

12. Далее добавляем пользователя в проект по шагам, открывая вкладки:
```
 Administrative -> My Test Project -> Members -> Add user
 ```
 13. Вводим имя и пароль для нового пользователя.
 14. Необходимо перелогиниться под только что созданным пользователем.
 15. Нажать на иконку пользователя(**user**) и выбрать **Profile**
 15. Во вкладке **Configuration Examples** будет пример файла **reportportal.properties** для данного пользователя.
 16. Создать в проекте в **IDEA** также в папке **resources** файл **reportportal.properties**
 17. Скопировать данные из **Configuration Examples** в данный файл в **IDEA**
 18. Создать в папке **resources** файл **junit-platform.properties** и добавить в него строку: 
  ```
 junit.jupiter.extensions.autodetection.enabled=true
  ```
 19. После того, как **JUnit** подключился к **ReportPortal** нужно запустить приложение и запустить тесты.
 20. На странице с **ReportPortal** слева нажать на вкладку **Launches**, после чего справа появится название launches эквивалентное указанному в файле **reportportal.properties**
 21. Нажав на нее, появится список тестов. Если нажать на каждый из них, то можно увидеть отчеты и логи.
