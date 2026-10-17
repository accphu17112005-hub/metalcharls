package org.metalscope.app.util

import org.metalscope.app.data.model.WeightUnit

object UnitConverter {

    const val TROY_OUNCE_IN_GRAMS = 31.1034768
    const val KG_IN_TROY_OUNCES = 32.1507465686

    fun convertPrice(pricePerUnit: Double, fromUnit: WeightUnit, toUnit: WeightUnit): Double {
        if (fromUnit == toUnit) return pricePerUnit
        // Convert to USD/oz first as canonical baseline
        val pricePerOz = when (fromUnit) {
            WeightUnit.OZ -> pricePerUnit
            WeightUnit.GRAM -> pricePerUnit * TROY_OUNCE_IN_GRAMS
            WeightUnit.KG -> (pricePerUnit / 1000.0) * TROY_OUNCE_IN_GRAMS
        }
        return toUnit.convertPriceFromOz(pricePerOz)
    }

    fun convertWeight(amount: Double, fromUnit: WeightUnit, toUnit: WeightUnit): Double {
        if (fromUnit == toUnit) return amount
        val grams = when (fromUnit) {
            WeightUnit.OZ -> amount * TROY_OUNCE_IN_GRAMS
            WeightUnit.GRAM -> amount
            WeightUnit.KG -> amount * 1000.0
        }
        return when (toUnit) {
            WeightUnit.OZ -> grams / TROY_OUNCE_IN_GRAMS
            WeightUnit.GRAM -> grams
            WeightUnit.KG -> grams / 1000.0
        }
    }
}
