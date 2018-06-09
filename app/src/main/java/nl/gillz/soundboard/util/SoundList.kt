package nl.gillz.soundboard.util

import android.content.Context
import android.os.Environment
import nl.gillz.soundboard.model.SoundItem
import java.io.File
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import nl.gillz.soundboard.model.SoundFavorite

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

        // Open the realm for the UI thread.

        // Set soundboard path
        soundboardPath = "${Environment.getExternalStorageDirectory()}${File.separator}soundboard"

        // Create a folder where to place sound files
        fileSoundboardFolder = File(soundboardPath)

        // have the object build the directory structure, if needed.
        fileSoundboardFolder.mkdirs()
    }

    companion object{
        fun getFileName(soundFile: File): String{

            // Get filename
            var fileName = soundFile.name;

            // Remove extension
            if (fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."))

            // return name
            return fileName
        }

        fun getDuration(soundFile: File, context: Context): Int{

            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, Uri.fromFile(soundFile))
            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val millSecond = Integer.parseInt(durationStr)
            var seconds = (millSecond/1000)
            if(seconds == 0){
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
        soundItemList.add(SoundItem(true, "Favorites",0, fileSoundboardFolder, false, false))

        // Set Favorite items soundFavoriteItem
        for (soundFavorite in realm.where<SoundFavorite>().findAll()) {

            // Create a file
            val soundFile = File(soundFavorite.soundFilePath)

            // Check file exists
            if(soundFile.exists()){
                soundItemList.add(SoundItem(false, getFileName(soundFile),getDuration(soundFile, context), soundFile, true, true))

                // Add to List array
                soundFavoriteItem.add(soundFavorite.soundFilePath)
            }
        }

        // Read folders
        fileSoundboardFolder.walkTopDown().forEach {

            if (it.isFile){
                soundItemList.add(SoundItem(false, getFileName(it),getDuration(it, context), it, soundFavoriteItem.contains(it.absolutePath),false))
            } else if (fileSoundboardFolder.absolutePath != it.absolutePath) {
                soundItemList.add(SoundItem(true, it.name,0, it, false, false))
            }
        }

        // Close realm
        realm.close()

        // Return sound item list
        return soundItemList
    }
}