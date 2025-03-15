package app.bettermetesttask.movies.sections.compose.details

import app.bettermetesttask.domainmovies.entries.Movie

sealed class DetailMovieState {
    data object Initial : DetailMovieState()

    data object Loading : DetailMovieState()

    data class Loaded(val movie: Movie) : DetailMovieState()

    data class Error(val message: String?) : DetailMovieState()
}