package com.rcode3.etree_test

import io.kotlintest.Spec
import io.kotlintest.specs.ShouldSpec
import java.io.File
import java.io.OutputStream
import java.util.*

/**
 * Does benchmarks of loading the data.
 */
class BenchmarkLoadIpMapTest : ShouldSpec() {

    var tmpFile : File? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        val resource = classLoader.getResource( "net_addr.jsonlines" )
        tmpFile = createTempFile( "net_addr", "jsonlines" )

        val inputStream = resource.openStream()
        val outputStream = tmpFile?.outputStream()
        inputStream.copyTo( outputStream as OutputStream )
        inputStream.close()
        outputStream.close()
    }

    override fun afterSpec(spec: Spec) {
        tmpFile?.delete()
    }

    init {

        should( "load IP map" ).config(invocations = 5) {
            var startTime = Date()
            loadIpMapFromJsonLines( tmpFile?.absolutePath ?: "no tmpFile" )
            val duration = Date().time - startTime.time
            println( "Test time was $duration")
        }

    }
}