package app.bettermetesttask.movies.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.ObserveMoviesUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    private val observeMoviesUseCase: ObserveMoviesUseCase,
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase,
) : ViewModel() {

    private val moviesMutableFlow: MutableStateFlow<MoviesState> =
        MutableStateFlow(MoviesState.Initial)

    val moviesStateFlow: StateFlow<MoviesState>
        get() = moviesMutableFlow.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            observeMoviesUseCase()
                .collect { result ->
                    if (result is Result.Success) {
                        moviesMutableFlow.emit(MoviesState.Loaded(movies = result.data))
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
        }
    }

    fun onSearchQueryChanged(searchText: String) {
        moviesMutableFlow.update { movieState ->
            if (movieState is MoviesState.Loaded) {
                movieState.copy(
                    searchText = searchText,
                    filteredMovies = movieState.movies.filter { movie ->
                        movie.title.lowercase().trim().contains(searchText.lowercase().trim())
                    })
            } else movieState
        }
    }


    fun openMovieDetails(movie: Movie) {
        // TODO: todo todo todo todo
    }
}