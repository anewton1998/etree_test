package com.rcode3.etree_test

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.ShouldSpec
import net.ripe.ipresource.IpAddress
import net.ripe.ipresource.IpRange
import net.ripe.ipresource.Ipv4Address
import net.ripe.ipresource.Ipv6Address
import net.ripe.ipresource.etree.IpResourceIntervalStrategy
import net.ripe.ipresource.etree.NestedIntervalMap
import java.lang.IllegalArgumentException

/**
 * Demonstrates how to use Etree.
 */
class BasicsTest : ShouldSpec( {


    should( "create IP address" ) {

        val ip = Ipv4Address.parse( "10.0.0.0" )

        ip.start shouldBe Ipv4Address.parse( "10.0.0.0" )
        ip.end   shouldBe Ipv4Address.parse( "10.0.0.0" )
    }

    /**
     * Zero-padded IPv4 addresses are really not a good idea as some parsers
     * interpret the leading zero as an octal signifier. Fortunately, this
     * parser does not.
     *
     * Zero-padded IPv4 addresses are often used for lexical sorting.
     */
    should( "create IPv4 addresses from zero padded" ) {

        val ip = Ipv4Address.parse( "010.000.000.000" )

        ip.start shouldBe Ipv4Address.parse( "10.0.0.0" )
        ip.end   shouldBe Ipv4Address.parse( "10.0.0.0" )

    }

    should( "create an IP network from CIDR notation" ) {

        val net = IpRange.parse( "10.0.0.0/24" )

        net.start shouldBe Ipv4Address.parse( "10.0.0.0" )
        net.end   shouldBe Ipv4Address.parse( "10.0.0.255" )

    }

    should( "create an IP network from a range" ) {

        val net = IpRange.parse( "10.0.0.0-10.255.255.255" )

        net.start shouldBe Ipv4Address.parse( "10.0.0.0" )
        net.end   shouldBe Ipv4Address.parse( "10.255.255.255" )

    }

    should( "parse IPv6 addresses" ) {

        Ipv6Address.parse( "2001:db8::" )
        Ipv6Address.parse( "::1" )
        Ipv6Address.parse( "::" )
        Ipv6Address.parse( "::ffff:192.0.2.128")

    }

    should( "parse IPv6 network in CIDR notation" ) {

        val net = IpRange.parse( "2001:db8::/64" )

        net.start shouldBe Ipv6Address.parse( "2001:db8::" )
        net.end   shouldBe Ipv6Address.parse( "2001:db8::ffff:ffff:ffff:ffff" )
    }

    should( "parse an IPv6 network as a range" ) {

        val net = IpRange.parse( "2001:db8::-2001:db8::ffff:ffff:ffff:ffff")

        net.start shouldBe Ipv6Address.parse( "2001:db8::" )
        net.end   shouldBe Ipv6Address.parse( "2001:db8::ffff:ffff:ffff:ffff" )

    }

    should( "not parse single IPv4s as an IPRange" ) {
        shouldThrow<IllegalArgumentException> {
            IpRange.parse( "10.0.0.0" )
        }
        shouldThrow<IllegalArgumentException> {
            IpRange.parse( "10.255.255.255" )
        }
    }

    should( "not parse single IPv6s as an IPRange" ) {
        shouldThrow<IllegalArgumentException> {
            IpRange.parse( "2001:db8::")
        }
        shouldThrow<IllegalArgumentException> {
            IpRange.parse( "2001:db8::ffff:ffff:ffff:ffff")
        }
    }

    should( "put construct IPRange from IPAddress" ) {
        var ip = IpAddress.parse( "10.0.0.0" )
        var range = IpRange.range( ip, ip )

        range.start shouldBe ip
        range.end   shouldBe ip

        ip = IpAddress.parse( "10.255.255.255" )
        range = IpRange.range( ip, ip )

        range.start shouldBe ip
        range.end   shouldBe ip

        ip = IpAddress.parse( "2001:db8::")
        range = IpRange.range( ip, ip )

        range.start shouldBe ip
        range.end   shouldBe ip

        ip= IpAddress.parse( "2001:db8::ffff:ffff:ffff:ffff")
        range = IpRange.range( ip, ip )

        range.start shouldBe ip
        range.end   shouldBe ip
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
        map.findExact( IpRange.parse( "10.0.0.0/8" ) )  shouldBe "net10_0_8"
        map.findExact( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_0_16_b" //the last one inserted

        // find the networks given subnets
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/8" ) )  shouldBe "net10_0_8"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/16" ) ) shouldBe "net10_0_16_b"
        map.findExactOrFirstLessSpecific( IpRange.parse( "10.0.0.0/19" ) ) shouldBe "net10_0_16_b"

        // look at all the subnets
        map.findAllLessSpecific( IpRange.parse( "10.0.0.0/20" ) ).size shouldBe      2
        map.findAllLessSpecific( IpRange.parse( "10.0.0.0/20" ) )      shouldContain "net10_0_8"
        map.findAllLessSpecific( IpRange.parse( "10.0.0.0/20" ) )      shouldContain "net10_0_16_b"
    }

    should( "combine IPv4 and IPv6 ranges into a single map" ) {

        val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

        // define networks - one inside the other
        val net10_16 = IpRange.parse("10.0.0.0/16")
        val net10_24 = IpRange.parse("10.0.0.0/24")
        val net2001_db8_48 = IpRange.parse( "2001:db8::/48")
        val net2001_db8_64 = IpRange.parse( "2001:db8::/64")

        // place them in the map
        map.put(net10_16, "net10_16" )
        map.put(net10_24, "net10_24" )
        map.put(net2001_db8_48, "net2001_db8_48" )
        map.put(net2001_db8_64, "net2001_db8_64" )

        // find the networks
        map.findExact( IpRange.parse( "10.0.0.0/16" ) )   shouldBe "net10_16"
        map.findExact( IpRange.parse( "10.0.0.0/24" ) )   shouldBe "net10_24"
        map.findExact( IpRange.parse( "2001:db8::/48" ) ) shouldBe "net2001_db8_48"
        map.findExact( IpRange.parse( "2001:db8::/64" ) ) shouldBe "net2001_db8_64"
    }

    should( "get all nets in the map" ) {

        val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

        // define networks - one inside the other
        val net10_16 = IpRange.parse("10.0.0.0/16")
        val net10_24 = IpRange.parse("10.0.0.0/24")
        val net20_16 = IpRange.parse("20.0.0.0/16")
        val net20_24 = IpRange.parse("20.0.0.0/24")

        // place them in the map
        map.put(net10_16, "net10_16" )
        map.put(net10_24, "net10_24" )
        map.put(net20_16, "net20_16" )
        map.put(net20_24, "net20_24" )

        val allNets = map.findAllMoreSpecific( IpRange.parse("0.0.0.0/0" ) )
        allNets.size shouldBe      4
        allNets      shouldContain "net10_16"
        allNets      shouldContain "net10_24"
        allNets      shouldContain "net20_16"
        allNets      shouldContain "net20_24"
    }

})