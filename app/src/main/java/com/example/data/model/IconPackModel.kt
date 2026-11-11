package com.example.data.model

import android.graphics.drawable.Drawable

enum class BuiltInIconTheme(val id: String, val title: String, val description: String) {
    SYSTEM("system", "System Default", "Standard application icons as provided by the system"),
    GRAPHENE_MONOCHROME("graphene_monochrome", "GrapheneOS Monochrome", "Desaturated high-contrast minimalist icons matching dark themes"),
    MINIMAL_TEXT("minimal_text", "Pure Minimalist Text", "Distraction-free typographic mode with zero icons"),
    GEOMETRIC_DARK("geometric_dark", "Minimal Geometric", "Clean dark monochrome glyphs with circular silhouettes"),
    MATERIAL_ACCENT("material_accent", "Themed Material Accent", "Subtle color-tinted monochrome icons")
}

enum class IconShape(val id: String, val title: String, val cornerRadiusPercent: Int) {
    CIRCLE("circle", "Circle", 50),
    SQUIRCLE("squircle", "Squircle", 28),
    ROUNDED_SQUARE("rounded_square", "Rounded", 18),
    HEXAGON("hexagon", "Hexagon", 10),
    SYSTEM_DEFAULT("system_default", "Default", 0)
}

data class InstalledIconPack(
    val packageName: String,
    val name: String,
    val icon: Drawable? = null
)
