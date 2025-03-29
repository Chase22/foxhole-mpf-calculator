package de.chasenet.foxhole.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CostTest {
    @Test
    fun `0 cost for 0 crate queue returns 0`() {
        Cost().queueCost(0) shouldBe Cost()
    }

    @Test
    fun `3 crate queue returns reduced cost`() {
        costOfConstant(100).queueCost(3) shouldBe costOfConstant(240)
    }

    @Test
    fun `6th crate in a queue only gets 50 percent discounted`() {
        val cost100 = costOfConstant(100)

        cost100.queueCost(6) shouldBe cost100.queueCost(5) + costOfConstant(50)
    }

    private fun costOfConstant(cost: Int) = Cost(bmat = cost, rmat = cost, emat = cost, hemat = cost)
}
