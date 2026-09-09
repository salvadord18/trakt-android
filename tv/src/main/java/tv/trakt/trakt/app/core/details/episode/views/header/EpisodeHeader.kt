package tv.trakt.trakt.app.core.details.episode.views.header

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tv.trakt.trakt.app.common.ui.chips.InfoChip
import tv.trakt.trakt.app.core.details.ui.ExternalRatingsStrip
import tv.trakt.trakt.app.core.details.ui.PosterChip
import tv.trakt.trakt.app.core.details.ui.PosterImage
import tv.trakt.trakt.app.ui.theme.TraktTheme
import tv.trakt.trakt.common.core.translations.model.MediaTranslation
import tv.trakt.trakt.common.helpers.extensions.capitalize
import tv.trakt.trakt.common.helpers.extensions.longDateTimeFormat
import tv.trakt.trakt.common.helpers.extensions.onClick
import tv.trakt.trakt.common.helpers.extensions.rememberDurationFormat
import tv.trakt.trakt.common.helpers.extensions.rememberThousandsFormat
import tv.trakt.trakt.common.helpers.extensions.toLocal
import tv.trakt.trakt.common.helpers.preview.PreviewData
import tv.trakt.trakt.common.model.Episode
import tv.trakt.trakt.common.model.ExternalRating
import tv.trakt.trakt.common.model.Images.Size.MEDIUM
import tv.trakt.trakt.common.model.Show
import tv.trakt.trakt.common.ui.theme.colors.Purple400
import tv.trakt.trakt.resources.R

@Composable
internal fun EpisodeHeader(
    show: Show,
    episode: Episode,
    episodePlays: Int,
    episodeTranslation: MediaTranslation?,
    externalRating: ExternalRating?,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onFocused: (String) -> Unit,
    onPosterUnfocused: () -> Unit,
    onBackdropFocused: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isPosterFocused by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = spacedBy(24.dp, Alignment.Start),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .height(TraktTheme.size.detailsPosterSize),
    ) {
        Box {
            PosterImage(
                posterUrl = show.images?.getPosterUrl(size = MEDIUM),
                modifier = Modifier
                    .onKeyEvent {
                        if (!isPosterFocused) {
                            return@onKeyEvent false
                        }
                        if (it.type == KeyEventType.KeyUp && it.key == Key.DirectionUp) {
                            onBackdropFocused()
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    }
                    .onFocusChanged {
                        if (it.isFocused) {
                            onFocused("poster")
                        } else {
                            onPosterUnfocused()
                        }
                        scope.launch {
                            delay(100)
                            isPosterFocused = it.isFocused
                        }
                    }
                    .focusRequester(focusRequester)
                    .onClick {
                        onPosterUnfocused()
                    },
            )

            this@Row.AnimatedVisibility(
                visible = episodePlays > 0,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        translationY = 8.5.dp.toPx()
                    },
            ) {
                PosterChip(
                    text = when {
                        episodePlays > 1 -> {
                            "${stringResource(R.string.tag_text_watched)} • $episodePlays"
                        }
                        else -> {
                            stringResource(R.string.tag_text_watched)
                        }
                    },
                    icon = painterResource(R.drawable.ic_check_double),
                )
            }
        }

        Column {
            Crossfade(
                targetState = episodeTranslation?.title,
                animationSpec = tween(250),
            ) { translation ->
                Text(
                    text = if (!translation.isNullOrBlank()) translation else episode.title,
                    color = TraktTheme.colors.textPrimary,
                    style = TraktTheme.typography.heading3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = TraktTheme.spacing.mainContentEndSpace),
                )
            }

            Text(
                text = "${show.title}  /  ${
                    stringResource(
                        R.string.text_season_episode_number,
                        episode.season,
                        episode.number,
                    )
                }",
                color = TraktTheme.colors.textPrimary,
                style = TraktTheme.typography.heading6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        end = TraktTheme.spacing.mainContentEndSpace,
                    ),
            )

            Row(
                horizontalArrangement = spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .horizontalScroll(rememberScrollState()),
            ) {
                episode.releasedAt?.let {
                    InfoChip(
                        text = it.toLocal().format(longDateTimeFormat()).capitalize(),
                        iconPainter = painterResource(R.drawable.ic_calendar),
                    )
                }

                episode.runtime?.let { runtime ->
                    InfoChip(
                        text = rememberDurationFormat(runtime.inWholeMinutes),
                    )
                }

                show.certification?.let { certification ->
                    InfoChip(text = certification)
                }
            }

            // Ratings
            Row(
                horizontalArrangement = spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .horizontalScroll(rememberScrollState()),
            ) {
                val hidden = !episode.rememberReleased()
                Row(
                    horizontalArrangement = spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val grayFilter = remember {
                        ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                setToSaturation(0F)
                            },
                        )
                    }
                    val redFilter = remember {
                        ColorFilter.tint(Purple400)
                    }

                    Image(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer {
                                translationY = -0.5.dp.toPx()
                            },
                        colorFilter = if (episode.rating.rating > 0 && !hidden) redFilter else grayFilter,
                    )
                    Text(
                        text = if (episode.rating.rating > 0 && !hidden) {
                            "${episode.rating.ratingPercent}%"
                        } else {
                            "-"
                        },
                        color = TraktTheme.colors.textPrimary,
                        style = TraktTheme.typography.ratingLabel,
                    )

                    if (episode.rating.rating > 0 && !hidden) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.Top),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = TraktTheme.colors.textSecondary,
                                modifier = Modifier.size(12.5.dp),
                            )
                            Text(
                                text = rememberThousandsFormat(episode.rating.votes),
                                color = TraktTheme.colors.textSecondary,
                                style = TraktTheme.typography.ratingLabel.copy(fontSize = 12.sp),
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = externalRating != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ExternalRatingsStrip(
                        externalRating = externalRating,
                        hidden = hidden,
                    )
                }
            }
        }
    }
}

@Preview(
    device = "id:tv_4k",
    showBackground = true,
    backgroundColor = 0xFF131517,
)
@Composable
private fun Preview() {
    TraktTheme {
        EpisodeHeader(
            show = PreviewData.show1,
            episode = PreviewData.episode1,
            episodePlays = 2,
            episodeTranslation = null,
            externalRating = ExternalRating(
                trakt = null,
                imdb = ExternalRating.ImdbRating(
                    rating = 7.9F,
                    votes = 1_267_356,
                    link = "https://www.imdb.com/title/tt1234567/",
                ),
                meta = ExternalRating.MetaRating(
                    rating = 85,
                    link = "https://www.metacritic.com/movie/some-movie",
                ),
                rotten = null,
                tmdb = null,
                mal = null,
                letterboxd = null,
            ),
            focusRequester = FocusRequester(),
            onFocused = {},
            onPosterUnfocused = {},
            onBackdropFocused = {},
        )
    }
}
