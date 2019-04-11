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
fun loadStructuredIntMapFromJsonLines( fileName: String ) : IpMap {

    val map = IpMap( IpResourceIntervalStrategy.getInstance() )

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
        val ipRange = IpRange.prefix( net, line.cidr_prefix )
        map.put( ipRange, IpData( ipRange, line.net_handle ) )
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
fun loadStructuredIntMapFromPsv( fileName: String ) : IpMap {

    val map = IpMap( IpResourceIntervalStrategy.getInstance() )

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
        val ipRange = IpRange.prefix( net, parts[ 2 ].toInt() )
        map.put( ipRange, IpData( ipRange, parts[ 3 ] ) )
    }

    return map
}

fun saveStructuredIntMapToPsv( fileName: String, map : IpMap ) {
    val v4s = map.findAllMoreSpecific( IpRange.parse( "0.0.0.0/0" ) )
    val v6s = map.findAllMoreSpecific( IpRange.parse( "::0/0" ) )
    val writer = File( fileName ).outputStream().writer()
    saveStructuredIntMapToPsv( writer, v4s )
    saveStructuredIntMapToPsv( writer, v6s )
    writer.close()
}

fun saveStructuredIntMapToPsv( writer: Writer, data : List<IpData>) {

    data.forEach { ipData ->
        val value = when( ipData.first.start.type ) {
            IpResourceType.IPv4 ->
                (ipData.first.start as Ipv4Address).longValue().toString()
            IpResourceType.IPv6 ->
                (ipData.first.start as Ipv6Address).value.toString()
            else ->
                throw RuntimeException( "illegal network type of ${ipData.first.start.type}" )
        }
        writer.write( "${ipData.first.type.code}|${value}|${ipData.first.prefixLength}|${ipData.second}\n")
    }
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