package nl.gillz.soundboard.util

import android.content.Context
import android.os.Environment
import nl.gillz.soundboard.model.SoundItem
import java.io.File
import android.media.MediaMetadataRetriever
import android.net.Uri

/**
 * Created by Noam on 16-5-2018.
 * SoundBoard
 */
class SoundList(private var context: Context) {

    private var soundboardPath: String = ""
    private var fileSoundboardFolder: File

    init {

        // Set soundboard path
        soundboardPath = "${Environment.getExternalStorageDirectory()}${File.separator}soundboard"

        // Create a folder where to place sound files
        fileSoundboardFolder = File(soundboardPath)

        // have the object build the directory structure, if needed.
        fileSoundboardFolder.mkdirs()
    }

    /**
     * Return list of all sound items
     */
    fun getSoundItems(): ArrayList<SoundItem> {

        val soundItemList = ArrayList<SoundItem>()

        fileSoundboardFolder.walkTopDown().forEach {

            if (it.isFile){
                soundItemList.add(SoundItem(false, it.name,getDuration(it), it))
            } else if (fileSoundboardFolder.absolutePath != it.absolutePath) {
                soundItemList.add(SoundItem(true, it.name,0, it))
            }
        }

        // Return sound item list
        return soundItemList
    }

    fun getDuration(soundFile: File): Int{

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