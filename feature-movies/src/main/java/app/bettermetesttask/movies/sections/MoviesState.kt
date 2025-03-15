package app.bettermetesttask.movies.sections

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MoviesState {

    data object Initial : MoviesState()

    data object Loading : MoviesState()

    data class Loaded(
        val movies: List<Movie>,
        val searchText: String = "",
        val filteredMovies: List<Movie> = emptyList()
    ) : MoviesState()
}