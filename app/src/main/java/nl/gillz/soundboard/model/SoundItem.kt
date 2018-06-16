package nl.gillz.soundboard.model

import java.io.File

data class SoundItem(
        val isSection: Boolean,
        val title: String,
        val file: File,
        val duration: Int,
        var isFavorite: Boolean,
        val isInFavoriteSection: Boolean
)