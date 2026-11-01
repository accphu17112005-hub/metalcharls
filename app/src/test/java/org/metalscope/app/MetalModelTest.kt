package org.metalscope.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.util.MarketSessionUtils

class MetalModelTest {

    @Test
    fun testMetalTypeSymbols() {
        assertEquals(MetalType.GOLD, MetalType.fromSymbol("XAU"))
        assertEquals(MetalType.SILVER, MetalType.fromSymbol("xag"))
        assertEquals(MetalType.PLATINUM, MetalType.fromSymbol("XPT"))
        assertEquals(MetalType.PALLADIUM, MetalType.fromSymbol("XPD"))
    }

    @Test
    fun testPriceQuoteCalculations() {
        val testQuote = PriceQuote(
            symbol = "XAU",
            name = "Gold",
            metalType = MetalType.GOLD,
            priceUsdPerOz = 3000.0,
            change24hUsd = 15.0,
            changePercent24h = 0.5,
            high24hUsd = 3010.0,
            low24hUsd = 2990.0,
            open24hUsd = 2985.0,
            bidUsd = 2999.0,
            askUsd = 3001.0,
            spreadUsd = 2.0,
            volume24hUsd = 10000.0,
            timestamp = System.currentTimeMillis(),
            source = "metalcharts.org",
            marketType = "Spot",
            isMarketOpen = true,
            marketSession = "European Session",
            isStale = false,
            isDemoData = false,
            sparkline = listOf(2990.0, 2995.0, 3000.0)
        )

        val usdPrice = testQuote.calculatePrice(Currency.USD, WeightUnit.OZ)
        val vndPrice = testQuote.calculatePrice(Currency.VND, WeightUnit.OZ)
        val expectedVnd = usdPrice * 25420.0
        assertEquals(expectedVnd, vndPrice, 1.0)

        val gramPrice = testQuote.calculatePrice(Currency.USD, WeightUnit.GRAM)
        val expectedGram = usdPrice / 31.1034768
        assertEquals(expectedGram, gramPrice, 0.01)
    }

    @Test
    fun testMarketSessionStatus() {
        val status = MarketSessionUtils.getMarketSessionStatus()
        assertNotNull(status.sessionTitle)
        assertNotNull(status.sessionDesc)
        assertTrue(status.sessionTitle.isNotEmpty())
    }
}
