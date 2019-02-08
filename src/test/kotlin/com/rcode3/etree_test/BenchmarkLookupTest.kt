package com.rcode3.etree_test

import io.kotlintest.Spec
import io.kotlintest.specs.ShouldSpec
import net.ripe.ipresource.IpAddress
import net.ripe.ipresource.IpRange
import net.ripe.ipresource.etree.NestedIntervalMap
import java.io.File
import java.io.OutputStream
import java.util.*

/**
 * Does a benchmark on lookups.
 */
class BenchmarkLookupTest : ShouldSpec() {

    var jsonLinesFile : File? = null

    var startTime = Date()
    var map : NestedIntervalMap<IpRange, String>? = null

    var ips : List<String>? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        var resource = classLoader.getResource( "net_addr.jsonlines" )
        jsonLinesFile = createTempFile( "net_addr", "jsonlines" )

        var inputStream = resource.openStream()
        var outputStream = jsonLinesFile?.outputStream()
        inputStream.copyTo( outputStream as OutputStream)
        inputStream.close()
        outputStream.close()

        map = loadIpMapFromJsonLines( jsonLinesFile?.absolutePath ?: "no jasonLinesFile" )

        resource = classLoader.getResource( "ips.txt" )
        ips = File( resource.toURI() ).readLines()
    }

    override fun afterSpec(spec: Spec) {
        jsonLinesFile?.delete()
    }

    init {

        should( "lookup IPs" ).config( invocations = 10 ) {
            beforeBenchmark()
            ips?.forEach{
                val ip = IpAddress.parse( it )
                map?.findExactOrFirstLessSpecific( IpRange.range( ip, ip ) )
            }
            afterBenchmark()
        }

    }

    /**
     * The builtin [beforeTest] and [afterTest] don't actually get called for every invocation. So we do this
     * instead.
     */
    fun beforeBenchmark() {
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
    }
}