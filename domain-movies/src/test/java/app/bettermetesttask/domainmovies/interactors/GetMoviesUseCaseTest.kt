package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.connectivity.ConnectivityManager
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import app.bettermetesttask.domaincore.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class GetMoviesUseCaseTest {

    private val repository: MoviesRepository = mock()
    private val connectivityManager: ConnectivityManager = mock()

    private lateinit var observeMoviesUseCase: ObserveMoviesUseCase

    @BeforeEach
    fun setUp() {
        observeMoviesUseCase = ObserveMoviesUseCase(repository, connectivityManager)
    }


    @AfterEach
    fun verifyNoAnymoreInteractions() {
        verifyNoMoreInteractions(repository, connectivityManager)
    }

    @Test
    fun `invoke should return remote movies when network is available`() = runTest {
        val movies = listOf(
            Movie(1, "Title", "Description", null, false),
            Movie(2, "Title", "Description", null, false)
        )

        val updateMovies = listOf(
            Movie(1, "Title", "Description", null, true),
            Movie(2, "Title", "Description", null, false)
        )
        val likedMovieIds = flowOf(listOf(1))

        whenever(connectivityManager.isNetworkAvailable()).thenReturn(true)
        whenever(repository.getRemoteMovies()).thenReturn(Result.Success(movies))
        whenever(repository.observeLikedMovieIds()).thenReturn(likedMovieIds)

        val result = observeMoviesUseCase().first()

        assertEquals(movies.size, (result as Result.Success).data.size)
        assertEquals(true, result.data[0].liked)

        verify(connectivityManager).isNetworkAvailable()
        verify(repository).getRemoteMovies()
        verify(repository).observeLikedMovieIds()
        verify(repository).addMovies(updateMovies)
    }

    @Test
    fun `invoke should return error when network is available but remote movies fail`() = runTest {
        val error = RuntimeException("API Error")

        whenever(connectivityManager.isNetworkAvailable()).thenReturn(true)
        whenever(repository.getRemoteMovies()).thenReturn(Result.Error(error))

        val result = observeMoviesUseCase().first()

        assertEquals(error, (result as Result.Error).error)

        verify(connectivityManager).isNetworkAvailable()
        verify(repository).getRemoteMovies()
        verify(repository, never()).observeLikedMovieIds()
    }

    @Test
    fun `invoke should return local movies when network is not available`() = runTest {
        val localMovies = listOf(Movie(1, "Title", "Description", null, false))
        val likedMovieIds = flowOf(listOf(1))

        whenever(connectivityManager.isNetworkAvailable()).thenReturn(false)
        whenever(repository.getLocalMovies()).thenReturn(Result.Success(localMovies))
        whenever(repository.observeLikedMovieIds()).thenReturn(likedMovieIds)
        whenever(repository.addMovies(localMovies)).thenReturn(Result.Success(Unit))

        val result = observeMoviesUseCase().first()

        assertEquals(localMovies.size, (result as Result.Success).data.size)
        assertEquals(true, result.data[0].liked)

        verify(connectivityManager).isNetworkAvailable()
        verify(repository).getLocalMovies()
        verify(repository).observeLikedMovieIds()
        verify(repository, never()).getRemoteMovies()
        verify(repository, never()).addMovies(localMovies)
    }

}