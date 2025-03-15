package app.bettermetesttask.movies.sections.compose.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.featurecommon.injection.utils.Injectable
import app.bettermetesttask.featurecommon.injection.viewmodel.SimpleViewModelProviderFactory
import app.bettermetesttask.movies.sections.compose.components.ErrorMessage
import app.bettermetesttask.movies.sections.compose.components.LoadingPlaceholder
import app.bettermetesttask.movies.sections.compose.components.MoviePosterPlaceholder
import coil3.compose.SubcomposeAsyncImage
import javax.inject.Inject
import javax.inject.Provider

class DetailMovieComposeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: Provider<DetailMovieViewModel>

    private val viewModel by viewModels<DetailMovieViewModel> {
        SimpleViewModelProviderFactory(
            viewModelProvider
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            viewModel.getDetailMovie(it.getInt("movieId"))
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val viewState by viewModel.movieStateFlow.collectAsStateWithLifecycle()

                DetailMovieComposeScreen(
                    movieState = viewState,
                    likeMovie = { movie ->
                        viewModel.likeMovie(movie)
                    },
                    onClickBack = viewModel::navigateBack
                )
            }
        }
    }
}

@Composable
fun DetailMovieComposeScreen(
    movieState: DetailMovieState,
    likeMovie: (Movie) -> Unit,
    onClickBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (movieState) {
            DetailMovieState.Loading, DetailMovieState.Initial -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DetailMovieState.Loaded -> {
                DetailMovieItem(
                    movieState.movie,
                    onLikeClicked = { likeMovie(movieState.movie) },
                    onClickBack = onClickBack
                )
            }

            is DetailMovieState.Error -> {
                ErrorMessage()
            }
        }
    }
}

@Composable
fun DetailMovieItem(movie: Movie, onLikeClicked: () -> Unit, onClickBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box {
            IconButton(
                onClick = onClickBack,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Arrow back"
                )
            }

            SubcomposeAsyncImage(
                model = movie.posterPath,
                contentDescription = "Movie Poster",
                error = { MoviePosterPlaceholder() },
                loading = { LoadingPlaceholder() },
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onLikeClicked() }) {
                Icon(
                    imageVector = if (movie.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Button",
                    tint = if (movie.liked) Color.Red else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.title,
            fontSize = 18.sp,
            color = Color.Black,
        )

        Text(
            text = movie.description,
            fontSize = 14.sp,
            color = Color.Gray,
        )
    }
}