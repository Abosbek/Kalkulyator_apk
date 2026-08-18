package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.engine.ExpressionEvaluator
import com.example.engine.UnitConverterData
import com.example.model.AngleUnit
import com.example.model.CalculatorMode
import com.example.model.ConversionUnit
import com.example.model.HistoryItem
import com.example.model.UnitCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val expression: String = "",
    val liveResult: String = "",
    val mainResult: String = "0",
    val isCalculated: Boolean = false,
    val errorMessage: String? = null,
    val mode: CalculatorMode = CalculatorMode.STANDARD,
    val angleUnit: AngleUnit = AngleUnit.DEG,
    val isInverseTrig: Boolean = false,
    val isDarkTheme: Boolean = true,
    val isOledBlack: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val history: List<HistoryItem> = emptyList(),
    // Converter state
    val converterCategory: UnitCategory = UnitCategory.LENGTH,
    val converterFromUnit: ConversionUnit = UnitConverterData.getUnitsForCategory(UnitCategory.LENGTH)[2], // Metr
    val converterToUnit: ConversionUnit = UnitConverterData.getUnitsForCategory(UnitCategory.LENGTH)[3],   // Kilometr
    val converterInputValue: String = "1",
    val converterOutputValue: String = "0.001"
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        updateConverterOutput()
    }

    // --- Calculator Operations ---

    fun onDigit(digit: String) {
        _uiState.update { current ->
            val newExpr = if (current.isCalculated) {
                digit
            } else {
                current.expression + digit
            }
            val liveRes = calculateLiveResult(newExpr, current.angleUnit)
            current.copy(
                expression = newExpr,
                mainResult = if (current.isCalculated) "0" else current.mainResult,
                liveResult = liveRes,
                isCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onDecimal() {
        _uiState.update { current ->
            val expr = if (current.isCalculated) "0" else current.expression
            // Check if the current number already contains a decimal dot
            val lastNumber = expr.split(Regex("[+\\-×÷%^()]")).lastOrNull() ?: ""
            if (!lastNumber.contains(".")) {
                val updated = if (expr.isEmpty() || expr.endsWith("+") || expr.endsWith("-") ||
                    expr.endsWith("×") || expr.endsWith("÷") || expr.endsWith("(") || expr.endsWith("^")
                ) {
                    expr + "0."
                } else {
                    expr + "."
                }
                val liveRes = calculateLiveResult(updated, current.angleUnit)
                current.copy(
                    expression = updated,
                    liveResult = liveRes,
                    isCalculated = false,
                    errorMessage = null
                )
            } else {
                current
            }
        }
    }

    fun onOperator(op: String) {
        _uiState.update { current ->
            val baseExpr = if (current.isCalculated) {
                // If just calculated, use the result as the starting operand
                current.mainResult
            } else {
                current.expression
            }

            if (baseExpr.isEmpty()) {
                if (op == "-") {
                    return@update current.copy(
                        expression = "-",
                        isCalculated = false,
                        errorMessage = null
                    )
                }
                return@update current
            }

            // If last char is operator, replace it
            val lastChar = baseExpr.last()
            val newExpr = if (lastChar in listOf('+', '-', '×', '÷', '^')) {
                baseExpr.dropLast(1) + op
            } else {
                baseExpr + op
            }

            current.copy(
                expression = newExpr,
                isCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onFunction(func: String) {
        _uiState.update { current ->
            val baseExpr = if (current.isCalculated) "" else current.expression
            val addition = when (func) {
                "sin" -> if (current.isInverseTrig) "asin(" else "sin("
                "cos" -> if (current.isInverseTrig) "acos(" else "cos("
                "tan" -> if (current.isInverseTrig) "atan(" else "tan("
                "ln" -> "ln("
                "log" -> "log("
                "sqrt" -> "√("
                "cbrt" -> "∛("
                "sqr" -> "^2"
                "cube" -> "^3"
                "pow" -> "^"
                "fact" -> "!"
                "pi" -> "π"
                "e" -> "e"
                "inv" -> "1/("
                "abs" -> "abs("
                else -> func
            }

            val newExpr = baseExpr + addition
            val live = calculateLiveResult(newExpr, current.angleUnit)
            current.copy(
                expression = newExpr,
                liveResult = live,
                isCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onParenthesis() {
        _uiState.update { current ->
            val expr = if (current.isCalculated) "" else current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val lastChar = expr.lastOrNull()
            val newExpr = if (lastChar == null || lastChar in listOf('+', '-', '×', '÷', '^', '(')) {
                expr + "("
            } else if (openCount > closeCount && (lastChar.isDigit() || lastChar in listOf(')', 'π', 'e', '!'))) {
                expr + ")"
            } else {
                expr + "×("
            }

            val live = calculateLiveResult(newExpr, current.angleUnit)
            current.copy(
                expression = newExpr,
                liveResult = live,
                isCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onToggleSign() {
        _uiState.update { current ->
            if (current.isCalculated) {
                val num = current.mainResult.toDoubleOrNull()
                if (num != null) {
                    val toggled = ExpressionEvaluator.formatNumber(-num)
                    return@update current.copy(
                        mainResult = toggled,
                        expression = toggled,
                        isCalculated = true
                    )
                }
            }

            val expr = current.expression
            if (expr.isEmpty()) {
                return@update current.copy(expression = "-")
            }

            // Find last number token and negate it
            val lastOpIdx = expr.lastIndexOfAny(charArrayOf('+', '-', '×', '÷', '('))
            val newExpr = if (lastOpIdx == -1) {
                if (expr.startsWith("-")) expr.drop(1) else "-$expr"
            } else {
                val prefix = expr.substring(0, lastOpIdx + 1)
                val suffix = expr.substring(lastOpIdx + 1)
                if (suffix.startsWith("-")) {
                    prefix + suffix.drop(1)
                } else {
                    prefix + "(-" + suffix
                }
            }

            val live = calculateLiveResult(newExpr, current.angleUnit)
            current.copy(
                expression = newExpr,
                liveResult = live,
                isCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onPercentage() {
        _uiState.update { current ->
            val expr = if (current.isCalculated) current.mainResult else current.expression
            if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
                val newExpr = "$expr%"
                val live = calculateLiveResult(newExpr, current.angleUnit)
                current.copy(
                    expression = newExpr,
                    liveResult = live,
                    isCalculated = false,
                    errorMessage = null
                )
            } else {
                current
            }
        }
    }

    fun onBackspace() {
        _uiState.update { current ->
            if (current.isCalculated) {
                return@update current.copy(
                    expression = "",
                    mainResult = "0",
                    liveResult = "",
                    isCalculated = false
                )
            }

            val expr = current.expression
            if (expr.isEmpty()) return@update current

            // Multi-char token removal: e.g. "sin(", "asin(", "log(", "cbrt(", etc.
            val tokenList = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "ln(", "sqrt(", "cbrt(", "abs(", "1/(")
            var newExpr = expr
            val matchedToken = tokenList.firstOrNull { expr.endsWith(it) }
            if (matchedToken != null) {
                newExpr = expr.dropLast(matchedToken.length)
            } else {
                newExpr = expr.dropLast(1)
            }

            val live = calculateLiveResult(newExpr, current.angleUnit)
            current.copy(
                expression = newExpr,
                liveResult = live,
                mainResult = if (newExpr.isEmpty()) "0" else current.mainResult,
                errorMessage = null
            )
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                mainResult = "0",
                liveResult = "",
                isCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onEquals() {
        _uiState.update { current ->
            val expr = current.expression.trim()
            if (expr.isEmpty()) return@update current

            // Auto-close missing parentheses
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }
            val balancedExpr = if (openCount > closeCount) {
                expr + ")".repeat(openCount - closeCount)
            } else {
                expr
            }

            when (val evalRes = ExpressionEvaluator.evaluate(balancedExpr, current.angleUnit)) {
                is ExpressionEvaluator.EvalResult.Success -> {
                    val historyEntry = HistoryItem(
                        expression = balancedExpr,
                        result = evalRes.formatted
                    )
                    current.copy(
                        expression = balancedExpr,
                        mainResult = evalRes.formatted,
                        liveResult = "",
                        isCalculated = true,
                        errorMessage = null,
                        history = listOf(historyEntry) + current.history.take(49)
                    )
                }
                is ExpressionEvaluator.EvalResult.Error -> {
                    current.copy(
                        errorMessage = evalRes.message,
                        liveResult = ""
                    )
                }
            }
        }
    }

    private fun calculateLiveResult(expr: String, angleUnit: AngleUnit): String {
        if (expr.isBlank()) return ""
        // Auto-close open parentheses for preview
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val previewExpr = if (openCount > closeCount) {
            expr + ")".repeat(openCount - closeCount)
        } else {
            expr
        }

        return when (val res = ExpressionEvaluator.evaluate(previewExpr, angleUnit)) {
            is ExpressionEvaluator.EvalResult.Success -> "= ${res.formatted}"
            is ExpressionEvaluator.EvalResult.Error -> ""
        }
    }

    // --- Mode & Settings ---

    fun setMode(mode: CalculatorMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun toggleAngleUnit() {
        _uiState.update { current ->
            val next = if (current.angleUnit == AngleUnit.DEG) AngleUnit.RAD else AngleUnit.DEG
            val live = calculateLiveResult(current.expression, next)
            current.copy(angleUnit = next, liveResult = live)
        }
    }

    fun toggleInverseTrig() {
        _uiState.update { it.copy(isInverseTrig = !it.isInverseTrig) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun toggleOledMode() {
        _uiState.update { it.copy(isOledBlack = !it.isOledBlack) }
    }

    fun toggleHistory(isOpen: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = isOpen) }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun useHistoryItem(item: HistoryItem, insertAsOperand: Boolean = false) {
        _uiState.update { current ->
            if (insertAsOperand) {
                val newExpr = if (current.isCalculated) item.result else current.expression + item.result
                val live = calculateLiveResult(newExpr, current.angleUnit)
                current.copy(
                    expression = newExpr,
                    liveResult = live,
                    isCalculated = false,
                    isHistoryOpen = false
                )
            } else {
                current.copy(
                    expression = item.expression,
                    mainResult = item.result,
                    liveResult = "",
                    isCalculated = true,
                    isHistoryOpen = false
                )
            }
        }
    }

    // --- Converter Operations ---

    fun setConverterCategory(category: UnitCategory) {
        val units = UnitConverterData.getUnitsForCategory(category)
        _uiState.update {
            it.copy(
                converterCategory = category,
                converterFromUnit = units[0],
                converterToUnit = units.getOrElse(1) { units[0] }
            )
        }
        updateConverterOutput()
    }

    fun setConverterFromUnit(unit: ConversionUnit) {
        _uiState.update { it.copy(converterFromUnit = unit) }
        updateConverterOutput()
    }

    fun setConverterToUnit(unit: ConversionUnit) {
        _uiState.update { it.copy(converterToUnit = unit) }
        updateConverterOutput()
    }

    fun swapConverterUnits() {
        _uiState.update {
            val from = it.converterFromUnit
            val to = it.converterToUnit
            it.copy(converterFromUnit = to, converterToUnit = from)
        }
        updateConverterOutput()
    }

    fun onConverterDigit(char: String) {
        _uiState.update { current ->
            val currentVal = current.converterInputValue
            val newVal = if (currentVal == "0") char else currentVal + char
            current.copy(converterInputValue = newVal)
        }
        updateConverterOutput()
    }

    fun onConverterDecimal() {
        _uiState.update { current ->
            val currentVal = current.converterInputValue
            if (!currentVal.contains(".")) {
                current.copy(converterInputValue = "$currentVal.")
            } else {
                current
            }
        }
        updateConverterOutput()
    }

    fun onConverterBackspace() {
        _uiState.update { current ->
            val currentVal = current.converterInputValue
            val newVal = if (currentVal.length <= 1) "0" else currentVal.dropLast(1)
            current.copy(converterInputValue = newVal)
        }
        updateConverterOutput()
    }

    fun onConverterClear() {
        _uiState.update { it.copy(converterInputValue = "0") }
        updateConverterOutput()
    }

    private fun updateConverterOutput() {
        _uiState.update { current ->
            val inputVal = current.converterInputValue.toDoubleOrNull() ?: 0.0
            val converted = UnitConverterData.convert(
                inputVal,
                current.converterFromUnit,
                current.converterToUnit
            )
            current.copy(converterOutputValue = ExpressionEvaluator.formatNumber(converted))
        }
    }
}
