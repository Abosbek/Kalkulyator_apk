package com.example.engine

import com.example.model.ConversionUnit
import com.example.model.UnitCategory

object UnitConverterData {

    fun getUnitsForCategory(category: UnitCategory): List<ConversionUnit> {
        return when (category) {
            UnitCategory.LENGTH -> listOf(
                ConversionUnit("Millimetr", "mm", 0.001),
                ConversionUnit("Santimetr", "sm", 0.01),
                ConversionUnit("Metr", "m", 1.0),
                ConversionUnit("Kilometr", "km", 1000.0),
                ConversionUnit("Dyuym (Inch)", "in", 0.0254),
                ConversionUnit("Fut (Foot)", "ft", 0.3048),
                ConversionUnit("Yard", "yd", 0.9144),
                ConversionUnit("Milya (Mile)", "mi", 1609.344)
            )

            UnitCategory.WEIGHT -> listOf(
                ConversionUnit("Milligramm", "mg", 0.000001),
                ConversionUnit("Gramm", "g", 0.001),
                ConversionUnit("Kilogramm", "kg", 1.0),
                ConversionUnit("Sentner", "sr", 100.0),
                ConversionUnit("Tonna", "t", 1000.0),
                ConversionUnit("Unsiya (Ounce)", "oz", 0.0283495),
                ConversionUnit("Funt (Pound)", "lb", 0.453592)
            )

            UnitCategory.TEMPERATURE -> listOf(
                ConversionUnit("Selsiy", "°C", isTemperature = true),
                ConversionUnit("Farengeyt", "°F", isTemperature = true),
                ConversionUnit("Kelvin", "K", isTemperature = true)
            )

            UnitCategory.DATA -> listOf(
                ConversionUnit("Bayt", "B", 1.0),
                ConversionUnit("Kilobayt", "KB", 1024.0),
                ConversionUnit("Megabayt", "MB", 1024.0 * 1024.0),
                ConversionUnit("Gigabayt", "GB", 1024.0 * 1024.0 * 1024.0),
                ConversionUnit("Terabayt", "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
            )

            UnitCategory.SPEED -> listOf(
                ConversionUnit("Metr / sekund", "m/s", 1.0),
                ConversionUnit("Kilometr / soat", "km/h", 1.0 / 3.6),
                ConversionUnit("Milya / soat", "mph", 0.44704),
                ConversionUnit("Tugun (Knot)", "kn", 0.514444)
            )

            UnitCategory.AREA -> listOf(
                ConversionUnit("Kvadrat santimetr", "sm²", 0.0001),
                ConversionUnit("Kvadrat metr", "m²", 1.0),
                ConversionUnit("Sotix (Ar)", "sotix", 100.0),
                ConversionUnit("Gektar", "ga", 10000.0),
                ConversionUnit("Kvadrat kilometr", "km²", 1000000.0),
                ConversionUnit("Akr", "ac", 4046.86)
            )

            UnitCategory.TIME -> listOf(
                ConversionUnit("Millisekund", "ms", 0.001),
                ConversionUnit("Sekund", "s", 1.0),
                ConversionUnit("Minut", "min", 60.0),
                ConversionUnit("Soat", "soat", 3600.0),
                ConversionUnit("Kun", "kun", 86400.0),
                ConversionUnit("Hafta", "hafta", 604800.0),
                ConversionUnit("Oy (30 kun)", "oy", 2592000.0),
                ConversionUnit("Yil (365 kun)", "yil", 31536000.0)
            )
        }
    }

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
        if (from == to) return value

        if (from.isTemperature || to.isTemperature) {
            // Convert 'from' to Celsius first
            val celsius = when (from.symbol) {
                "°C" -> value
                "°F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }

            // Convert Celsius to 'to'
            return when (to.symbol) {
                "°C" -> celsius
                "°F" -> (celsius * 9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        // Standard linear conversion via base unit
        val inBase = value * from.toBaseRatio
        return inBase / to.toBaseRatio
    }
}
