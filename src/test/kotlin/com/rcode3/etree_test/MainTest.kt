package com.rcode3.etree_test

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class MainTest : ShouldSpec({

    should( "run a test" ) {
        val v = 2
        v shouldBe 2
    }

})