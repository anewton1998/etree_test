import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jackson_version = "2.9.5"

plugins {
    application
    kotlin("jvm") version "1.3.20"
}

group = "com.rcode3"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.rcode3.etree_test.MainKt"
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    compile( "net.ripe.ipresource:ipresource:1.46")
    compile( "com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version" )

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.2.1")
}

repositories {
    jcenter()
    mavenCentral()
}

tasks.withType<KotlinCompile>{
    kotlinOptions{
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    maxHeapSize = "5g"
}

tasks.withType<Wrapper> {
    gradleVersion = "5.1.1"
}