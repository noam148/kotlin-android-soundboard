package nl.gillz.soundboard.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import nl.gillz.soundboard.content.data.SoundDurationSingleton
import nl.gillz.soundboard.model.SoundDuration
import nl.gillz.soundboard.model.SoundFavorite
import nl.gillz.soundboard.model.SoundItem
import java.io.File

/**
 * Created by Noam on 16-5-2018.
 * SoundBoard
 */
class SoundList(private var context: Context) {

    private var soundboardPath: String = ""
    private var fileSoundboardFolder: File
    private var realm: Realm = Realm.getDefaultInstance()
    private var soundFavoriteItem = ArrayList<String>()

    init {

        // Get Duration items
        val soundDurationResult = realm.where<SoundDuration>().findAll()
        SoundDurationSingleton.INSTANCE.soundDurationList = realm.copyFromRealm(soundDurationResult)

        // Set soundboard path
        soundboardPath = "${Environment.getExternalStorageDirectory()}${File.separator}soundboard"

        // Create a folder where to place sound files
        fileSoundboardFolder = File(soundboardPath)

        // have the object build the directory structure, if needed.
        fileSoundboardFolder.mkdirs()
    }

    companion object {
        fun getFileName(soundFile: File): String {

            // Get filename
            var fileName = soundFile.name

            // Remove extension
            if (fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."))

            // return name
            return fileName
        }

        fun getDuration(soundFilePath: String): Int {
            var seconds = 0

            val soundDurationListFiltered = SoundDurationSingleton.INSTANCE.soundDurationList.filter { it.soundFilePath in soundFilePath }
            SoundDurationSingleton.INSTANCE.counter++

            // Check record exists. If exists get from database else get from file and store to db
            if (soundDurationListFiltered.isNotEmpty()) {
                seconds = soundDurationListFiltered[0].soundDuration;
            } else {

                // Get time and add to db
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(soundFilePath)
                val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val millSecond = Integer.parseInt(durationStr)
                seconds = (millSecond / 1000)
                mmr.release()

                // Add data to realm db
                var realm: Realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->

                    // Add a duration
                    val soundDuration = realm.createObject<SoundDuration>()
                    soundDuration.soundFilePath = soundFilePath
                    soundDuration.soundDuration = seconds
                }
                realm.close()
            }

            if (seconds == 0) {
                seconds = 1
            }
            return seconds
        }
    }

    /**
     * Return list of all sound items
     */
    fun getSoundItems(): ArrayList<SoundItem> {

        val soundItemList = ArrayList<SoundItem>()

        // Add Favorite section
        soundItemList.add(SoundItem(true, "Favorites", fileSoundboardFolder, 0, false, false))

        // Set Favorite items soundFavoriteItem
        for (soundFavorite in realm.where<SoundFavorite>().findAll()) {

            // Create a file
            val soundFile = File(soundFavorite.soundFilePath)

            // Check file exists
            if (soundFile.exists()) {
                soundItemList.add(SoundItem(false, getFileName(soundFile), soundFile, getDuration(soundFile.absolutePath), true, true))

                // Add to List array
                soundFavoriteItem.add(soundFavorite.soundFilePath)
            }
        }

        // Read folders
        fileSoundboardFolder.walkTopDown().forEach {

            if (it.isFile) {
                soundItemList.add(SoundItem(false, getFileName(it), it, getDuration(it.absolutePath), soundFavoriteItem.contains(it.absolutePath), false))
            } else if (fileSoundboardFolder.absolutePath != it.absolutePath) {
                soundItemList.add(SoundItem(true, it.name, it, 0, false, false))
            }
        }

        // Close realm
        realm.close()

        // Return sound item list
        return soundItemList
    }
}