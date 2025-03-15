package app.bettermetesttask.movies.navigation

import javax.inject.Inject

interface MoviesCoordinator {

    fun navigateToDetail(movieId: Int)

    fun navigateBack()
}

class MoviesCoordinatorImpl @Inject constructor(
    private val navigator: MoviesNavigator
) : MoviesCoordinator {

    override fun navigateToDetail(movieId: Int) {
        navigator.navigateToDetail(movieId)
    }

    override fun navigateBack() {
        navigator.navigateBack()
    }
}