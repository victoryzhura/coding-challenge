package app.bettermetesttask.movies.navigation

import android.os.Bundle
import androidx.navigation.NavController
import app.bettermetesttask.movies.R
import dagger.Lazy
import javax.inject.Inject

interface MoviesNavigator {
    fun navigateToDetail(movieId: Int)

    fun navigateBack()
}

class MoviesNavigatorImpl @Inject constructor(
    private val navController: Lazy<NavController>,
) : MoviesNavigator {

    override fun navigateToDetail(movieId: Int) {
        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }
        navController.get().navigate(R.id.action_moviesFragmentCompose_to_detailMovieFragmentCompose, bundle)
    }

    override fun navigateBack() {
        navController.get().popBackStack()
    }
}