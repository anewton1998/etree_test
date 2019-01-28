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

    }

})