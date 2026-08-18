package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.UnitConverterData
import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import com.example.ui.theme.LocalCalculatorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    selectedCategory: UnitCategory,
    fromUnit: ConversionUnit,
    toUnit: ConversionUnit,
    inputValue: String,
    outputValue: String,
    onCategorySelect: (UnitCategory) -> Unit,
    onFromUnitSelect: (ConversionUnit) -> Unit,
    onToUnitSelect: (ConversionUnit) -> Unit,
    onSwapUnits: () -> Unit,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val categories = UnitCategory.values()
    val availableUnits = remember(selectedCategory) {
        UnitConverterData.getUnitsForCategory(selectedCategory)
    }

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- 1. Horizontal Category Chips ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) colors.accentPrimary else colors.surfaceCard)
                        .clickable { onCategorySelect(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("category_${category.name.lowercase()}")
                ) {
                    Text(
                        text = category.titleUz,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else colors.textSecondary
                    )
                }
            }
        }

        // --- 2. Conversion Cards Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // From Unit Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surfaceCard)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Boshlang'ich",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        // From Unit Selector
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceElevated)
                                    .clickable { showFromMenu = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("from_unit_dropdown")
                            ) {
                                Text(
                                    text = "${fromUnit.nameUz} (${fromUnit.symbol})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.accentPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Ochish",
                                    tint = colors.accentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showFromMenu,
                                onDismissRequest = { showFromMenu = false },
                                modifier = Modifier.background(colors.surfaceCard)
                            ) {
                                availableUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${unit.nameUz} (${unit.symbol})",
                                                color = if (unit == fromUnit) colors.accentPrimary else colors.textPrimary
                                            )
                                        },
                                        onClick = {
                                            onFromUnitSelect(unit)
                                            showFromMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = inputValue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("converter_input_text")
                    )
                }
            }

            // Swap button centered
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onSwapUnits,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.accentPrimary)
                        .testTag("swap_units_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Almashtirish",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // To Unit Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surfaceCard)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Natija",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        // To Unit Selector
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceElevated)
                                    .clickable { showToMenu = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("to_unit_dropdown")
                            ) {
                                Text(
                                    text = "${toUnit.nameUz} (${toUnit.symbol})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.accentPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Ochish",
                                    tint = colors.accentPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showToMenu,
                                onDismissRequest = { showToMenu = false },
                                modifier = Modifier.background(colors.surfaceCard)
                            ) {
                                availableUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${unit.nameUz} (${unit.symbol})",
                                                color = if (unit == toUnit) colors.accentPrimary else colors.textPrimary
                                            )
                                        },
                                        onClick = {
                                            onToUnitSelect(unit)
                                            showToMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = outputValue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("converter_output_text")
                    )
                }
            }
        }

        // --- 3. Converter NumPad (Clean 4x4 Grid) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "7",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("7") }
                )
                CalcButton(
                    text = "8",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("8") }
                )
                CalcButton(
                    text = "9",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("9") }
                )
                CalcButton(
                    text = "C",
                    type = ButtonType.FUNCTION,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = onClear
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "4",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("4") }
                )
                CalcButton(
                    text = "5",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("5") }
                )
                CalcButton(
                    text = "6",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("6") }
                )
                CalcButton(
                    icon = Icons.Default.Backspace,
                    text = "⌫",
                    type = ButtonType.FUNCTION,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = onBackspace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "1",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("1") }
                )
                CalcButton(
                    text = "2",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("2") }
                )
                CalcButton(
                    text = "3",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("3") }
                )
                CalcButton(
                    text = "00",
                    type = ButtonType.NUMBER,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = { onDigit("00") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton(
                    text = "0",
                    type = ButtonType.NUMBER,
                    modifier = Modifier.weight(2f).height(54.dp),
                    onClick = { onDigit("0") }
                )
                CalcButton(
                    text = ".",
                    type = ButtonType.NUMBER,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = onDecimal
                )
                CalcButton(
                    text = "±",
                    type = ButtonType.FUNCTION,
                    modifier = Modifier.weight(1f).height(54.dp),
                    onClick = {
                        if (inputValue.startsWith("-")) {
                            onDigit(inputValue.drop(1))
                        } else if (inputValue != "0") {
                            onDigit("-$inputValue")
                        }
                    }
                )
            }
        }
    }
}
