package com.rcode3.etree_test

import io.kotlintest.Spec
import io.kotlintest.specs.ShouldSpec
import java.io.File
import java.util.*

/**
 * Does benchmarks of loading the data.
 */
class BenchmarkLoadSortedIntMapTest : ShouldSpec() {

    var jsonLinesFile : File? = null
    var psvFile       : File? = null
    var sortedPsvFile : File? = null

    var startTime = Date()
    var map : IpMap? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        var resource = classLoader.getResource( "net_addr.jsonlines" )
        jsonLinesFile = createTempFile( "net_addr_unsorted_int", "jsonlines" )
        val jsonOut = jsonLinesFile?.outputStream()?.writer()
        var resourceIn = resource.openStream().reader()
        if (jsonOut != null) {
            convertIpToStructuredIntJsonLines( resourceIn, jsonOut )
        }
        jsonOut?.close()
        resourceIn.close()

        resource = classLoader.getResource( "net_addr.psv" )
        psvFile = createTempFile( "net_addr_unsorted_int", "psv" )
        val psvOut = psvFile?.outputStream()?.writer()
        resourceIn = resource.openStream().reader()
        if (psvOut != null) {
            convertIpToStructuredIntPsv( resource.openStream().reader(), psvOut )
        }
        psvOut?.close()
        resourceIn.close()

        map = loadStructuredIntMapFromPsv( psvFile?.absolutePath ?: "no psvfile"  )
        sortedPsvFile = createTempFile( "net_addr_sorted_int", "psv" )
        saveStructuredIntMapToPsv(sortedPsvFile?.absolutePath!!, map!!)
    }

    override fun afterSpec(spec: Spec) {
        jsonLinesFile?.delete()
        psvFile?.delete()
    }

    init {

        should( "time load of sorted structured int map from PSV" ).config( invocations = 5 ) {
            beforeBenchmark()
            map = loadStructuredIntMapFromPsv( sortedPsvFile?.absolutePath ?: "no psvFile" )
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