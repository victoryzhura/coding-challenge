package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import javax.inject.Inject

class GetMovieFromLocalUseCase @Inject constructor(
    private val repository: MoviesRepository
) {

    suspend operator fun invoke(id: Int): Result<Movie> {
        return when (val result = repository.getMovie(id)) {
            is Result.Success -> {
                val isLiked = repository.getLikedById(id) != null
                val movie = result.data.copy(liked = isLiked)
                Result.Success(movie)
            }

            is Result.Error -> result
        }
    }
}