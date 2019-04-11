package com.rcode3.etree_test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.ripe.ipresource.IpRange
import net.ripe.ipresource.etree.IpResourceIntervalStrategy
import java.io.File

data class NetLine (
    val version     : String = "4",
    val start_ip    : String = "::",
    val cidr_prefix : Int    = 32,
    val net_handle  : String = ""
)

/**
 * Reads a JSON lines file where the JSON line is of the form:
 *
 * ```
 * {"version":"6","start_ip":"2610:00E0:0000:0000:0000:0000:0000:0000","cidr_prefix":32,"net_handle":"NET6-2610-E0-1"}
 * ```
 */
fun loadIpMapFromJsonLines( fileName : String ) : IpMap {

    val map = IpMap( IpResourceIntervalStrategy.getInstance() )

    val mapper = jacksonObjectMapper()

    File( fileName ).forEachLine { s ->
        val line : NetLine = mapper.readValue( content = s )
        val net = IpRange.parse( "${line.start_ip}/${line.cidr_prefix}" )
        map.put( net, IpData( net, line.net_handle ) )
    }

    return map
}

/**
 * Reads a Pipe Separate Values (PSV) file where the line is of the form:
 *
 * ```
 * 6|2610:00E0:0000:0000:0000:0000:0000:0000|32|NET6-2610-E0-1
 * ```
 */
fun loadIpMapFromPsv( fileName : String ) : IpMap {

    val map = IpMap( IpResourceIntervalStrategy.getInstance() )

    File( fileName ).forEachLine { s ->
        val parts = s.split( '|' )
        val net = IpRange.parse( "${parts[1]}/${parts[2]}" )
        map.put( net, IpData( net, parts[3] ) )
    }

    return map
}