package org.metalscope.app

import org.junit.Assert.assertEquals
import org.junit.Test
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.util.UnitConverter

class UnitConverterTest {

    @Test
    fun testTroyOunceToGramsConversion() {
        val gramsInOz = UnitConverter.convertWeight(1.0, WeightUnit.OZ, WeightUnit.GRAM)
        assertEquals(31.1034768, gramsInOz, 0.0001)
    }

    @Test
    fun testKilogramToTroyOuncesConversion() {
        val ozInKg = UnitConverter.convertWeight(1.0, WeightUnit.KG, WeightUnit.OZ)
        assertEquals(32.1507466, ozInKg, 0.001)
    }

    @Test
    fun testGoldPricePerGramCalculation() {
        val goldPricePerOz = 2658.40
        val pricePerGram = WeightUnit.GRAM.convertPriceFromOz(goldPricePerOz)
        val expected = goldPricePerOz / 31.1034768
        assertEquals(expected, pricePerGram, 0.01)
    }

    @Test
    fun testGoldPricePerKgCalculation() {
        val goldPricePerOz = 2658.40
        val pricePerKg = WeightUnit.KG.convertPriceFromOz(goldPricePerOz)
        val expected = (goldPricePerOz / 31.1034768) * 1000.0
        assertEquals(expected, pricePerKg, 0.01)
    }

    @Test
    fun testIdentityConversion() {
        val price = 1500.0
        val converted = UnitConverter.convertPrice(price, WeightUnit.OZ, WeightUnit.OZ)
        assertEquals(price, converted, 0.0001)
    }
}
