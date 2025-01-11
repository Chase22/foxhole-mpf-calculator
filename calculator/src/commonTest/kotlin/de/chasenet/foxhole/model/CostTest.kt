package de.chasenet.foxhole.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CostTest {
    @Test
    fun `0 cost for 0 crate queue returns 0`() {
        assertEquals(
            Cost(),
            Cost().queueCost(0),
        )
    }

    @Test
    fun `3 crate queue returns reduced cost`() {
        assertEquals(
            costOfConstant(240),
            costOfConstant(100).queueCost(3),
        )
    }

    @Test
    fun `6th crate in a queue only gets 50% discounted`() {
        assertEquals(
            costOfConstant(100).queueCost(5) + costOfConstant(50),
            costOfConstant(100).queueCost(6),
        )
    }

    private fun costOfConstant(cost: Int) = Cost(bmat = cost, rmat = cost, emat = cost, hemat = cost)
}
