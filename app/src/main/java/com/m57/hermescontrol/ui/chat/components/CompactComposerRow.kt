package com.m57.hermescontrol.ui.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.ui.common.LocalCompactLayout

internal enum class CompactComposerPart { INPUT, TOOLBAR }

/** Only changes placement: the existing field, gesture owner and active-flow children stay composed. */
@Composable
internal fun ComposerContent(
    showSecondaryAction: Boolean,
    content: @Composable () -> Unit,
) {
    if (LocalCompactLayout.current) {
        CompactComposerRow(showSecondaryAction, content)
    } else {
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)) { content() }
    }
}

@Composable
private fun CompactComposerRow(
    showSecondaryAction: Boolean,
    content: @Composable () -> Unit,
) {
    Layout(content = content) { measurables, constraints ->
        val width = constraints.maxWidth
        val toolbarIndex = measurables.indexOfFirst { it.layoutId == CompactComposerPart.TOOLBAR }
        val inputIndex = measurables.indexOfFirst { it.layoutId == CompactComposerPart.INPUT }
        val toolbarWidth = ((if (showSecondaryAction) 144 else 96).dp.roundToPx()).coerceAtMost(width)
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val childConstraints = constraints.copy(minHeight = 0)
        for (index in measurables.indices) {
            val childWidth =
                when (index) {
                    toolbarIndex -> toolbarWidth
                    inputIndex -> width - toolbarWidth
                    else -> width
                }
            placeables[index] =
                measurables[index].measure(
                    childConstraints.copy(minWidth = childWidth, maxWidth = childWidth),
                )
        }
        val input = requireNotNull(placeables[inputIndex])
        val toolbar = requireNotNull(placeables[toolbarIndex])
        val rowHeight = maxOf(input.height, toolbar.height)
        var height = rowHeight
        for (index in placeables.indices) {
            if (index != inputIndex && index != toolbarIndex) height += requireNotNull(placeables[index]).height
        }
        layout(width, constraints.constrainHeight(height)) {
            var y = 0
            for (index in placeables.indices) {
                if (index == toolbarIndex) continue
                when (index) {
                    inputIndex -> {
                        input.placeRelative(0, y + (rowHeight - input.height) / 2)
                        toolbar.placeRelative(width - toolbarWidth, y + (rowHeight - toolbar.height) / 2)
                        y += rowHeight
                    }

                    else -> {
                        val child = requireNotNull(placeables[index])
                        child.placeRelative(0, y)
                        y += child.height
                    }
                }
            }
        }
    }
}
