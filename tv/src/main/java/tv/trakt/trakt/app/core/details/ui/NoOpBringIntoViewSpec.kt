package tv.trakt.trakt.app.core.details.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec

/**
 * Suppresses focus-driven scrolling. Provided through LocalBringIntoViewSpec
 * until the initial focus has landed, so details screens stay scrolled to the
 * top when the action buttons row receives the first focus.
 */
@OptIn(ExperimentalFoundationApi::class)
internal val NoOpBringIntoViewSpec: BringIntoViewSpec = object : BringIntoViewSpec {
    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float,
    ) = 0F
}
