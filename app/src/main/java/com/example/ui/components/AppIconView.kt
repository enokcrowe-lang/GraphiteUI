package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.BuiltInIconTheme
import com.example.data.model.IconShape
import com.example.data.repository.IconPackManager
import com.example.ui.theme.GrapheneDarkCard
import com.example.ui.theme.GrapheneTextPrimary

@Composable
fun AppIconView(
    app: AppItem,
    iconTheme: BuiltInIconTheme,
    iconShape: IconShape,
    iconPackManager: IconPackManager,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    customIconPackPkg: String? = null
) {
    if (iconTheme == BuiltInIconTheme.MINIMAL_TEXT) {
        // Pure minimalist text mode: show sleek initial letter badge
        val initial = app.displayLabel.take(1).uppercase()
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(GrapheneDarkCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = GrapheneTextPrimary,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val processedBitmap: Bitmap = remember(app.componentKey, iconTheme, iconShape, customIconPackPkg) {
        iconPackManager.processAppIcon(
            originalIcon = app.icon,
            iconTheme = iconTheme,
            customIconPackPkg = customIconPackPkg,
            iconShape = iconShape
        )
    }

    Image(
        bitmap = processedBitmap.asImageBitmap(),
        contentDescription = app.displayLabel,
        modifier = modifier.size(size)
    )
}
