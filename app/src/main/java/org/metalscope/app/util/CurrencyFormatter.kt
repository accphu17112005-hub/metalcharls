package org.metalscope.app.util

import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.WeightUnit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    fun formatPrice(
        amount: Double,
        currency: Currency,
        unit: WeightUnit? = null,
        decimalOverride: Int? = null
    ): String {
        val decimals = decimalOverride ?: when {
            currency == Currency.VND || currency == Currency.JPY -> 0
            amount < 1.0 -> 4
            else -> currency.standardDecimals
        }

        val pattern = buildString {
            append("#,##0")
            if (decimals > 0) {
                append(".")
                repeat(decimals) { append("0") }
            }
        }

        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat(pattern, symbols)
        val formattedNumber = formatter.format(amount)

        val priceWithSymbol = when (currency) {
            Currency.USD, Currency.GBP, Currency.JPY -> "${currency.symbol}$formattedNumber"
            Currency.EUR -> "$formattedNumber ${currency.symbol}"
            Currency.AUD, Currency.CAD -> "${currency.symbol}$formattedNumber"
            Currency.VND -> "$formattedNumber ${currency.symbol}"
        }

        return if (unit != null) {
            "$priceWithSymbol / ${unit.symbol}"
        } else {
            priceWithSymbol
        }
    }

    fun formatChange(
        amount: Double,
        currency: Currency,
        includeSign: Boolean = true
    ): String {
        val decimals = if (currency == Currency.VND || currency == Currency.JPY) 0 else 2
        val pattern = buildString {
            append("#,##0")
            if (decimals > 0) {
                append(".")
                repeat(decimals) { append("0") }
            }
        }
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat(pattern, symbols)
        val absVal = kotlin.math.abs(amount)
        val formattedNumber = formatter.format(absVal)

        val sign = when {
            amount > 0 && includeSign -> "+"
            amount < 0 -> "-"
            else -> ""
        }

        return when (currency) {
            Currency.USD, Currency.GBP, Currency.JPY, Currency.AUD, Currency.CAD ->
                "$sign${currency.symbol}$formattedNumber"
            Currency.EUR, Currency.VND ->
                "$sign$formattedNumber ${currency.symbol}"
        }
    }

    fun formatPercent(percent: Double, includeSign: Boolean = true): String {
        val sign = when {
            percent > 0 && includeSign -> "+"
            percent < 0 -> "-"
            else -> ""
        }
        val absVal = kotlin.math.abs(percent)
        return String.format(Locale.US, "%s%.2f%%", sign, absVal)
    }

    fun formatRatio(ratio: Double): String {
        return String.format(Locale.US, "%.1fx", ratio)
    }
}
