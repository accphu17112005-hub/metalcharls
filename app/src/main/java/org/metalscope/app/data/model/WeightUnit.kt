package org.metalscope.app.data.model

enum class WeightUnit(
    val code: String,
    val displayName: String,
    val symbol: String,
    val gramsFactor: Double
) {
    OZ(
        code = "OZ",
        displayName = "Troy Ounce",
        symbol = "oz",
        gramsFactor = 31.1034768
    ),
    GRAM(
        code = "G",
        displayName = "Gram",
        symbol = "g",
        gramsFactor = 1.0
    ),
    KG(
        code = "KG",
        displayName = "Kilogram",
        symbol = "kg",
        gramsFactor = 1000.0
    );

    fun convertPriceFromOz(pricePerOz: Double): Double {
        return when (this) {
            OZ -> pricePerOz
            GRAM -> pricePerOz / 31.1034768
            KG -> (pricePerOz / 31.1034768) * 1000.0
        }
    }

    companion object {
        const val TROY_OUNCE_TO_GRAMS = 31.1034768
        const val KG_TO_TROY_OUNCES = 32.1507465686

        fun fromCode(code: String): WeightUnit {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: OZ
        }
    }
}
