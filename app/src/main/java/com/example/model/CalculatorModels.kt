package com.example.model

enum class CalculatorMode {
    STANDARD,
    SCIENTIFIC,
    CONVERTER
}

enum class AngleUnit(val label: String) {
    DEG("DEG"),
    RAD("RAD")
}

data class HistoryItem(
    val id: Long = System.currentTimeMillis(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class UnitCategory(val titleUz: String, val iconName: String) {
    LENGTH("Uzunlik", "straighten"),
    WEIGHT("Og'irlik", "scale"),
    TEMPERATURE("Harorat", "thermostat"),
    DATA("Ma'lumot hajmi", "memory"),
    SPEED("Tezlik", "speed"),
    AREA("Maydon", "square_foot"),
    TIME("Vaqt", "schedule")
}

data class ConversionUnit(
    val nameUz: String,
    val symbol: String,
    val toBaseRatio: Double = 1.0, // base unit multiplier
    val isTemperature: Boolean = false
)
