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
class LoadIpMapTest : ShouldSpec () {

    var jasonLinesFile : File? = null
    var psvFile        : File? = null

    override fun beforeSpec(spec: Spec) {
        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        var resource = classLoader.getResource( "loadipmaptest.jsonlines" )
        var data = resource.readText()
        jasonLinesFile = createTempFile( "loadipmaptest", "jsonlines" )
        jasonLinesFile?.writeText( data )

        resource = classLoader.getResource( "loadipmaptest.psv" )
        data = resource.readText()
        psvFile = createTempFile( "loadipmaptest", "psv" )
        psvFile?.writeText( data )
    }

    override fun afterSpec(spec: Spec) {
        jasonLinesFile?.delete()
    }

    init {

        should( "parse loadipmaptest.jsonlines" ) {

            val map = loadIpMapFromJsonLines( jasonLinesFile?.absolutePath ?: "no jasonLinesFile" )
            map.findExact( IpRange.parse( "2610:00E0:0000:0000:0000:0000:0000:0000/32" ) ) shouldBe "NET6-2610-E0-1"
            map.findExact( IpRange.parse( "209.136.123.000/24" ) ) shouldBe "NET-209-136-123-0-1"

        }

        should( "parse loadipmaptest.psv" ) {

            val map = loadIpMapFromPsv( psvFile?.absolutePath ?: "no jasonLinesFile" )
            map.findExact( IpRange.parse( "2610:00E0:0000:0000:0000:0000:0000:0000/32" ) ) shouldBe "NET6-2610-E0-1"
            map.findExact( IpRange.parse( "209.136.123.000/24" ) ) shouldBe "NET-209-136-123-0-1"

        }
    }

}