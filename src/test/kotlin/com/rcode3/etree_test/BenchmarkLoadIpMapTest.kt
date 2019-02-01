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

    var jsonLinesFile : File? = null
    var psvFile       : File? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        var resource = classLoader.getResource( "net_addr.jsonlines" )
        jsonLinesFile = createTempFile( "net_addr", "jsonlines" )

        var inputStream = resource.openStream()
        var outputStream = jsonLinesFile?.outputStream()
        inputStream.copyTo( outputStream as OutputStream )
        inputStream.close()
        outputStream.close()

        resource = classLoader.getResource( "net_addr.psv" )
        psvFile = createTempFile( "net_addr", "psv" )

        inputStream = resource.openStream()
        outputStream = psvFile?.outputStream()
        inputStream.copyTo( outputStream as OutputStream )
        inputStream.close()
        outputStream.close()
    }

    override fun afterSpec(spec: Spec) {
        jsonLinesFile?.delete()
        psvFile?.delete()
    }

    init {

        should( "time load of IP map from jsonlines" ).config(invocations = 5) {
            val startTime = Date()
            loadIpMapFromJsonLines( jsonLinesFile?.absolutePath ?: "no jasonLinesFile" )
            val duration = Date().time - startTime.time
            println( "Test time was $duration")
        }

        should( "time laod of IP map from PSV" ).config( invocations = 5 ) {

            val startTime = Date()
            loadIpMapFromPsv( psvFile?.absolutePath ?: "no jasonLinesFile" )
            val duration = Date().time - startTime.time
            println( "Test time was $duration")

        }
    }
}