package com.example.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.GestureAction
import com.example.data.model.GestureSettings

@Composable
fun GestureDetectorBox(
    gestureSettings: GestureSettings,
    onGesture: (GestureAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var totalDragY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(gestureSettings) {
                detectTapGestures(
                    onDoubleTap = {
                        onGesture(gestureSettings.doubleTap)
                    },
                    onLongPress = {
                        onGesture(gestureSettings.longPress)
                    }
                )
            }
            .pointerInput(gestureSettings) {
                detectTransformGestures { _, pan, zoom, _ ->
                    totalDragY += pan.y

                    if (totalDragY < -160f) {
                        onGesture(gestureSettings.swipeUp)
                        totalDragY = 0f
                    } else if (totalDragY > 160f) {
                        onGesture(gestureSettings.swipeDown)
                        totalDragY = 0f
                    }

                    if (zoom < 0.85f) {
                        onGesture(gestureSettings.pinchIn)
                    }
                }
            }
    ) {
        content()
    }
}
