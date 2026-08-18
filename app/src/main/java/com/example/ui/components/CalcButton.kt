package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors

enum class ButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    EQUALS,
    ACCENT
}

@Composable
fun CalcButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    type: ButtonType = ButtonType.NUMBER,
    fontSize: TextUnit = 24.sp,
    shapeRadius: Dp = 24.dp,
    testTag: String = text ?: "calc_button",
    onClick: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "btn_press_scale"
    )

    val bgColor = when (type) {
        ButtonType.NUMBER -> colors.keyNumber
        ButtonType.OPERATOR -> colors.keyOperator
        ButtonType.FUNCTION -> colors.keyFunction
        ButtonType.EQUALS -> colors.keyEquals
        ButtonType.ACCENT -> colors.accentPrimary
    }

    val contentColor = when (type) {
        ButtonType.NUMBER -> colors.keyNumberText
        ButtonType.OPERATOR -> colors.keyOperatorText
        ButtonType.FUNCTION -> colors.keyFunctionText
        ButtonType.EQUALS -> colors.keyEqualsText
        ButtonType.ACCENT -> Color.White
    }

    val shape = RoundedCornerShape(shapeRadius)

    val gradientModifier = if (type == ButtonType.EQUALS) {
        Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF5D6EFC), Color(0xFF3F51F5))
            ),
            shape = shape
        )
    } else {
        Modifier.background(color = bgColor, shape = shape)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(shape)
            .then(gradientModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(
                    color = if (type == ButtonType.NUMBER) colors.accentPrimary else Color.White.copy(alpha = 0.4f)
                ),
                onClick = onClick
            )
            .testTag(testTag)
            .padding(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text ?: "Button icon",
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        } else if (text != null) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = if (type == ButtonType.EQUALS || type == ButtonType.OPERATOR) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
