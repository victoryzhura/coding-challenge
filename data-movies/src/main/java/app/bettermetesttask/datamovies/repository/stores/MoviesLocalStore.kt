package app.bettermetesttask.datamovies.repository.stores

import app.bettermetesttask.datamovies.database.MoviesDatabase
import app.bettermetesttask.datamovies.database.dao.MoviesDao
import app.bettermetesttask.datamovies.database.entities.LikedMovieEntity
import app.bettermetesttask.datamovies.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MoviesLocalStore @Inject constructor(
    database: MoviesDatabase
) {

    private val moviesDao: MoviesDao = database.getMoviesDao()

    suspend fun getMovies(): List<MovieEntity> {
        return moviesDao.getMovies()
    }

    suspend fun getMovie(id: Int): MovieEntity {
        return moviesDao.getMovieById(id).first()
    }

    suspend fun insertMovies(movies: List<MovieEntity>) {
        return moviesDao.insertMovies(movies)
    }

    suspend fun likeMovie(id: Int) {
        moviesDao.insertLikedEntry(LikedMovieEntity(id))
    }

    suspend fun dislikeMovie(id: Int) {
        moviesDao.removeLikedEntry(id)
    }

    fun observeLikedMoviesIds(): Flow<List<Int>> {
        return moviesDao.getLikedEntries().map { movieIdsFlow -> movieIdsFlow.map { it.movieId } }
    }

    suspend fun getLikedById(id: Int): Int? {
        return moviesDao.getLikedById(id)?.movieId
    }
}