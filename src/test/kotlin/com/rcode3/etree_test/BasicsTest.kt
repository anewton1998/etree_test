package com.rcode3.etree_test

import io.kotlintest.matchers.collections.shouldContain
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

        // define networks
        val net10  = IpRange.parse("10.0.0.0/24")
        val net20 = IpRange.parse("20.0.0.0/24")

        // place them in the map
        map.put( net10, "net10" )
        map.put( net20, "net20" )

        // find the networks
        map.findExact( IpRange.parse( "10.0.0.0/24" ) ) shouldBe "net10"
        map.findExact( IpRange.parse( "20.0.0.0/24" ) ) shouldBe "net20"

        // find the networks given an IP address (/32)
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/32" ) ) shouldBe "net10"
        map.findExactOrFirstLessSpecific( IpRange.parse( "20.0.0.0/32" ) ) shouldBe "net20"

    }

    should( "build nested networks" ) {

        val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

        // define networks - one inside the other
        val net10_16 = IpRange.parse("10.0.0.0/16")
        val net10_24 = IpRange.parse("10.0.0.0/24")

        // place them in the map
        map.put(net10_16, "net10_16" )
        map.put(net10_24, "net10_24" )

        // find the networks
        map.findExact( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_16"
        map.findExact( IpRange.parse( "10.0.0.0/24" ) ) shouldBe "net10_24"

        // find the networks given subnets
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/32" ) ) shouldBe "net10_24"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/24" ) ) shouldBe "net10_24"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/19" ) ) shouldBe "net10_16"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_16"
    }

    should( "build overlapped / nested networks" ) {

        val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

        //define the networks
        val net10_0_8 = IpRange.parse( "10.0.0.0/8" )   // 10.0.0.0 to 10.255.255.255
        val net10_0_16_a= IpRange.parse( "10.0.0.0/16" )  // 10.0.0.0 to 10.0.255.255
        val net10_0_16_b= IpRange.parse( "10.0.0.0/16" )  // 10.0.0.0 to 10.0.255.255

        // place networks in the map
        map.put( net10_0_8, "net10_0_8" )
        map.put( net10_0_16_a, "net10_0_16_a" )
        map.put( net10_0_16_b, "net10_0_16_b" )

        //find the networks
        map.findExact( IpRange.parse( "10.0.0.0/8" ) ) shouldBe "net10_0_8"
        map.findExact( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_0_16_b" //the last one inserted

        // find the networks given subnets
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/8" ) ) shouldBe "net10_0_8"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_0_16_b"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/19" ) ) shouldBe "net10_0_16_b"

        // look at all the subnets
        map.findAllLessSpecific( IpRange.parse( "10.0.0.0/20" ) ).size shouldBe 2
        map.findAllLessSpecific( IpRange.parse( "10.0.0.0/20" ) ) shouldContain "net10_0_8"
        map.findAllLessSpecific( IpRange.parse( "10.0.0.0/20" ) ) shouldContain "net10_0_16_b"
    }

})