package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun ScratchpadWidgetView(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(content) { mutableStateOf(content) }

    WidgetContainer(
        modifier = modifier.testTag("scratchpad_widget"),
        testTag = "scratchpad_widget_container"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Scratchpad",
                    tint = GrapheneAccentTeal,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Private Scratchpad",
                    style = AppTypography.titleMedium,
                    color = GrapheneTextPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted Local",
                    tint = GrapheneTextTertiary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Encrypted Local",
                    style = AppTypography.labelSmall,
                    color = GrapheneTextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onContentChange(it)
            },
            textStyle = AppTypography.bodyMedium.copy(color = GrapheneTextPrimary),
            cursorBrush = SolidColor(GrapheneAccentTeal),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp, max = 140.dp)
                .testTag("scratchpad_input")
        )
    }
}
