package com.rcode3.etree_test

import net.ripe.ipresource.IpRange
import net.ripe.ipresource.etree.NestedIntervalMap

typealias IpData = Pair<IpRange,String>

typealias IpMap = NestedIntervalMap<IpRange,IpData>
