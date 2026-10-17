package org.metalscope.app.data.model

enum class Currency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val rateFromUsd: Double,
    val standardDecimals: Int
) {
    USD(
        code = "USD",
        symbol = "$",
        displayName = "US Dollar",
        rateFromUsd = 1.0,
        standardDecimals = 2
    ),
    EUR(
        code = "EUR",
        symbol = "€",
        displayName = "Euro",
        rateFromUsd = 0.925,
        standardDecimals = 2
    ),
    GBP(
        code = "GBP",
        symbol = "£",
        displayName = "British Pound",
        rateFromUsd = 0.788,
        standardDecimals = 2
    ),
    JPY(
        code = "JPY",
        symbol = "¥",
        displayName = "Japanese Yen",
        rateFromUsd = 153.2,
        standardDecimals = 0
    ),
    AUD(
        code = "AUD",
        symbol = "A$",
        displayName = "Australian Dollar",
        rateFromUsd = 1.545,
        standardDecimals = 2
    ),
    CAD(
        code = "CAD",
        symbol = "C$",
        displayName = "Canadian Dollar",
        rateFromUsd = 1.385,
        standardDecimals = 2
    ),
    VND(
        code = "VND",
        symbol = "₫",
        displayName = "Vietnamese Dong",
        rateFromUsd = 25420.0,
        standardDecimals = 0
    );

    fun convertFromUsd(amountUsd: Double): Double {
        return amountUsd * rateFromUsd
    }

    companion object {
        fun fromCode(code: String): Currency {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: USD
        }
    }
}
