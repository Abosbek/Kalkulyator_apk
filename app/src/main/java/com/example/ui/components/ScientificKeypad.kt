package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScientificKeypad(
    isInverseTrig: Boolean,
    hasInput: Boolean,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onFunction: (String) -> Unit,
    onDecimal: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onParenthesis: () -> Unit,
    onPercentage: () -> Unit,
    onToggleSign: () -> Unit,
    onInverseToggle: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // --- Scientific Row 1: INV, sin/asin, cos/acos, tan/atan, π ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(
                text = if (isInverseTrig) "INV •" else "INV",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = onInverseToggle
            )
            CalcButton(
                text = if (isInverseTrig) "sin⁻¹" else "sin",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("sin") }
            )
            CalcButton(
                text = if (isInverseTrig) "cos⁻¹" else "cos",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("cos") }
            )
            CalcButton(
                text = if (isInverseTrig) "tan⁻¹" else "tan",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("tan") }
            )
            CalcButton(
                text = "π",
                type = ButtonType.FUNCTION,
                fontSize = 15.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("pi") }
            )
        }

        // --- Scientific Row 2: ln, log, √, x^y, e ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(
                text = "ln",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("ln") }
            )
            CalcButton(
                text = "log",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("log") }
            )
            CalcButton(
                text = "√",
                type = ButtonType.FUNCTION,
                fontSize = 15.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("sqrt") }
            )
            CalcButton(
                text = "xʸ",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("pow") }
            )
            CalcButton(
                text = "e",
                type = ButtonType.FUNCTION,
                fontSize = 15.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("e") }
            )
        }

        // --- Scientific Row 3: x², 1/x, x!, (, ) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(
                text = "x²",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("sqr") }
            )
            CalcButton(
                text = "1/x",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("inv") }
            )
            CalcButton(
                text = "x!",
                type = ButtonType.FUNCTION,
                fontSize = 13.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onFunction("fact") }
            )
            CalcButton(
                text = "(",
                type = ButtonType.FUNCTION,
                fontSize = 15.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onDigit("(") }
            )
            CalcButton(
                text = ")",
                type = ButtonType.FUNCTION,
                fontSize = 15.sp,
                shapeRadius = 14.dp,
                modifier = Modifier.weight(1f).height(42.dp),
                onClick = { onDigit(")") }
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // --- Standard NumPad Row 1: C, ⌫, %, ÷ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = if (hasInput) "C" else "AC",
                type = ButtonType.FUNCTION,
                fontSize = 18.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = onClear
            )
            CalcButton(
                icon = Icons.Default.Backspace,
                text = "⌫",
                type = ButtonType.FUNCTION,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = onBackspace
            )
            CalcButton(
                text = "%",
                type = ButtonType.FUNCTION,
                fontSize = 18.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = onPercentage
            )
            CalcButton(
                text = "÷",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onOperator("÷") }
            )
        }

        // --- Standard NumPad Row 2: 7, 8, 9, × ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "7",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("7") }
            )
            CalcButton(
                text = "8",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("8") }
            )
            CalcButton(
                text = "9",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("9") }
            )
            CalcButton(
                text = "×",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onOperator("×") }
            )
        }

        // --- Standard NumPad Row 3: 4, 5, 6, − ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "4",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("4") }
            )
            CalcButton(
                text = "5",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("5") }
            )
            CalcButton(
                text = "6",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("6") }
            )
            CalcButton(
                text = "−",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onOperator("-") }
            )
        }

        // --- Standard NumPad Row 4: 1, 2, 3, + ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "1",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("1") }
            )
            CalcButton(
                text = "2",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("2") }
            )
            CalcButton(
                text = "3",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("3") }
            )
            CalcButton(
                text = "+",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onOperator("+") }
            )
        }

        // --- Standard NumPad Row 5: ±, 0, ., = ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "±",
                type = ButtonType.FUNCTION,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = onToggleSign
            )
            CalcButton(
                text = "0",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = { onDigit("0") }
            )
            CalcButton(
                text = ".",
                type = ButtonType.NUMBER,
                fontSize = 24.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = onDecimal
            )
            CalcButton(
                text = "=",
                type = ButtonType.EQUALS,
                fontSize = 24.sp,
                shapeRadius = 18.dp,
                modifier = Modifier.weight(1f).height(52.dp),
                onClick = onEquals
            )
        }
    }
}
