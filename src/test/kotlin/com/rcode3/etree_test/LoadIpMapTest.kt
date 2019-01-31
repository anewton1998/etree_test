package com.rcode3.etree_test

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import net.ripe.ipresource.IpRange
import java.io.File.createTempFile

/**
 * Does a basic test of loading a map for a JSON Lines file.
 */
class LoadIpMapTest : ShouldSpec ( {

    should( "parse loadipmaptest.jsonlines" ) {

        //copy the resource to a temp location
        val classLoader = javaClass.classLoader
        val resource = classLoader.getResource( "loadipmaptest.jsonlines" )
        val data = resource.readText()
        val file = createTempFile( "loadipmaptest", "jsonlines" )
        file.writeText( data )

        val map = loadIpMapFromJsonLines( file.absolutePath )
        map.findExact( IpRange.parse( "2610:00E0:0000:0000:0000:0000:0000:0000/32" ) ) shouldBe "NET6-2610-E0-1"
        map.findExact( IpRange.parse( "209.136.123.000/24" ) ) shouldBe "NET-209-136-123-0-1"
    }

})