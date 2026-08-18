package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.CalculatorMode
import com.example.ui.components.*
import com.example.ui.theme.CalculatorTheme
import com.example.ui.theme.LocalCalculatorColors
import com.example.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    CalculatorTheme(
        darkTheme = state.isDarkTheme,
        oledBlack = state.isOledBlack
    ) {
        val colors = LocalCalculatorColors.current

        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = colors.canvas,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colors.canvas)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. Header & Display Section
                    DisplaySection(
                        expression = state.expression,
                        liveResult = state.liveResult,
                        mainResult = state.mainResult,
                        errorMessage = state.errorMessage,
                        mode = state.mode,
                        angleUnit = state.angleUnit,
                        isDarkTheme = state.isDarkTheme,
                        isOledBlack = state.isOledBlack,
                        historyCount = state.history.size,
                        onModeChange = { viewModel.setMode(it) },
                        onAngleToggle = { viewModel.toggleAngleUnit() },
                        onThemeToggle = { viewModel.toggleTheme() },
                        onHistoryClick = { viewModel.toggleHistory(true) },
                        onBackspace = { viewModel.onBackspace() },
                        modifier = Modifier.weight(if (state.mode == CalculatorMode.CONVERTER) 0.15f else 1f)
                    )

                    // 2. Body Section: Standard vs Scientific vs Converter
                    AnimatedContent(
                        targetState = state.mode,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "mode_switch_animation",
                        modifier = Modifier.fillMaxWidth()
                    ) { targetMode ->
                        when (targetMode) {
                            CalculatorMode.STANDARD -> {
                                StandardKeypad(
                                    hasInput = state.expression.isNotEmpty(),
                                    onDigit = { viewModel.onDigit(it) },
                                    onOperator = { viewModel.onOperator(it) },
                                    onDecimal = { viewModel.onDecimal() },
                                    onClear = { viewModel.onClear() },
                                    onBackspace = { viewModel.onBackspace() },
                                    onParenthesis = { viewModel.onParenthesis() },
                                    onPercentage = { viewModel.onPercentage() },
                                    onToggleSign = { viewModel.onToggleSign() },
                                    onEquals = { viewModel.onEquals() }
                                )
                            }

                            CalculatorMode.SCIENTIFIC -> {
                                ScientificKeypad(
                                    isInverseTrig = state.isInverseTrig,
                                    hasInput = state.expression.isNotEmpty(),
                                    onDigit = { viewModel.onDigit(it) },
                                    onOperator = { viewModel.onOperator(it) },
                                    onFunction = { viewModel.onFunction(it) },
                                    onDecimal = { viewModel.onDecimal() },
                                    onClear = { viewModel.onClear() },
                                    onBackspace = { viewModel.onBackspace() },
                                    onParenthesis = { viewModel.onParenthesis() },
                                    onPercentage = { viewModel.onPercentage() },
                                    onToggleSign = { viewModel.onToggleSign() },
                                    onInverseToggle = { viewModel.toggleInverseTrig() },
                                    onEquals = { viewModel.onEquals() }
                                )
                            }

                            CalculatorMode.CONVERTER -> {
                                ConverterScreen(
                                    selectedCategory = state.converterCategory,
                                    fromUnit = state.converterFromUnit,
                                    toUnit = state.converterToUnit,
                                    inputValue = state.converterInputValue,
                                    outputValue = state.converterOutputValue,
                                    onCategorySelect = { viewModel.setConverterCategory(it) },
                                    onFromUnitSelect = { viewModel.setConverterFromUnit(it) },
                                    onToUnitSelect = { viewModel.setConverterToUnit(it) },
                                    onSwapUnits = { viewModel.swapConverterUnits() },
                                    onDigit = { viewModel.onConverterDigit(it) },
                                    onDecimal = { viewModel.onConverterDecimal() },
                                    onBackspace = { viewModel.onConverterBackspace() },
                                    onClear = { viewModel.onConverterClear() }
                                )
                            }
                        }
                    }
                }

                // 3. History Bottom Sheet
                if (state.isHistoryOpen) {
                    HistoryBottomSheet(
                        historyList = state.history,
                        onDismiss = { viewModel.toggleHistory(false) },
                        onClearHistory = { viewModel.clearHistory() },
                        onSelectItem = { item, insertAsOperand ->
                            viewModel.useHistoryItem(item, insertAsOperand)
                        }
                    )
                }
            }
        }
    }
}
