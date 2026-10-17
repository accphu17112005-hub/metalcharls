package org.metalscope.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.util.CurrencyFormatter

class CurrencyFormatterTest {

    @Test
    fun testUsdFormatting() {
        val formatted = CurrencyFormatter.formatPrice(2658.40, Currency.USD)
        assertEquals("$2,658.40", formatted)
    }

    @Test
    fun testPriceWithUnit() {
        val formatted = CurrencyFormatter.formatPrice(2658.40, Currency.USD, WeightUnit.OZ)
        assertEquals("$2,658.40 / oz", formatted)
    }

    @Test
    fun testPositiveChangeFormatting() {
        val changeStr = CurrencyFormatter.formatChange(18.60, Currency.USD)
        assertEquals("+$18.60", changeStr)
    }

    @Test
    fun testNegativeChangeFormatting() {
        val changeStr = CurrencyFormatter.formatChange(-6.40, Currency.USD)
        assertEquals("-$6.40", changeStr)
    }

    @Test
    fun testPercentFormatting() {
        val positivePct = CurrencyFormatter.formatPercent(1.53)
        assertEquals("+1.53%", positivePct)

        val negativePct = CurrencyFormatter.formatPercent(-0.64)
        assertEquals("-0.64%", negativePct)
    }

    @Test
    fun testRatioFormatting() {
        val ratioStr = CurrencyFormatter.formatRatio(83.468)
        assertEquals("83.5x", ratioStr)
    }
}
