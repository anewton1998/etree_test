package com.rcode3.etree_test

import arrow.core.toT
import io.kotlintest.Spec
import io.kotlintest.specs.ShouldSpec
import net.ripe.ipresource.IpRange
import net.ripe.ipresource.etree.NestedIntervalMap
import java.io.File
import java.io.OutputStream
import java.util.*

/**
 * Does benchmarks of loading the data.
 */
class BenchmarkLoadStructuredIntMapTest : ShouldSpec() {

    var jsonLinesFile : File? = null
    var psvFile       : File? = null

    var startTime = Date()
    var map : IpMap? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        var resource = classLoader.getResource( "net_addr.jsonlines" )
        jsonLinesFile = createTempFile( "net_addr_structured_int", "jsonlines" )
        val jsonOut = jsonLinesFile?.outputStream()?.writer()
        var resourceIn = resource.openStream().reader()
        if (jsonOut != null) {
            convertIpToStructuredIntJsonLines( resourceIn, jsonOut )
        }
        jsonOut?.close()
        resourceIn.close()

        resource = classLoader.getResource( "net_addr.psv" )
        psvFile = createTempFile( "net_addr_structured_int", "psv" )
        val psvOut = psvFile?.outputStream()?.writer()
        resourceIn = resource.openStream().reader()
        if (psvOut != null) {
            convertIpToStructuredIntPsv( resource.openStream().reader(), psvOut )
        }
        psvOut?.close()
        resourceIn.close()
    }

    override fun afterSpec(spec: Spec) {
        jsonLinesFile?.delete()
        psvFile?.delete()
    }

    init {

        should( "time load of structured int map from jsonlines" ).config(invocations = 5) {
            beforeBenchmark()
            map = loadStructuredIntMapFromJsonLines( jsonLinesFile?.absolutePath ?: "no jasonLinesFile" )
            afterBenchmark()
        }

        should( "time load of structured int map from PSV" ).config( invocations = 5 ) {
            beforeBenchmark()
            map = loadStructuredIntMapFromPsv( psvFile?.absolutePath ?: "no psvFile" )
            afterBenchmark()
        }
    }

    /**
     * The builtin [beforeTest] and [afterTest] don't actually get called for every invocation. So we do this
     * instead.
     */
    fun beforeBenchmark() {
        System.gc()
        println( "------")
        println( this.javaClass )
        startTime = Date()
    }

    /**
     * The builtin [beforeTest] and [afterTest] don't actually get called for every invocation. So we do this
     * instead.
     */
    fun afterBenchmark() {
        val duration = Date().time - startTime.time
        println( "Test time was $duration")
        val runtime = Runtime.getRuntime()
        println( "Used memory  : ${runtime.totalMemory() - runtime.freeMemory()}")
        println( "Free memory  : ${runtime.freeMemory()}")
        println( "Total memory : ${runtime.totalMemory()}")
        println( "------")
    }
}