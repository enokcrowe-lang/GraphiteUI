package com.example.ui.drawer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppItem
import com.example.ui.theme.*

@Composable
fun AppRenameDialog(
    app: AppItem,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember(app) { mutableStateOf(app.customLabel ?: app.defaultLabel) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = GrapheneDarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, GrapheneBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_rename_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Rename Application",
                    style = AppTypography.titleLarge,
                    color = GrapheneTextPrimary
                )
                Text(
                    text = "Original name: ${app.defaultLabel}",
                    style = AppTypography.bodySmall,
                    color = GrapheneTextTertiary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GrapheneAccentTeal,
                        unfocusedBorderColor = GrapheneBorder,
                        focusedTextColor = GrapheneTextPrimary,
                        unfocusedTextColor = GrapheneTextPrimary,
                        focusedLabelColor = GrapheneAccentTeal,
                        unfocusedLabelColor = GrapheneTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_input_field")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onSave("") // Reset
                            onDismiss()
                        },
                        modifier = Modifier.testTag("rename_reset_button")
                    ) {
                        Text("Reset Default", color = GrapheneTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSave(text.trim())
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GrapheneAccentTeal,
                            contentColor = AmoledBlack
                        ),
                        modifier = Modifier.testTag("rename_save_button")
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
