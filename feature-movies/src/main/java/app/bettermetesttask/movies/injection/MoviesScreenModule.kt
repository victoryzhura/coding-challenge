package app.bettermetesttask.movies.injection

import app.bettermetesttask.movies.navigation.MoviesCoordinator
import app.bettermetesttask.movies.navigation.MoviesCoordinatorImpl
import app.bettermetesttask.movies.navigation.MoviesNavigator
import app.bettermetesttask.movies.navigation.MoviesNavigatorImpl
import dagger.Module
import dagger.Provides

@Module
class MoviesScreenModule {

    @Provides
    fun bindNavigator(navigatorImpl: MoviesNavigatorImpl): MoviesNavigator {
        return navigatorImpl
    }

    @Provides
    fun bindCoordinator(coordinatorImpl: MoviesCoordinatorImpl): MoviesCoordinator {
        return coordinatorImpl
    }
}