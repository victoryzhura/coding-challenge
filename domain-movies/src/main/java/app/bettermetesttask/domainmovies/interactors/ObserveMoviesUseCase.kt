package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domaincore.utils.connectivity.ConnectivityManager
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveMoviesUseCase @Inject constructor(
    private val repository: MoviesRepository,
    private val connectivityManager: ConnectivityManager
) {

    suspend operator fun invoke(): Flow<Result<List<Movie>>> {
        val isNetworkAvailable = connectivityManager.isNetworkAvailable()
        val result =
            if (isNetworkAvailable) repository.getRemoteMovies() else repository.getLocalMovies()

        return when (result) {
            is Result.Success -> {
                repository.observeLikedMovieIds()
                    .map { likedMoviesIds ->
                        val movies = result.data.map {
                            it.copy(liked = likedMoviesIds.contains(it.id))
                        }

                        if (isNetworkAvailable) {
                            Result.of { repository.addMovies(movies) }
                        }

                        Result.Success(movies)
                    }
            }

            is Result.Error -> {
                flowOf(result)
            }
        }
    }
}