package com.m57.hermescontrol.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.m57.hermescontrol.R
import com.m57.hermescontrol.theme.HermesControlTheme

@Preview(name = "Below compact boundary", widthDp = 411, heightDp = 519)
@Composable
private fun CompactBoundaryPreview() {
    HermesControlTheme {
        check(LocalCompactLayout.current)
        Text(stringResource(R.string.app_name))
    }
}

@Preview(name = "At regular boundary", widthDp = 411, heightDp = 520)
@Composable
private fun RegularBoundaryPreview() {
    HermesControlTheme {
        check(!LocalCompactLayout.current)
        Text(stringResource(R.string.app_name))
    }
}
