package com.m57.hermescontrol.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.theme.LocalHermesStatusColors

/** The visible meter is 2dp; its independent, non-overlapping touch target remains 48dp. */
@Composable
internal fun CompactContextUsage(
    usedTokens: Long?,
    fullTokens: Long?,
    onClick: () -> Unit,
) {
    if (fullTokens == null || fullTokens <= 0L) return
    val fraction = if (usedTokens == null) 0f else (usedTokens.toFloat() / fullTokens).coerceIn(0f, 1f)
    val status = LocalHermesStatusColors.current
    val color =
        when {
            usedTokens == null -> MaterialTheme.colorScheme.primary
            fraction >= 0.9f -> status.error
            fraction >= 0.7f -> status.warning
            else -> MaterialTheme.colorScheme.primary
        }
    val label = stringResource(R.string.context_window)
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = label
                progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
            },
        contentAlignment = Alignment.TopStart,
    ) {
        Box(Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colorScheme.surfaceVariant))
        Box(Modifier.fillMaxWidth(fraction).height(2.dp).background(color))
    }
}
