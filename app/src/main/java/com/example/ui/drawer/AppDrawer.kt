package com.example.ui.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.IconPackManager
import com.example.ui.components.AppIconView
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawer(
    apps: List<AppItem>,
    searchQuery: String,
    selectedCategory: AppCategory,
    iconTheme: BuiltInIconTheme,
    iconShape: IconShape,
    iconScalePercent: Int,
    gridColumns: Int,
    showAppLabels: Boolean,
    showAlphabetScrubber: Boolean,
    customIconPackPkg: String?,
    iconPackManager: IconPackManager,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (AppCategory) -> Unit,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onCloseDrawer()
    }

    var isGridView by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val focusRequester = remember { FocusRequester() }

    val alphabet = remember { ('A'..'Z').toList() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .testTag("app_drawer_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Header Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = GrapheneDarkCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, GrapheneBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_search_surface")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier.testTag("drawer_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Close drawer",
                            tint = GrapheneTextPrimary
                        )
                    }

                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = "Search ${apps.size} applications…",
                                style = AppTypography.bodyMedium,
                                color = GrapheneTextTertiary
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = GrapheneTextPrimary,
                            unfocusedTextColor = GrapheneTextPrimary,
                            cursorColor = GrapheneAccentTeal
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .testTag("drawer_search_input")
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.testTag("drawer_clear_search")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = GrapheneTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.testTag("drawer_toggle_view_mode")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List",
                            tint = GrapheneTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppCategory.entries.take(4).forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) GrapheneAccentTeal else GrapheneDarkCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GrapheneAccentTeal else GrapheneBorder
                        ),
                        modifier = Modifier
                            .testTag("category_chip_${cat.name.lowercase()}")
                            .clickable { onCategorySelect(cat) }
                    ) {
                        Text(
                            text = cat.title,
                            style = AppTypography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            color = if (isSelected) AmoledBlack else GrapheneTextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Content (Grid or Minimalist List)
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No apps",
                            tint = GrapheneTextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No applications found",
                            style = AppTypography.titleMedium,
                            color = GrapheneTextSecondary
                        )
                    }
                }
            } else if (isGridView) {
                val iconSize = (46 * (iconScalePercent / 100f)).dp

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    state = gridState,
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("drawer_grid_view")
                ) {
                    items(apps, key = { it.componentKey }) { app ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .testTag("app_item_${app.packageName}")
                                .clip(RoundedCornerShape(14.dp))
                                .combinedClickable(
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            AppIconView(
                                app = app,
                                iconTheme = iconTheme,
                                iconShape = iconShape,
                                iconPackManager = iconPackManager,
                                size = iconSize,
                                customIconPackPkg = customIconPackPkg
                            )

                            if (showAppLabels) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = app.displayLabel,
                                    style = AppTypography.bodySmall,
                                    color = GrapheneTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            } else {
                // Minimalist List View (GrapheneOS / Niagara style)
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("drawer_list_view")
                ) {
                    items(apps, key = { it.componentKey }) { app ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_item_${app.packageName}")
                                .clip(RoundedCornerShape(12.dp))
                                .combinedClickable(
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            AppIconView(
                                app = app,
                                iconTheme = iconTheme,
                                iconShape = iconShape,
                                iconPackManager = iconPackManager,
                                size = 40.dp,
                                customIconPackPkg = customIconPackPkg
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.displayLabel,
                                    style = AppTypography.bodyLarge,
                                    color = GrapheneTextPrimary
                                )
                                if (app.customLabel != null) {
                                    Text(
                                        text = "Custom label (${app.defaultLabel})",
                                        style = AppTypography.labelSmall,
                                        color = GrapheneTextTertiary
                                    )
                                }
                            }
                            if (app.isPinnedDock) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Pinned to Dock",
                                    tint = GrapheneAccentTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fast Alphabet Scrubber on right edge
        if (showAlphabetScrubber && apps.size > 10 && searchQuery.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp, top = 120.dp, bottom = 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrapheneDarkCard.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                alphabet.forEach { letter ->
                    Text(
                        text = letter.toString(),
                        style = AppTypography.labelSmall.copy(fontSize = 9.sp),
                        color = GrapheneTextTertiary,
                        modifier = Modifier
                            .clickable {
                                val targetIndex = apps.indexOfFirst {
                                    it.displayLabel.firstOrNull()?.uppercaseChar() == letter
                                }
                                if (targetIndex != -1) {
                                    coroutineScope.launch {
                                        if (isGridView) {
                                            gridState.animateScrollToItem(targetIndex)
                                        } else {
                                            listState.animateScrollToItem(targetIndex)
                                        }
                                    }
                                }
                            }
                            .padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}
