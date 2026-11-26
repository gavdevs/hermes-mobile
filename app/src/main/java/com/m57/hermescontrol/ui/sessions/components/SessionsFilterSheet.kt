package com.m57.hermescontrol.ui.sessions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.R
import com.m57.hermescontrol.theme.LocalHermesStatusColors
import com.m57.hermescontrol.theme.LocalSpacing
import com.m57.hermescontrol.ui.common.LocalCompactLayout
import com.m57.hermescontrol.ui.common.SearchBar
import com.m57.hermescontrol.ui.sessions.HistorySection
import com.m57.hermescontrol.ui.sessions.NO_PROJECT_KEY
import com.m57.hermescontrol.ui.sessions.SessionsUiState
import com.m57.hermescontrol.ui.sessions.SessionsViewModel
import com.m57.hermescontrol.ui.common.ExpressiveFilledTonalButton as FilledTonalButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsFilterSheet(
    state: SessionsUiState,
    viewModel: SessionsViewModel,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Text(
                stringResource(R.string.compact_sessions_filters),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(LocalSpacing.current.md),
            )
            SessionsStatsRow(
                total = state.total,
                loadedMessageCount = state.sessions.sumOf { it.message_count ?: 0 },
                section = state.section,
                spacing = LocalSpacing.current,
                statusColors = LocalHermesStatusColors.current,
                onPruneClick = {
                    onDismiss()
                    viewModel.showPruneDialog()
                },
            )
            SessionsFilterControls(state, viewModel)
        }
    }
}

/** Shared regular header/sheet controls: the same labels, tags and callbacks in both layouts. */
@Composable
fun SessionsFilterControls(
    state: SessionsUiState,
    viewModel: SessionsViewModel,
) {
    val spacing = LocalSpacing.current
    val chipModifier = if (LocalCompactLayout.current) Modifier.heightIn(min = 48.dp) else Modifier
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm),
        contentPadding = PaddingValues(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        item {
            FilterChip(
                selected = state.section == HistorySection.CONVERSATIONS,
                onClick = { viewModel.selectSection(HistorySection.CONVERSATIONS) },
                label = { Text(stringResource(R.string.sessions_tab_conversations)) },
                modifier = chipModifier.testTag("history_tab_conversations"),
            )
        }
        item {
            FilterChip(
                selected = state.section == HistorySection.AUTOMATIONS,
                onClick = { viewModel.selectSection(HistorySection.AUTOMATIONS) },
                label = { Text(stringResource(R.string.sessions_tab_automations)) },
                modifier = chipModifier.testTag("history_tab_automations"),
            )
        }
        item {
            FilterChip(
                selected = state.showArchived,
                onClick = { viewModel.toggleShowArchived() },
                label = { Text(stringResource(R.string.sessions_tab_archived)) },
                leadingIcon = {
                    Icon(
                        imageVector = if (state.showArchived) Icons.Filled.Unarchive else Icons.Filled.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
                modifier = chipModifier.testTag("sessions_action_toggle_archived"),
            )
        }
    }
    val sources = state.availableSources
    if (sources.size > 1) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().testTag("history_source_chips"),
            contentPadding = PaddingValues(horizontal = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            items(sources, key = { it }) { source ->
                FilterChip(
                    selected = state.activeSourceFilter == source,
                    onClick = { viewModel.selectSourceFilter(source) },
                    label = { Text(sourceLabel(source)) },
                    modifier = chipModifier.testTag("history_source_chip_$source"),
                )
            }
        }
    }
    val projectChoices = state.availableProjects
    if (state.section == HistorySection.CONVERSATIONS && projectChoices.size > 1) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().testTag("history_project_chips"),
            contentPadding = PaddingValues(horizontal = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            item(key = "group_toggle") {
                FilterChip(
                    selected = state.groupByProject,
                    onClick = { viewModel.toggleGroupByProject() },
                    label = { Text(stringResource(R.string.sessions_group_by_project)) },
                    modifier = chipModifier.testTag("history_group_by_project"),
                )
            }
            items(projectChoices, key = { "project_${it.key}" }) { choice ->
                FilterChip(
                    selected = state.activeProjectFilter == choice.key,
                    onClick = { viewModel.selectProjectFilter(choice.key) },
                    label = {
                        Text(
                            if (choice.key == NO_PROJECT_KEY) {
                                stringResource(R.string.sessions_project_none)
                            } else {
                                choice.key
                            },
                        )
                    },
                    modifier = chipModifier.testTag("history_project_chip_${choice.key}"),
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchBar(
            query = state.searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            placeholder = stringResource(R.string.sessions_search_placeholder),
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(spacing.sm))
        if (state.section == HistorySection.CONVERSATIONS) {
            BadgedBox(badge = { if (state.emptyCount > 0) Badge { Text("${state.emptyCount}") } }) {
                FilledTonalButton(
                    onClick = { viewModel.requestEmptyCleanup() },
                    enabled = state.emptyCount > 0,
                    contentPadding = PaddingValues(horizontal = spacing.md, vertical = 8.dp),
                ) {
                    Icon(Icons.Filled.DeleteSweep, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(stringResource(R.string.sessions_empty_cleanup_desc))
                }
            }
        }
    }
}
