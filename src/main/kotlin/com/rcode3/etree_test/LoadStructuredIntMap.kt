package com.rcode3.etree_test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.ripe.ipresource.*
import net.ripe.ipresource.etree.IpResourceIntervalStrategy
import net.ripe.ipresource.etree.NestedIntervalMap
import java.io.File
import java.io.Reader
import java.io.Writer

data class IntLine (
        val type: String,
        val value: String,
        val cidr_prefix : Int,
        val net_handle: String
)

/**
 * Reades a JSON lines file where the JSON line is the form:
 *
 * ```
 * {"type":"ipResourceType.code","value":"xxxx","cidr_prefix":32,"net_handle":"NET6-2610-E0-1"}
 * ```
 */
fun loadStructuredIntMapFromJsonLines( fileName: String ) : NestedIntervalMap<IpRange,String> {

    val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

    val mapper = jacksonObjectMapper()

    File( fileName ).forEachLine { s ->
        val line : IntLine = mapper.readValue( content = s )
        val ipResourceType = IpResourceType.valueOf( line.type )
        val net = when( ipResourceType ) {
            IpResourceType.IPv4 ->
                Ipv4Address( line.value.toLong() )
            IpResourceType.IPv6 ->
                Ipv6Address( line.value.toBigInteger() )
            else ->
                throw RuntimeException( "illegal network type" )
        }
        map.put( IpRange.prefix( net, line.cidr_prefix ), line.net_handle )
    }

    return map
}

/**
 * Reads a Pipe Separated Values (PSV) file where the line is of the form:
 *
 * ```
 * ipResourceType.code|xxxxx|32|NET6...
 * ```
 */
fun loadStructuredIntMapFromPsv( fileName: String ) : NestedIntervalMap<IpRange,String> {

    val map = NestedIntervalMap<IpRange,String>( IpResourceIntervalStrategy.getInstance() )

    File( fileName ).forEachLine { s ->
        val parts = s.split( '|' )
        val ipResourceType = IpResourceType.valueOf( parts[ 0 ] )
        val net = when( ipResourceType ) {
            IpResourceType.IPv4 ->
                Ipv4Address( parts[ 1 ].toLong() )
            IpResourceType.IPv6 ->
                Ipv6Address( parts[ 1 ].toBigInteger() )
            else ->
                throw RuntimeException( "illegal network type" )
        }
        map.put( IpRange.prefix( net, parts[ 2 ].toInt() ), parts[ 3 ] )
    }

    return map
}

fun saveStructuredIntMaptoPsv( fileName: String, map : NestedIntervalMap<IpRange,String> ) {
    val v4s = map.findAllMoreSpecific( IpRange.parse( "0.0.0.0/0" ) )
    val v6s = map.findAllMoreSpecific( IpRange.parse( "::0/0" ) )
}


fun convertIpToStructuredIntJsonLines(input: Reader, output: Writer) {
    val mapper = jacksonObjectMapper()

    input.forEachLine { inLine ->
        val line : NetLine = mapper.readValue( inLine )
        val ip = IpResource.parse( line.start_ip )
        val value = when( ip.type ) {
            IpResourceType.IPv6 ->
                (ip as Ipv6Address).value.toString()
            IpResourceType.IPv4 ->
                (ip as Ipv4Address).longValue().toString()
            else ->
                throw RuntimeException( "illegal network type" )
        }
        val intLine = IntLine( type = ip.type.code,
                value = value,
                cidr_prefix = line.cidr_prefix,
                net_handle = line.net_handle )
        output.write( "${mapper.writeValueAsString(intLine)}\n" )
    }
}

fun convertIpToStructuredIntPsv( input: Reader, output: Writer ) {
    input.forEachLine { line ->
        val parts = line.split( '|' )
        val net = IpAddress.parse( parts[ 1 ] )
        val value = when( net.type ) {
            IpResourceType.IPv4 ->
                (net as Ipv4Address).longValue().toString()
            IpResourceType.IPv6 ->
                (net as Ipv6Address).value.toString()
            else ->
                throw RuntimeException( "illegal network type" )
        }
        output.write( "${net.type.code}|${value}|${parts[2]}|${parts[3]}\n")
    }
}