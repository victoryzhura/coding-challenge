package app.bettermetesttask.movies.sections.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MoviePosterPlaceholder() {
    Icon(
        imageVector = Icons.Default.Image,
        contentDescription = "Movie placeholder",
        tint = Color.White
    )
}