package app.bettermetesttask.movies.sections.compose.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.GetMovieFromLocalUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import app.bettermetesttask.movies.navigation.MoviesCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailMovieViewModel @Inject constructor(
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase,
    private val getMovie: GetMovieFromLocalUseCase,
    private val coordinator: MoviesCoordinator,
) : ViewModel() {

    private val movieMutableFlow: MutableStateFlow<DetailMovieState> =
        MutableStateFlow(DetailMovieState.Initial)

    val movieStateFlow: StateFlow<DetailMovieState>
        get() = movieMutableFlow.asStateFlow()

    fun getDetailMovie(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            movieMutableFlow.emit(DetailMovieState.Loading)

            when (val movie = getMovie(movieId)) {
                is Result.Success -> {
                    movieMutableFlow.emit(DetailMovieState.Loaded(movie = movie.data))
                }

                is Result.Error -> {
                    movieMutableFlow.emit(
                        DetailMovieState.Error("No such Movie")
                    )
                }
            }
        }
    }

    fun likeMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            if (movie.liked) {
                dislikeMovieUseCase(movie.id)
            } else {
                likeMovieUseCase(movie.id)
            }

            movieMutableFlow.update { movieState ->
                if (movieState is DetailMovieState.Loaded) {
                    movieState.copy(
                        movie = movie.copy(liked = !movie.liked)
                    )
                } else movieState
            }
        }
    }

    fun navigateBack() {
        coordinator.navigateBack()
    }
}