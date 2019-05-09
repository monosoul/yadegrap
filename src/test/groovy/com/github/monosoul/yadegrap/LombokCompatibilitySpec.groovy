package com.github.monosoul.yadegrap


import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
class LombokCompatibilitySpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    def buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle.kts')
        testProjectDir.newFile('settings.gradle.kts') << "rootProject.name = \"lombokTest\""
    }

    def "should work with lombok #lombokVersion"() {
        setup:
            buildFile << """
            import com.github.monosoul.yadegrap.DelombokTask

            plugins {
                java
                id("com.github.monosoul.yadegrap")
            }
            
            repositories {
                jcenter()
            }
            
            dependencies {
                compileOnly("org.projectlombok:lombok:$lombokVersion")
            }
            
            tasks {
                "delombok"(DelombokTask::class) {
                    verbose = true
                }
            }
            """
        and:
            def somePojoFileName = "SomePojo.java"
        and:
            def somePojoFile = new File(getClass().getResource("/$somePojoFileName").toURI())
            testProjectDir.newFolder('src', 'main', 'java')
            def target = testProjectDir.newFile("src/main/java/$somePojoFileName")
            Files.copy(somePojoFile, target)
        when:
            def result = GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments("delombok", "--stacktrace")
                    .withPluginClasspath()
                    .build()
        then:
            result.output.contains("${target.canonicalPath} [delomboked]")
            result.task(":delombok").outcome == SUCCESS
        where:
            lombokVersion << [
					"1.18.8",
                    "1.18.6",
                    "1.18.4",
                    "1.18.2",
                    "1.18.0",
                    "1.16.22",
                    "1.16.20",
                    "1.16.18",
                    "1.16.16",
                    "1.16.14",
                    "1.16.12",
                    "1.16.10",
                    "1.16.8",
                    "1.16.6",
                    "1.16.4"
            ]
    }
}
