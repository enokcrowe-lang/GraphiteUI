package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.data.model.BuiltInIconTheme
import com.example.data.model.IconShape
import com.example.data.model.InstalledIconPack

class IconPackManager(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    /**
     * Finds third-party icon packs installed on the device.
     */
    fun getInstalledIconPacks(): List<InstalledIconPack> {
        val iconPacks = mutableMapOf<String, InstalledIconPack>()

        val actions = listOf(
            "org.adw.launcher.THEMES",
            "com.novalauncher.THEME",
            "com.gau.go.launcherex.theme",
            "com.fede.launcher.THEME_ICONPACK",
            "com.anddoes.launcher.THEME",
            "com.teslacoilsw.launcher.THEME"
        )

        for (action in actions) {
            val intent = Intent(action)
            val activities = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            for (resolveInfo in activities) {
                val pkgName = resolveInfo.activityInfo.packageName
                if (!iconPacks.containsKey(pkgName)) {
                    val label = resolveInfo.loadLabel(packageManager).toString()
                    val icon = resolveInfo.loadIcon(packageManager)
                    iconPacks[pkgName] = InstalledIconPack(pkgName, label, icon)
                }
            }
        }
        return iconPacks.values.toList()
    }

    /**
     * Retrieves or transforms the app icon based on selected icon pack and theme.
     */
    fun processAppIcon(
        originalIcon: Drawable?,
        iconTheme: BuiltInIconTheme,
        customIconPackPkg: String?,
        iconShape: IconShape
    ): Bitmap {
        val baseBitmap = drawableToBitmap(originalIcon)

        return when (iconTheme) {
            BuiltInIconTheme.SYSTEM -> applyShapeMask(baseBitmap, iconShape)
            BuiltInIconTheme.GRAPHENE_MONOCHROME -> {
                val monochrome = applyMonochromeFilter(baseBitmap, isHighContrast = true, accentTint = null)
                applyShapeMask(monochrome, iconShape, withDarkBackdrop = true)
            }
            BuiltInIconTheme.MATERIAL_ACCENT -> {
                val accent = applyMonochromeFilter(baseBitmap, isHighContrast = false, accentTint = Color.rgb(128, 203, 196))
                applyShapeMask(accent, iconShape, withDarkBackdrop = true)
            }
            BuiltInIconTheme.GEOMETRIC_DARK -> {
                val darkMonochrome = applyMonochromeFilter(baseBitmap, isHighContrast = true, accentTint = Color.rgb(220, 220, 225))
                applyShapeMask(darkMonochrome, iconShape, withDarkBackdrop = true)
            }
            BuiltInIconTheme.MINIMAL_TEXT -> baseBitmap // Text mode doesn't render icon
        }
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap {
        if (drawable == null) {
            val bmp = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint().apply {
                color = Color.DKGRAY
                isAntiAlias = true
            }
            canvas.drawCircle(64f, 64f, 60f, paint)
            return bmp
        }

        if (drawable is BitmapDrawable && drawable.bitmap != null && !drawable.bitmap.isRecycled) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Converts a full-color app icon into a sleek, desaturated GrapheneOS monochrome icon.
     */
    private fun applyMonochromeFilter(src: Bitmap, isHighContrast: Boolean, accentTint: Int?): Bitmap {
        val width = src.width
        val height = src.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        // Grayscale color matrix
        val cm = ColorMatrix().apply {
            setSaturation(0f)
        }

        if (isHighContrast) {
            // Increase contrast slightly for crisp glyph readability
            val contrast = 1.3f
            val translate = (-0.5f * contrast + 0.5f) * 255f
            val contrastMatrix = floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
            cm.postConcat(ColorMatrix(contrastMatrix))
        }

        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)

        if (accentTint != null) {
            paint.colorFilter = PorterDuffColorFilter(accentTint, PorterDuff.Mode.SRC_ATOP)
            canvas.drawBitmap(output, 0f, 0f, paint)
        }

        return output
    }

    /**
     * Applies geometric shape mask (Circle, Squircle, Rounded Square) with optional subtle dark background.
     */
    private fun applyShapeMask(src: Bitmap, shape: IconShape, withDarkBackdrop: Boolean = false): Bitmap {
        if (shape == IconShape.SYSTEM_DEFAULT && !withDarkBackdrop) {
            return src
        }

        val size = 160
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        val rectF = RectF(0f, 0f, size.toFloat(), size.toFloat())
        val path = Path()

        when (shape) {
            IconShape.CIRCLE -> {
                path.addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CW)
            }
            IconShape.SQUIRCLE -> {
                val cornerRadius = size * 0.32f
                path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
            }
            IconShape.ROUNDED_SQUARE -> {
                val cornerRadius = size * 0.20f
                path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
            }
            IconShape.HEXAGON -> {
                val r = size / 2f
                path.moveTo(r + r * 0.9f * kotlin.math.cos(0.0).toFloat(), r + r * 0.9f * kotlin.math.sin(0.0).toFloat())
                for (i in 1..5) {
                    val angle = 2.0 * Math.PI / 6.0 * i
                    path.lineTo(r + r * 0.9f * kotlin.math.cos(angle).toFloat(), r + r * 0.9f * kotlin.math.sin(angle).toFloat())
                }
                path.close()
            }
            IconShape.SYSTEM_DEFAULT -> {
                val cornerRadius = size * 0.28f
                path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
            }
        }

        // Draw backdrop if themed
        if (withDarkBackdrop) {
            paint.color = Color.rgb(26, 26, 30) // #1A1A1E
            canvas.drawPath(path, paint)
        }

        // Clip to shape
        canvas.clipPath(path)

        // Draw scaled icon into center with comfortable padding
        val padding = if (withDarkBackdrop) (size * 0.18f).toInt() else 0
        val destRect = Rect(padding, padding, size - padding, size - padding)
        val srcRect = Rect(0, 0, src.width, src.height)

        paint.colorFilter = null
        canvas.drawBitmap(src, srcRect, destRect, paint)

        return output
    }
}
