package nl.gillz.soundboard.util

/**
 * Created by Noam on 9-6-2018.
 * SoundBoard
 */
interface SoundFavoriteInterface {
    fun onSoundFileFavoriteUpdate(soundFilePath: String, isFavorite: Boolean)
}