package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StandardKeypad(
    hasInput: Boolean,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onParenthesis: () -> Unit,
    onPercentage: () -> Unit,
    onToggleSign: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: C, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcButton(
                text = if (hasInput) "C" else "AC",
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = onClear
            )
            CalcButton(
                text = "( )",
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = onParenthesis
            )
            CalcButton(
                text = "%",
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = onPercentage
            )
            CalcButton(
                text = "÷",
                type = ButtonType.OPERATOR,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcButton(
                text = "7",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("7") }
            )
            CalcButton(
                text = "8",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("8") }
            )
            CalcButton(
                text = "9",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("9") }
            )
            CalcButton(
                text = "×",
                type = ButtonType.OPERATOR,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcButton(
                text = "4",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("4") }
            )
            CalcButton(
                text = "5",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("5") }
            )
            CalcButton(
                text = "6",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("6") }
            )
            CalcButton(
                text = "−",
                type = ButtonType.OPERATOR,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onOperator("-") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcButton(
                text = "1",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("1") }
            )
            CalcButton(
                text = "2",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("2") }
            )
            CalcButton(
                text = "3",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("3") }
            )
            CalcButton(
                text = "+",
                type = ButtonType.OPERATOR,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ±, 0, ., = (and long press on . or swipe for backspace, plus ⌫ function)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcButton(
                text = "±",
                type = ButtonType.FUNCTION,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = onToggleSign
            )
            CalcButton(
                text = "0",
                type = ButtonType.NUMBER,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = { onDigit("0") }
            )
            CalcButton(
                text = ".",
                type = ButtonType.NUMBER,
                fontSize = 26.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = onDecimal
            )
            CalcButton(
                text = "=",
                type = ButtonType.EQUALS,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.15f),
                onClick = onEquals
            )
        }
    }
}
