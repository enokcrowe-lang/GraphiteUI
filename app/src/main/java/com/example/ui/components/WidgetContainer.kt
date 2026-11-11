package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GrapheneBorder
import com.example.ui.theme.GrapheneDarkCard

@Composable
fun WidgetContainer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    showBorder: Boolean = true,
    onClick: (() -> Unit)? = null,
    testTag: String = "widget_container",
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = GrapheneDarkCard.copy(alpha = 0.85f),
        border = if (showBorder) BorderStroke(1.dp, GrapheneBorder) else null,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}
