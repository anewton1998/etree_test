package com.rcode3.etree_test

import io.kotlintest.Spec
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import net.ripe.ipresource.IpRange
import java.io.File
import java.io.File.createTempFile

/**
 * Does a basic test of loading a map for a JSON Lines file.
 */
class LoadStructuredIntMapTest : ShouldSpec () {

    var jasonLinesFile : File? = null
    var psvFile        : File? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        var resource = classLoader.getResource( "loadipmaptest.jsonlines" )
        jasonLinesFile = createTempFile( "loadstructuredintmaptest", "jsonlines" )
        val jsonOut = jasonLinesFile?.outputStream()?.bufferedWriter()
        if (jsonOut != null) {
            convertIpToStructuredIntJsonLines( resource.openStream().bufferedReader(), jsonOut )
        }
        jsonOut?.close()
        println( "---converted jsonlines" )
        println( jasonLinesFile?.readText() )
        println( "---" )

        resource = classLoader.getResource( "loadipmaptest.psv" )
        psvFile = createTempFile( "loadstructuredintmaptest", "psv" )
        val psvOut = psvFile?.outputStream()?.bufferedWriter()
        if (psvOut != null) {
            convertIpToStructuredIntPsv( resource.openStream().reader(), psvOut )
        }
        psvOut?.close()
        println( "---converted psv" )
        println( psvFile?.readText() )
        println( "---" )
    }

    override fun afterSpec(spec: Spec) {
        jasonLinesFile?.delete()
        psvFile?.delete()
    }

    init {

        should( "parse loadstructuredintmaptest.jsonlines" ) {

            val map = loadStructuredIntMapFromJsonLines( jasonLinesFile?.absolutePath ?: "no jasonLinesFile" )
            map.findExact( IpRange.parse( "2610:00E0:0000:0000:0000:0000:0000:0000/32" ) ).second shouldBe "NET6-2610-E0-1"
            map.findExact( IpRange.parse( "209.136.123.000/24" ) ).second shouldBe "NET-209-136-123-0-1"

        }

        should( "parse loadstructuredintmaptest.psv" ) {

            val map = loadStructuredIntMapFromPsv( psvFile?.absolutePath ?: "no psvFile" )
            map.findExact( IpRange.parse( "2610:00E0:0000:0000:0000:0000:0000:0000/32" ) ).second shouldBe "NET6-2610-E0-1"
            map.findExact( IpRange.parse( "209.136.123.000/24" ) ).second shouldBe "NET-209-136-123-0-1"

        }
    }

}