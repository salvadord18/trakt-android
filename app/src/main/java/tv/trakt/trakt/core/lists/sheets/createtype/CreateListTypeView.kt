package tv.trakt.trakt.core.lists.sheets.createtype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tv.trakt.trakt.common.helpers.extensions.onClick
import tv.trakt.trakt.resources.R
import tv.trakt.trakt.ui.theme.TraktTheme

@Composable
internal fun CreateListTypeView(
    modifier: Modifier = Modifier,
    onCreateList: () -> Unit = {},
    onCreateSmartList: () -> Unit = {},
) {
    Column(
        verticalArrangement = spacedBy(24.dp),
        modifier = modifier,
    ) {
        CreateListTypeRow(
            icon = R.drawable.ic_bolt,
            iconSize = 24.dp,
            title = stringResource(R.string.button_text_smart_lists),
            description = stringResource(R.string.text_cta_smart_lists),
            onClick = onCreateSmartList,
        )
        CreateListTypeRow(
            icon = R.drawable.ic_person_trakt,
            iconSize = 20.dp,
            title = stringResource(R.string.button_text_personal),
            description = stringResource(R.string.text_cta_personal_lists),
            onClick = onCreateList,
        )
    }
}

@Composable
private fun CreateListTypeRow(
    icon: Int,
    iconSize: Dp,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .onClick(onClick = onClick),
    ) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = when (TraktTheme.colors.isLight) {
                        true -> TraktTheme.colors.placeholderContainer
                        false -> TraktTheme.colors.chipContainer
                    },
                    shape = CircleShape,
                ),
        ) {
            Icon(
                painter = painterResource(icon),
                tint = TraktTheme.colors.textSecondary,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }

        Column(
            verticalArrangement = spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = TraktTheme.colors.textPrimary,
                style = TraktTheme.typography.heading5,
            )
            Text(
                text = description,
                color = TraktTheme.colors.textSecondary,
                style = TraktTheme.typography.paragraphSmaller.copy(
                    fontSize = 12.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun CreateListTypeViewPreview() {
    TraktTheme {
        CreateListTypeView()
    }
}
