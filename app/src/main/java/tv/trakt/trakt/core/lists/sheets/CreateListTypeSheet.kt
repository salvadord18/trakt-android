@file:OptIn(ExperimentalMaterial3Api::class)

package tv.trakt.trakt.core.lists.sheets

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tv.trakt.trakt.core.lists.sheets.createtype.CreateListTypeView
import tv.trakt.trakt.ui.components.TraktBottomSheet

@Composable
internal fun CreateListTypeSheet(
    state: SheetState = rememberBottomSheetState(
        initialValue = Hidden,
        enabledValues = setOf(Hidden, Expanded),
    ),
    active: Boolean,
    onCreateList: () -> Unit,
    onCreateSmartList: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetScope = rememberCoroutineScope()

    fun hideThen(action: () -> Unit) {
        sheetScope
            .launch { state.hide() }
            .invokeOnCompletion {
                if (!state.isVisible) {
                    onDismiss()
                    action()
                }
            }
    }

    if (active) {
        TraktBottomSheet(
            sheetState = state,
            onDismiss = onDismiss,
        ) {
            CreateListTypeView(
                onCreateList = { hideThen(onCreateList) },
                onCreateSmartList = { hideThen(onCreateSmartList) },
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp)
                    .padding(horizontal = 24.dp),
            )
        }
    }
}
