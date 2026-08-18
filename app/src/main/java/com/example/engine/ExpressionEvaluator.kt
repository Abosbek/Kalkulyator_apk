package com.example.engine

import com.example.model.AngleUnit
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object ExpressionEvaluator {

    sealed class EvalResult {
        data class Success(val value: Double, val formatted: String) : EvalResult()
        data class Error(val message: String) : EvalResult()
    }

    /**
     * Evaluates a mathematical expression string.
     */
    fun evaluate(rawExpr: String, angleUnit: AngleUnit = AngleUnit.DEG): EvalResult {
        if (rawExpr.isBlank()) return EvalResult.Success(0.0, "0")

        try {
            val normalized = preprocess(rawExpr)
            val parser = Parser(normalized, angleUnit)
            val result = parser.parseExpression()

            if (parser.hasMoreTokens()) {
                return EvalResult.Error("Noto'g'ri ifoda")
            }

            if (result.isNaN()) {
                return EvalResult.Error("Noma'lum natija")
            }
            if (result.isInfinite()) {
                return EvalResult.Error("0 ga bo'lish mumkin emas")
            }

            return EvalResult.Success(result, formatNumber(result))
        } catch (e: ArithmeticException) {
            return EvalResult.Error(e.message ?: "Hisoblash xatosi")
        } catch (e: Exception) {
            return EvalResult.Error("Xato ifoda")
        }
    }

    /**
     * Formats numbers cleanly (e.g. 5 -> "5", 5.123 -> "5.123", very large -> scientific).
     */
    fun formatNumber(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

        val absVal = abs(value)
        if (absVal != 0.0 && (absVal >= 1e12 || absVal < 1e-7)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(value).replace("E", "e")
        }

        // Use BigDecimal to avoid float precision artifacts (e.g. 0.30000000000000004)
        return try {
            val bd = BigDecimal(value.toString(), MathContext(12, RoundingMode.HALF_UP))
                .stripTrailingZeros()
            bd.toPlainString()
        } catch (e: Exception) {
            val df = DecimalFormat("#,##0.########", DecimalFormatSymbols(Locale.US))
            df.isGroupingUsed = false
            df.format(value)
        }
    }

    /**
     * Preprocesses symbols, implicit multiplication, and percentage shorthands.
     */
    private fun preprocess(input: String): String {
        var str = input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace(" ", "")

        // Handle implicit multiplication: e.g. 2(3) -> 2*(3), (2)(3) -> (2)*(3), 2PI -> 2*PI, 2sqrt -> 2*sqrt
        val implicitRegex1 = Regex("(\\d|PI|e|\\))(\\()|(\\))(\\d|PI|e|sin|cos|tan|asin|acos|atan|ln|log|sqrt|cbrt)")
        while (implicitRegex1.containsMatchIn(str)) {
            str = implicitRegex1.replace(str) { match ->
                val v1 = match.groupValues[1]
                val v2 = match.groupValues[2]
                val v3 = match.groupValues[3]
                val v4 = match.groupValues[4]
                if (v1.isNotEmpty() && v2.isNotEmpty()) "$v1*$v2"
                else "$v3*$v4"
            }
        }

        val implicitRegex2 = Regex("(\\d)(PI|e|sin|cos|tan|asin|acos|atan|ln|log|sqrt|cbrt)")
        str = implicitRegex2.replace(str, "$1*$2")

        return str
    }

    private class Parser(private val src: String, private val angleUnit: AngleUnit) {
        private var pos = 0

        fun hasMoreTokens(): Boolean {
            skipWhitespace()
            return pos < src.length
        }

        private fun peek(): Char = if (pos < src.length) src[pos] else '\u0000'

        private fun getChar(): Char = if (pos < src.length) src[pos++] else '\u0000'

        private fun skipWhitespace() {
            while (pos < src.length && src[pos].isWhitespace()) pos++
        }

        // Expression -> Term ( ('+' | '-') Term )*
        fun parseExpression(): Double {
            var result = parseTerm()
            while (true) {
                skipWhitespace()
                val ch = peek()
                if (ch == '+') {
                    getChar()
                    val nextTerm = parseTerm()
                    result += nextTerm
                } else if (ch == '-') {
                    getChar()
                    val nextTerm = parseTerm()
                    result -= nextTerm
                } else {
                    break
                }
            }
            return result
        }

        // Term -> Factor ( ('*' | '/' | '%') Factor )*
        private fun parseTerm(): Double {
            var result = parseFactor()
            while (true) {
                skipWhitespace()
                val ch = peek()
                if (ch == '*') {
                    getChar()
                    result *= parseFactor()
                } else if (ch == '/') {
                    getChar()
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("0 ga bo'lish mumkin emas")
                    result /= divisor
                } else if (ch == '%') {
                    // Modulo or percentage
                    getChar()
                    // Check if modulo is followed by another factor
                    skipWhitespace()
                    val nextChar = peek()
                    if (nextChar.isDigit() || nextChar == '(' || nextChar == '+' || nextChar == '-' || nextChar.isLetter()) {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("0 ga bo'lish mumkin emas")
                        result %= divisor
                    } else {
                        // Standalone %: e.g. 50% = 0.5
                        result /= 100.0
                    }
                } else {
                    break
                }
            }
            return result
        }

        // Factor -> Power ( ('^') Power )*
        private fun parseFactor(): Double {
            var base = parseUnary()
            skipWhitespace()
            if (peek() == '^') {
                getChar()
                val exponent = parseFactor() // right-associative power
                base = base.pow(exponent)
            }
            return parsePostfix(base)
        }

        private fun parsePostfix(initial: Double): Double {
            var res = initial
            while (true) {
                skipWhitespace()
                val ch = peek()
                if (ch == '!') {
                    getChar()
                    res = factorial(res)
                } else if (ch == '%') {
                    // Check if followed by binary operator; if not, treat as percent
                    getChar()
                    res /= 100.0
                } else {
                    break
                }
            }
            return res
        }

        // Unary: '+' | '-' | Primary
        private fun parseUnary(): Double {
            skipWhitespace()
            val ch = peek()
            if (ch == '+') {
                getChar()
                return parseUnary()
            }
            if (ch == '-') {
                getChar()
                return -parseUnary()
            }
            return parsePrimary()
        }

        // Primary: Number | '(' Expression ')' | Function '(' Expression ')' | Constants
        private fun parsePrimary(): Double {
            skipWhitespace()
            val ch = peek()

            if (ch == '(') {
                getChar() // consume '('
                val res = parseExpression()
                skipWhitespace()
                if (peek() == ')') {
                    getChar() // consume ')'
                }
                return parsePostfix(res)
            }

            if (ch.isDigit() || ch == '.') {
                return parseNumber()
            }

            if (ch.isLetter()) {
                val ident = parseIdentifier()
                return when (ident.lowercase()) {
                    "pi" -> parsePostfix(Math.PI)
                    "e" -> parsePostfix(Math.E)
                    "sin" -> {
                        val arg = parseArgument()
                        val rad = if (angleUnit == AngleUnit.DEG) Math.toRadians(arg) else arg
                        sin(rad)
                    }
                    "cos" -> {
                        val arg = parseArgument()
                        val rad = if (angleUnit == AngleUnit.DEG) Math.toRadians(arg) else arg
                        cos(rad)
                    }
                    "tan" -> {
                        val arg = parseArgument()
                        val rad = if (angleUnit == AngleUnit.DEG) Math.toRadians(arg) else arg
                        if (cos(rad) == 0.0) throw ArithmeticException("Aniqlanmagan qiymat")
                        tan(rad)
                    }
                    "asin" -> {
                        val arg = parseArgument()
                        if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain xatosi [-1, 1]")
                        val res = asin(arg)
                        if (angleUnit == AngleUnit.DEG) Math.toDegrees(res) else res
                    }
                    "acos" -> {
                        val arg = parseArgument()
                        if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain xatosi [-1, 1]")
                        val res = acos(arg)
                        if (angleUnit == AngleUnit.DEG) Math.toDegrees(res) else res
                    }
                    "atan" -> {
                        val arg = parseArgument()
                        val res = atan(arg)
                        if (angleUnit == AngleUnit.DEG) Math.toDegrees(res) else res
                    }
                    "ln" -> {
                        val arg = parseArgument()
                        if (arg <= 0.0) throw ArithmeticException("ln faqat musbat sonlar uchun")
                        ln(arg)
                    }
                    "log" -> {
                        val arg = parseArgument()
                        if (arg <= 0.0) throw ArithmeticException("log faqat musbat sonlar uchun")
                        log10(arg)
                    }
                    "sqrt" -> {
                        val arg = parseArgument()
                        if (arg < 0.0) throw ArithmeticException("Manfiy sondan ildiz chiqmaydi")
                        sqrt(arg)
                    }
                    "cbrt" -> {
                        val arg = parseArgument()
                        cbrt(arg)
                    }
                    "abs" -> {
                        val arg = parseArgument()
                        abs(arg)
                    }
                    else -> throw IllegalArgumentException("Noma'lum funksiya: $ident")
                }
            }

            throw IllegalArgumentException("Kutilmagan belgi: '$ch'")
        }

        private fun parseArgument(): Double {
            skipWhitespace()
            return if (peek() == '(') {
                getChar() // '('
                val res = parseExpression()
                skipWhitespace()
                if (peek() == ')') getChar()
                res
            } else {
                parseUnary()
            }
        }

        private fun parseNumber(): Double {
            val start = pos
            var hasDot = false
            while (pos < src.length) {
                val c = src[pos]
                if (c.isDigit()) {
                    pos++
                } else if (c == '.' && !hasDot) {
                    hasDot = true
                    pos++
                } else {
                    break
                }
            }
            val numStr = src.substring(start, pos)
            return numStr.toDoubleOrNull() ?: 0.0
        }

        private fun parseIdentifier(): String {
            val start = pos
            while (pos < src.length && src[pos].isLetter()) {
                pos++
            }
            return src.substring(start, pos)
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != floor(n) || n > 170) {
                throw ArithmeticException("Faktorial butun musbat son (≤170) uchun")
            }
            var res = 1.0
            for (i in 2..n.toInt()) {
                res *= i
            }
            return res
        }
    }
}
