package nl.gillz.soundboard.model

import java.io.File

data class SoundItem(
    val isSection: Boolean,
    val title: String,
    val length: Int,
    val path: File
)