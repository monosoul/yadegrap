package com.github.monosoul.yadegrap


import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
class LombokCompatibilitySpec extends Specification {

    @TempDir
    File testProjectDir

    def "should work with lombok #lombokVersion"() {
        setup:
            def buildFile = new File(testProjectDir, 'build.gradle.kts')
            new File(testProjectDir, 'settings.gradle.kts') << "rootProject.name = \"lombokTest\""
            buildFile << """
            import com.github.monosoul.yadegrap.DelombokTask

            plugins {
                java
                id("com.github.monosoul.yadegrap")
            }
            
            repositories {
                mavenCentral()
            }
            
            dependencies {
                compileOnly("org.projectlombok:lombok:$lombokVersion")
            }
            
            tasks {
                "delombok"(DelombokTask::class) {
                    verbose.set(true)
                }
            }
            """
        and:
            def somePojoFileName = "SomePojo.java"
        and:
            def somePojoFile = new File(getClass().getResource("/$somePojoFileName").toURI())
            new File(testProjectDir, 'src/main/java').mkdirs()
            def target = new File(testProjectDir, "src/main/java/$somePojoFileName")
            Files.copy(somePojoFile, target)
        when:
            def result = GradleRunner.create()
                    .withProjectDir(testProjectDir)
                    .withArguments("delombok", "--stacktrace")
                    .withPluginClasspath()
                    .build()
        then:
            result.output.contains("${target.canonicalPath} [delomboked]")
            result.task(":delombok").outcome == SUCCESS
        where:
            lombokVersion << [
					"1.18.20",
					"1.18.18",
					"1.18.16",
					"1.18.14",
					"1.18.12",
					"1.18.10",
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
