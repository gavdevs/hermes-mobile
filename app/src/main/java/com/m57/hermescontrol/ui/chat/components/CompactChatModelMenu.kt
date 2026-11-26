package com.m57.hermescontrol.ui.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.applock.DropdownMenu
import com.m57.hermescontrol.ui.chat.ChatUiState
import com.m57.hermescontrol.ui.chat.ChatViewModel

@Composable
internal fun CompactChatModelMenu(
    state: ChatUiState,
    viewModel: ChatViewModel,
    onDismiss: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                stringResource(
                    R.string.compact_model_label,
                    state.currentSessionModel?.let { composerModelLabel(it, state.showModelProvider) }
                        ?: stringResource(R.string.chat_model_label),
                ),
            )
        },
        onClick = {
            onDismiss()
            viewModel.openModelPicker()
        },
        modifier = Modifier.heightIn(min = 48.dp).testTag("compact_menu_model"),
    )
    var reasoningExpanded by remember { mutableStateOf(false) }
    Box {
        DropdownMenuItem(
            text = {
                Column {
                    Text(stringResource(R.string.chat_reasoning_menu_desc))
                    Text(compactReasoningLabel(state.pendingReasoningLevel ?: state.reasoningLevel))
                }
            },
            enabled =
                state.isConnected && (state.isSessionReady || state.isDraftSession) &&
                    state.pendingReasoningLevel == null,
            onClick = { reasoningExpanded = true },
            modifier = Modifier.heightIn(min = 48.dp),
        )
        DropdownMenu(expanded = reasoningExpanded, onDismissRequest = { reasoningExpanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.chat_fast_mode_label)) },
                trailingIcon = {
                    if (state.fastMode) Icon(Icons.Filled.Check, contentDescription = null)
                },
                enabled =
                    state.isConnected && state.currentModelCapabilities?.fast == true && !state.isFastModeChanging,
                onClick = { viewModel.toggleFastMode() },
                modifier = Modifier.heightIn(min = 48.dp),
            )
            val levels =
                listOf(
                    "none" to R.string.compact_reasoning_none,
                    "minimal" to R.string.compact_reasoning_minimal,
                    "low" to R.string.compact_reasoning_low,
                    "medium" to R.string.compact_reasoning_medium,
                    "high" to R.string.compact_reasoning_high,
                    "xhigh" to R.string.compact_reasoning_xhigh,
                    "max" to R.string.compact_reasoning_max,
                    "ultra" to R.string.compact_reasoning_ultra,
                )
            for ((level, label) in levels) {
                val unsupported = state.currentModelCapabilities?.reasoningSupport == false
                val noneDisabled = level == "none" && state.currentModelCapabilities?.can_disable_reasoning == false
                val selected = (state.pendingReasoningLevel ?: state.reasoningLevel) == level
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(stringResource(label))
                            if (
                                selected && state.reasoningWireLevel != null &&
                                !state.reasoningWireLevel.equals(level, ignoreCase = true)
                            ) {
                                Text(
                                    stringResource(
                                        R.string.chat_reasoning_clamped_desc,
                                        stringResource(label),
                                        compactReasoningLabel(state.reasoningWireLevel),
                                    ),
                                )
                            }
                        }
                    },
                    trailingIcon = { if (selected) Icon(Icons.Filled.Check, contentDescription = null) },
                    enabled = !unsupported && !noneDisabled && state.pendingReasoningLevel == null,
                    onClick = {
                        reasoningExpanded = false
                        onDismiss()
                        viewModel.setReasoningLevel(level)
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                )
            }
        }
    }
}

@Composable
private fun compactReasoningLabel(level: String?): String =
    when (level) {
        null -> stringResource(R.string.chat_reasoning_default)
        "none" -> stringResource(R.string.compact_reasoning_none)
        "minimal" -> stringResource(R.string.compact_reasoning_minimal)
        "low" -> stringResource(R.string.compact_reasoning_low)
        "medium" -> stringResource(R.string.compact_reasoning_medium)
        "high" -> stringResource(R.string.compact_reasoning_high)
        "xhigh" -> stringResource(R.string.compact_reasoning_xhigh)
        "max" -> stringResource(R.string.compact_reasoning_max)
        "ultra" -> stringResource(R.string.compact_reasoning_ultra)
        else -> level
    }
