package com.rcode3.etree_test

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import net.ripe.ipresource.IpRange
import net.ripe.ipresource.Ipv4Address
import net.ripe.ipresource.etree.IpResourceIntervalStrategy
import net.ripe.ipresource.etree.NestedIntervalMap

/**
 * Demonstrates how to use Etree.
 */
class BasicsTest : ShouldSpec( {


    should( "create IP address" ) {

        val ip = Ipv4Address.parse( "10.0.0.0" )
        ip.start shouldBe Ipv4Address.parse( "10.0.0.0" )
        ip.end shouldBe Ipv4Address.parse( "10.0.0.0" )
    }

    should( "create an IP network" ) {

        val net = IpRange.parse( "10.0.0.0/24" )
        net.start shouldBe Ipv4Address.parse( "10.0.0.0" )
        net.end shouldBe Ipv4Address.parse( "10.0.0.255" )

    }

    should( "build a network of Ip Networks" ) {

        val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

        val net10  = IpRange.parse("10.0.0.0/24")
        val net20 = IpRange.parse("20.0.0.0/24")

        map.put( net10, "net10" )
        map.put( net20, "net20" )

        map.findExact( IpRange.parse( "10.0.0.0/24" ) ) shouldBe "net10"
        map.findExact( IpRange.parse( "20.0.0.0/24" ) ) shouldBe "net20"

        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/32" ) ) shouldBe "net10"
        map.findExactOrFirstLessSpecific( IpRange.parse( "20.0.0.0/32" ) ) shouldBe "net20"

    }

    should( "build nested networks" ) {

        val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

        val net10_16 = IpRange.parse("10.0.0.0/16")
        val net10_24 = IpRange.parse("10.0.0.0/24")

        map.put(net10_16, "net10_16" )
        map.put(net10_24, "net10_24" )

        map.findExact( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_16"
        map.findExact( IpRange.parse( "10.0.0.0/24" ) ) shouldBe "net10_24"

        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/32" ) ) shouldBe "net10_24"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/24" ) ) shouldBe "net10_24"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/19" ) ) shouldBe "net10_16"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_16"
    }

})