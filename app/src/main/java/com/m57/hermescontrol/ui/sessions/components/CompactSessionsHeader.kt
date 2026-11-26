package com.m57.hermescontrol.ui.sessions.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.m57.hermescontrol.NavigationController
import com.m57.hermescontrol.R
import com.m57.hermescontrol.applock.DropdownMenu
import com.m57.hermescontrol.theme.LocalSpacing
import com.m57.hermescontrol.ui.common.HermesIconButton
import com.m57.hermescontrol.ui.sessions.HistorySection
import com.m57.hermescontrol.ui.sessions.NO_PROJECT_KEY
import com.m57.hermescontrol.ui.sessions.SessionsUiState
import com.m57.hermescontrol.ui.sessions.SessionsViewModel

private val SessionsUiState.hasCompactFilters: Boolean
    get() =
        section != HistorySection.CONVERSATIONS || showArchived || showHidden ||
            sourceFilter != null || projectFilter != null || groupByProject || searchQuery.isNotEmpty()

@Composable
fun CompactSessionsActions(
    state: SessionsUiState,
    viewModel: SessionsViewModel,
    onShowFilters: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    HermesIconButton(Icons.Filled.Search, stringResource(R.string.chat_action_search), onShowFilters)
    BadgedBox(badge = { if (state.hasCompactFilters) Badge() }) {
        HermesIconButton(Icons.Filled.FilterList, stringResource(R.string.compact_sessions_filters), onShowFilters)
    }
    Box {
        HermesIconButton(Icons.Filled.MoreVert, stringResource(R.string.profiles_more_options), { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CompactHistoryMenuItem(R.string.content_desc_refresh, { expanded = false }) {
                viewModel.loadSessions(forceRefresh = true)
            }
            CompactHistoryMenuItem(R.string.sessions_action_prune, { expanded = false }, viewModel::showPruneDialog)
            CompactHistoryMenuItem(R.string.sessions_action_select, { expanded = false }, viewModel::toggleSelecting)
            CompactHistoryMenuItem(R.string.content_desc_new_chat, { expanded = false }) {
                NavigationController.openNewChat()
            }
            if (state.hasHiddenSessions || state.showHidden) {
                CompactHistoryMenuItem(
                    if (state.showHidden) R.string.sessions_hide_hidden else R.string.sessions_show_hidden,
                    { expanded = false },
                    viewModel::toggleShowHidden,
                )
            }
        }
    }
}

@Composable
private fun CompactHistoryMenuItem(
    label: Int,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(stringResource(label)) },
        onClick = {
            onDismiss()
            onClick()
        },
        modifier = Modifier.heightIn(min = 48.dp),
    )
}

@Composable
fun CompactSessionsHeader(
    state: SessionsUiState,
    viewModel: SessionsViewModel,
    onShowFilters: () -> Unit,
) {
    if (!state.hasCompactFilters) return
    val labels =
        listOfNotNull(
            if (state.section == HistorySection.AUTOMATIONS) {
                stringResource(R.string.sessions_tab_automations)
            } else {
                null
            },
            if (state.showArchived) stringResource(R.string.sessions_tab_archived) else null,
            state.sourceFilter?.let { sourceLabel(it) },
            state.projectFilter?.let {
                if (it == NO_PROJECT_KEY) stringResource(R.string.sessions_project_none) else it
            },
            if (state.groupByProject) stringResource(R.string.sessions_group_by_project) else null,
            if (state.showHidden) stringResource(R.string.sessions_show_hidden) else null,
            state.searchQuery.takeIf { it.isNotEmpty() },
        )
    Row(Modifier.padding(horizontal = LocalSpacing.current.md)) {
        InputChip(
            selected = true,
            onClick = onShowFilters,
            label = {
                Text(
                    stringResource(R.string.compact_sessions_summary, labels.joinToString(", ")),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailingIcon = {
                HermesIconButton(
                    Icons.Filled.Close,
                    stringResource(R.string.action_clear),
                    onClick = {
                        viewModel.setSearchQuery("")
                        viewModel.selectSourceFilter(null)
                        viewModel.selectProjectFilter(null)
                        if (state.groupByProject) viewModel.toggleGroupByProject()
                        if (state.showHidden) viewModel.toggleShowHidden()
                        if (state.showArchived) viewModel.toggleShowArchived()
                        viewModel.selectSection(HistorySection.CONVERSATIONS)
                    },
                    modifier = Modifier.size(48.dp),
                )
            },
            modifier = Modifier.heightIn(min = 48.dp),
        )
    }
}
