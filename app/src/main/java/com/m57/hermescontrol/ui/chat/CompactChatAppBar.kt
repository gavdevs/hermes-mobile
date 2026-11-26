package com.m57.hermescontrol.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.m57.hermescontrol.R
import com.m57.hermescontrol.ui.common.ActionFeedbackSnackbarHost
import com.m57.hermescontrol.ui.common.HermesIconButton
import com.m57.hermescontrol.ui.common.HermesScaffold
import com.m57.hermescontrol.ui.common.LocalActionFeedbackHost
import com.m57.hermescontrol.ui.common.LocalCompactLayout
import com.m57.hermescontrol.ui.common.LocalDrawerGestureController
import com.m57.hermescontrol.ui.common.NavIcon

/** Compact-only scaffold adapter; the regular chat keeps its existing shared scaffold. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompactChatScaffold(
    modifier: Modifier,
    hideAppBar: Boolean,
    title: @Composable () -> Unit,
    onOpenDrawer: (() -> Unit)?,
    snackbarHost: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (!LocalCompactLayout.current) {
        HermesScaffold(
            modifier = modifier,
            title = title,
            navigationIcon = onOpenDrawer?.let { NavIcon.Menu(it) },
            pinTopBar = true,
            showConnectionBanner = false,
            snackbarHost = snackbarHost,
            actions = actions,
            content = content,
        )
        return
    }

    val gestureController = LocalDrawerGestureController.current
    SideEffect { gestureController?.reconcile(true) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val feedbackHostState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalActionFeedbackHost provides feedbackHostState) {
        // LP3 task 3.6: omit the entire bar, including its insets. Keep the scaffold/content
        // at the same composition site so IME changes do not recreate the focused composer.
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets.navigationBars,
            snackbarHost = {
                snackbarHost()
                ActionFeedbackSnackbarHost(feedbackHostState)
            },
            topBar = {
                if (!hideAppBar) {
                    TopAppBar(
                        title = title,
                        navigationIcon = {
                            if (onOpenDrawer != null) {
                                HermesIconButton(
                                    imageVector = Icons.Filled.Menu,
                                    label = stringResource(R.string.content_desc_open_drawer),
                                    onClick = onOpenDrawer,
                                    modifier = Modifier.testTag("menu_button"),
                                )
                            }
                        },
                        actions = { actions() },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        scrollBehavior = scrollBehavior,
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { paddingValues ->
            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                content(paddingValues)
            }
        }
    }
}
