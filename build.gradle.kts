plugins {
    application
    kotlin("jvm") version "1.3.10"
}

application {
    mainClassName = "com.rcode3.etree_test.MainKt"
}

dependencies {
    compile(kotlin("stdlib"))
    compile( "net.ripe.ipresource:ipresource:1.46")
}

repositories {
    jcenter()
}