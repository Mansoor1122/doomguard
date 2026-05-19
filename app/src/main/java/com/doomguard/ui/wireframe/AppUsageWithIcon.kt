package com.doomguard.ui.wireframe

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.doomguard.tracking.AppUsageEntry
import com.doomguard.ui.theme.WireframeColors

@Composable
fun AllAppsTodayUsageRows(
    entries: List<AppUsageEntry>,
    modifier: Modifier = Modifier,
    maxItems: Int = 45,
) {
    val capped = entries.take(maxItems)
    val maxMin = capped.maxOfOrNull { it.minutesToday }?.coerceAtLeast(1) ?: 1
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        capped.forEachIndexed { index, e ->
            AppTodayUsageRow(entry = e, maxMinutes = maxMin)
            if (index < capped.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = WireframeColors.Neutral.copy(alpha = 0.12f),
                )
            }
        }
    }
}

@Composable
private fun AppTodayUsageRow(
    entry: AppUsageEntry,
    maxMinutes: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AppPackageIcon(packageName = entry.packageName, modifier = Modifier.size(40.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = WireframeColors.TextPrimary,
            )
            Spacer(Modifier.height(6.dp))
            val frac = (entry.minutesToday.toFloat() / maxMinutes.toFloat()).coerceIn(0.04f, 1f)
            LinearProgressIndicator(
                progress = { frac },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = WireframeColors.Primary,
                trackColor = WireframeColors.Neutral.copy(alpha = 0.18f),
                strokeCap = StrokeCap.Round,
            )
        }
        Text(
            "${entry.minutesToday}m",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = WireframeColors.TextSecondary,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun AppPackageIcon(packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val sizePx = remember(density) { with(density) { 40.dp.roundToPx() } }
    val bitmap = remember(packageName, sizePx) {
        runCatching {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            drawable.toBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
        )
    } else {
        Icon(
            Icons.Default.Apps,
            contentDescription = null,
            modifier = modifier.clip(RoundedCornerShape(10.dp)),
            tint = WireframeColors.TextSecondary,
        )
    }
}
