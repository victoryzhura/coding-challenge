package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.database.entities.MovieEntity
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class MoviesRepositoryTest {

    private val localStore: MoviesLocalStore = mock()
    private val restStore: MoviesRestStore = mock()
    private val mapper: MoviesMapper = mock()

    private lateinit var repository: MoviesRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = MoviesRepositoryImpl(localStore, restStore, mapper)
    }

    @AfterEach
    fun verifyNoAnymoreInteractions() {
        verifyNoMoreInteractions(restStore, localStore)
    }

    @Test
    fun `getRemoteMovies should return remote movies on success`() = runTest {
        val expected = listOf(Movie(1, "Title", "Description", null))
        whenever(restStore.getMovies()).thenReturn(expected)

        val result = repository.getRemoteMovies()
        assertEquals(expected, (result as Result.Success).data)

        verify(restStore).getMovies()
    }

    @Test
    fun `getRemoteMovies should return error when API fails`() = runTest {
        val expected = RuntimeException("API Error")
        whenever(restStore.getMovies()).thenThrow(expected)

        val result = repository.getRemoteMovies()

        assertEquals(expected, (result as Result.Error).error)

        verify(restStore).getMovies()
    }

    @Test
    fun `getLocalMovies should return success result when local store works correctly`() = runTest {
        val localMovies = listOf(MovieEntity(1, "Title", "Description", null))
        val expected = listOf(Movie(1, "Title", "Description", null))

        whenever(localStore.getMovies()).thenReturn(localMovies)
        whenever(mapper.mapFromLocal(localMovies.first())).thenReturn(expected.first())

        val result = repository.getLocalMovies()

        assertEquals(expected, (result as Result.Success).data)
        verify(localStore).getMovies()
    }

    @Test
    fun `getLocalMovies should return error result when local store fails`() = runTest {
        val expected = RuntimeException("Local store Error")

        whenever(localStore.getMovies()).thenThrow(expected)

        val result = repository.getLocalMovies()

        assertEquals(expected, (result as Result.Error).error)
        verify(localStore).getMovies()
    }

    @Test
    fun `getMovie should return movie when found in local store`() = runTest {
        val localMovie = MovieEntity(1, "Title", "Description", null)
        val expected = Movie(1, "Title", "Description", null)

        whenever(localStore.getMovie(1)).thenReturn(localMovie)
        whenever(mapper.mapFromLocal(localMovie)).thenReturn(expected)

        val result = repository.getMovie(1)

        assertEquals(expected, (result as Result.Success).data)
        verify(localStore).getMovie(1)
    }

    @Test
    fun `getMovie should return error when movie not found`() = runTest {
        val expected = NoSuchElementException("Movie not found")

        whenever(localStore.getMovie(1)).thenThrow(expected)

        val result = repository.getMovie(1)

        assertEquals(expected, (result as Result.Error).error)

        verify(localStore).getMovie(1)
    }

    @Test
    fun `addMovies should store movies locally and return success result`() = runTest {
        val movies = listOf(
            Movie(1, "Title", "Description", null),
            Movie(2, "Title2", "Description2", null)
        )

        val localMovies = listOf(
            MovieEntity(1, "Title", "Description", null),
            MovieEntity(2, "Title2", "Description2", null)
        )

        val expected = Unit

        whenever(mapper.mapToLocal(movies[0])).thenReturn(localMovies[0])
        whenever(mapper.mapToLocal(movies[1])).thenReturn(localMovies[1])

        val result = repository.addMovies(movies)

        assertEquals(expected, (result as Result.Success).data)
        verify(localStore).insertMovies(localMovies)
    }

    @Test
    fun `addMovies should return error result when local store fails`() = runTest {
        val expected = RuntimeException("Failed to insert movies")

        val localMovies = listOf(MovieEntity(1, "Title", "Description", null))
        val movies = listOf(Movie(1, "Title", "Description", null))

        whenever(mapper.mapToLocal(movies[0])).thenReturn(localMovies[0])
        whenever(localStore.insertMovies(localMovies)).thenThrow(expected)

        val result = repository.addMovies(movies)

        assertEquals(expected, (result as Result.Error).error)
        verify(localStore).insertMovies(localMovies)
    }

    @Test
    fun `observeLikedMovieIds should return flow of liked movie ids`() = runTest {
        val expected = flowOf(listOf(1, 2, 3))

        whenever(localStore.observeLikedMoviesIds()).thenReturn(expected)

        val result = repository.observeLikedMovieIds()

        assertEquals(expected, result)

        verify(localStore).observeLikedMoviesIds()
    }

    @Test
    fun `getLikedById should return id of liked movie`() = runTest {
        val movieId = 1
        val expected = 1

        whenever(localStore.getLikedById(movieId)).thenReturn(movieId)

        val result = repository.getLikedById(movieId)

        assertEquals(expected, result)
        verify(localStore).getLikedById(movieId)
    }

    @Test
    fun `addMovieToFavorites should call localStore likeMovie`() = runTest {
        val expected = 1

        repository.addMovieToFavorites(expected)

        verify(localStore).likeMovie(expected)
    }

    @Test
    fun `removeMovieFromFavorites should call localStore dislikeMovie`() = runTest {
        val expected = 1

        repository.removeMovieFromFavorites(expected)

        verify(localStore).dislikeMovie(expected)
    }
}