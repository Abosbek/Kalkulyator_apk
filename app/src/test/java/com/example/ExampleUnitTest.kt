package com.example

import com.example.engine.ExpressionEvaluator
import com.example.engine.UnitConverterData
import com.example.model.AngleUnit
import com.example.model.UnitCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testOperatorPrecedence() {
        val res = ExpressionEvaluator.evaluate("2 + 3 × 4")
        assertTrue(res is ExpressionEvaluator.EvalResult.Success)
        assertEquals("14", (res as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun testParenthesesAndPowers() {
        val res = ExpressionEvaluator.evaluate("(2 + 3) ^ 2")
        assertTrue(res is ExpressionEvaluator.EvalResult.Success)
        assertEquals("25", (res as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun testPercentages() {
        val res = ExpressionEvaluator.evaluate("200 × 15%")
        assertTrue(res is ExpressionEvaluator.EvalResult.Success)
        assertEquals("30", (res as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun testTemperatureConversion() {
        val units = UnitConverterData.getUnitsForCategory(UnitCategory.TEMPERATURE)
        val c = units.first { it.symbol == "°C" }
        val f = units.first { it.symbol == "°F" }
        val converted = UnitConverterData.convert(100.0, c, f)
        assertEquals(212.0, converted, 0.001)
    }
}
