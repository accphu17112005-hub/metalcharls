package org.metalscope.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.data.source.DemoDataProvider

class MetalModelTest {

    @Test
    fun testMetalTypeSymbols() {
        assertEquals(MetalType.GOLD, MetalType.fromSymbol("XAU"))
        assertEquals(MetalType.SILVER, MetalType.fromSymbol("xag"))
        assertEquals(MetalType.PLATINUM, MetalType.fromSymbol("XPT"))
        assertEquals(MetalType.PALLADIUM, MetalType.fromSymbol("XPD"))
    }

    @Test
    fun testDemoDataProviderQuotes() {
        val quotes = DemoDataProvider.getQuotes()
        assertEquals(4, quotes.size)

        val goldQuote = quotes.first { it.symbol == "XAU" }
        assertNotNull(goldQuote)
        assertEquals("Gold", goldQuote.name)
        assert(goldQuote.priceUsdPerOz > 2000.0)
        assert(goldQuote.sparkline.isNotEmpty())
    }

    @Test
    fun testCurrencyConversions() {
        val quotes = DemoDataProvider.getQuotes()
        val goldQuote = quotes.first { it.symbol == "XAU" }

        val usdPrice = goldQuote.calculatePrice(Currency.USD, WeightUnit.OZ)
        val vndPrice = goldQuote.calculatePrice(Currency.VND, WeightUnit.OZ)
        val expectedVnd = usdPrice * 25420.0
        assertEquals(expectedVnd, vndPrice, 1.0)
    }

    @Test
    fun testHistoricalDataGeneration() {
        val records1M = DemoDataProvider.getHistoricalData(MetalType.GOLD, TimeRange.ONE_MONTH)
        assertEquals(TimeRange.ONE_MONTH.pointCount, records1M.size)
        assert(records1M.all { it.closeUsdPerOz > 0 })
    }

    @Test
    fun testMarketInsightsCalculation() {
        val insight = DemoDataProvider.getMarketInsights()
        assert(insight.goldSilverRatio > 50.0 && insight.goldSilverRatio < 120.0)
        assertNotNull(insight.weeklyBestPerformer)
    }
}
