package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.ExpressionEvaluator
import com.example.engine.UnitConverterData
import com.example.model.AngleUnit
import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kalkulyator", appName)
  }

  @Test
  fun `test basic arithmetic`() {
    val res1 = ExpressionEvaluator.evaluate("15 + 25")
    assertTrue(res1 is ExpressionEvaluator.EvalResult.Success)
    assertEquals("40", (res1 as ExpressionEvaluator.EvalResult.Success).formatted)

    val res2 = ExpressionEvaluator.evaluate("100 - 35.5")
    assertTrue(res2 is ExpressionEvaluator.EvalResult.Success)
    assertEquals("64.5", (res2 as ExpressionEvaluator.EvalResult.Success).formatted)

    val res3 = ExpressionEvaluator.evaluate("12 × 5 ÷ 3")
    assertTrue(res3 is ExpressionEvaluator.EvalResult.Success)
    assertEquals("20", (res3 as ExpressionEvaluator.EvalResult.Success).formatted)
  }

  @Test
  fun `test scientific functions`() {
    val resSin = ExpressionEvaluator.evaluate("sin(30)", AngleUnit.DEG)
    assertTrue(resSin is ExpressionEvaluator.EvalResult.Success)
    assertEquals("0.5", (resSin as ExpressionEvaluator.EvalResult.Success).formatted)

    val resFact = ExpressionEvaluator.evaluate("5!")
    assertTrue(resFact is ExpressionEvaluator.EvalResult.Success)
    assertEquals("120", (resFact as ExpressionEvaluator.EvalResult.Success).formatted)

    val resSqrt = ExpressionEvaluator.evaluate("√(144)")
    assertTrue(resSqrt is ExpressionEvaluator.EvalResult.Success)
    assertEquals("12", (resSqrt as ExpressionEvaluator.EvalResult.Success).formatted)
  }

  @Test
  fun `test unit converter length and weight`() {
    val units = UnitConverterData.getUnitsForCategory(UnitCategory.LENGTH)
    val meters = units.first { it.symbol == "m" }
    val km = units.first { it.symbol == "km" }
    val converted = UnitConverterData.convert(2500.0, meters, km)
    assertEquals(2.5, converted, 0.001)
  }
}
