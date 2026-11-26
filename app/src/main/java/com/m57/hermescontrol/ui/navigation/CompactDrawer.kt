package com.m57.hermescontrol.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.m57.hermescontrol.ChatScreen
import com.m57.hermescontrol.CronJobsScreen
import com.m57.hermescontrol.DrawerSection
import com.m57.hermescontrol.HistoryScreen
import com.m57.hermescontrol.NavigationController
import com.m57.hermescontrol.R
import com.m57.hermescontrol.ScreenDefinition
import com.m57.hermescontrol.ScreenRegistry
import com.m57.hermescontrol.SettingsScreen
import com.m57.hermescontrol.data.local.AgentDisplayName
import com.m57.hermescontrol.data.local.AuthManager
import com.m57.hermescontrol.data.ws.ConnectionStatus
import com.m57.hermescontrol.data.ws.HermesWsClient
import com.m57.hermescontrol.profilelock.ProfileLock
import com.m57.hermescontrol.theme.LocalHermesStatusColors
import com.m57.hermescontrol.theme.LocalSpacing
import com.m57.hermescontrol.ui.profiles.AgentNameDialog

private enum class CompactDrawerGroup(
    val labelRes: Int,
) {
    AUTOMATION(R.string.compact_drawer_automation),
    CONFIGURE(R.string.compact_drawer_configure),
    DEVELOP(R.string.compact_drawer_develop),
}

@Stable
internal class CompactDrawerState {
    var expanded by mutableStateOf(emptySet<Int>())
}

@Composable
internal fun rememberCompactDrawerState(): CompactDrawerState = remember { CompactDrawerState() }

private val curatedKeys = listOf(ChatScreen, HistoryScreen, CronJobsScreen, SettingsScreen)

private fun ScreenDefinition.compactGroup(): CompactDrawerGroup =
    when (drawerSection) {
        DrawerSection.AUTOMATE -> CompactDrawerGroup.AUTOMATION
        DrawerSection.INSPECT -> CompactDrawerGroup.DEVELOP
        else -> CompactDrawerGroup.CONFIGURE
    }

@Composable
internal fun CompactDrawer(
    currentScreen: NavKey,
    hasToken: Boolean,
    state: CompactDrawerState,
    onItemClick: () -> Unit,
) {
    val lockState by ProfileLock.state.collectAsState()
    val entries = ScreenRegistry.ALL_SCREENS.filter { hasToken && (!lockState.locked || ProfileLock.allows(it.key)) }
    CompactDrawerHeader()
    for (key in curatedKeys) {
        entries.firstOrNull { it.key == key }?.let { CompactDrawerDestination(it, currentScreen, onItemClick) }
    }
    for (group in CompactDrawerGroup.entries) {
        val children = entries.filter { it.key !in curatedKeys && it.compactGroup() == group }
        if (children.isEmpty()) continue
        val expanded = group.ordinal in state.expanded
        val expandedLabel = stringResource(if (expanded) R.string.compact_expanded else R.string.compact_collapsed)
        Surface(
            onClick = {
                state.expanded = if (expanded) state.expanded - group.ordinal else state.expanded + group.ordinal
            },
            modifier = Modifier.semantics { stateDescription = expandedLabel },
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Row(
                modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = LocalSpacing.current.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(group.labelRes), modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }
        }
        if (expanded) {
            for (entry in children) CompactDrawerDestination(entry, currentScreen, onItemClick)
        }
    }
}

@Composable
private fun CompactDrawerDestination(
    entry: ScreenDefinition,
    currentScreen: NavKey,
    onItemClick: () -> Unit,
) {
    val selected = currentScreen == entry.key
    Surface(
        onClick = {
            onItemClick()
            NavigationController.navigateTo(entry.key)
        },
        modifier = Modifier.semantics { this.selected = selected },
        shape = MaterialTheme.shapes.extraLarge,
        color =
            if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor =
            if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Row(
            modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = LocalSpacing.current.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.md),
        ) {
            Icon(
                entry.icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(stringResource(entry.labelRes), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CompactDrawerHeader() {
    val name by AgentDisplayName.current.collectAsState()
    val scope by AuthManager.dataScopeFlow.collectAsState()
    val status by HermesWsClient.connectionStatus.collectAsState()
    val statusColors = LocalHermesStatusColors.current
    val (label, color) =
        when (status) {
            ConnectionStatus.CONNECTED -> R.string.session_integrations_status_connected to statusColors.success
            ConnectionStatus.CONNECTING -> R.string.notif_connecting to statusColors.warning
            ConnectionStatus.RECONNECTING -> R.string.notif_reconnecting to statusColors.warning
            ConnectionStatus.DISCONNECTED -> R.string.chat_status_disconnected to statusColors.error
            ConnectionStatus.NO_NETWORK -> R.string.chat_status_no_network to statusColors.error
            ConnectionStatus.AUTH_EXPIRED -> R.string.chat_status_auth_expired to statusColors.error
        }
    var editName by remember { mutableStateOf(false) }
    Surface(onClick = { editName = true }, color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Row(
            modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = LocalSpacing.current.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.sm),
        ) {
            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    ScreenRegistry.ALL_SCREENS.first { it.key == ChatScreen }.icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = color,
                )
                Text(stringResource(label), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    if (editName) {
        AgentNameDialog(
            connectionProfileId = scope?.connectionProfileId,
            profileName = scope?.activeProfileId ?: AuthManager.DEFAULT_PROFILE_ID,
            onDismiss = { editName = false },
        )
    }
}
