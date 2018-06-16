package nl.gillz.soundboard.util

import android.content.Context
import android.os.Environment
import nl.gillz.soundboard.model.SoundItem
import java.io.File
import android.media.MediaMetadataRetriever
import io.realm.Realm
import io.realm.kotlin.where
import nl.gillz.soundboard.model.SoundDuration
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
    private var soundDurationList: Array<*>

    init {

        // Get Duration items
        soundDurationList = realm.where<SoundDuration>().findAll().toArray() as Array<*>

//todo: remove test scripts

       // val arrayListOfUnmanagedObjects = realm.copyFromRealm(soundDurationList)

//        List<String, Int>(): test = ArrayList<String, Int>()
//        val result = routeTypes
//                .filter { it.type in filter.keys }
//                .map { it.copy(items = it.items.filter { it.id in filter[routeType.type]!! }) }


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
            var fileName = soundFile.name

            // Remove extension
            if (fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."))

            // return name
            return fileName
        }

        fun getDuration(soundFilePath: String): Int{
            var seconds = 0

            //todo: set in async or somting like that store data to table
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(soundFilePath)
            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val millSecond = Integer.parseInt(durationStr)
            seconds = (millSecond/1000)
            mmr.release()

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
        soundItemList.add(SoundItem(true, "Favorites", fileSoundboardFolder, 0, false, false))

        // Set Favorite items soundFavoriteItem
        for (soundFavorite in realm.where<SoundFavorite>().findAll()) {

            // Create a file
            val soundFile = File(soundFavorite.soundFilePath)

//todo: add duration to db if not exists
//            realm.executeTransaction { realm ->
//                soundDurationList.filter
//                if (soundDurationList.filtered("seasonNumber == $0", season.seasonNumber).length == 0) {
//                    seasonsList.push(season)
//                }
//
//                // Add a favorite
//                val soundFavorite = realm.createObject<SoundFavorite>()
//                soundFavorite.soundFilePath = soundItem.file.absolutePath
//            }

            // Check file exists
            if(soundFile.exists()){
                soundItemList.add(SoundItem(false, getFileName(soundFile), soundFile, getDuration(soundFile.absolutePath), true, true))

                // Add to List array
                soundFavoriteItem.add(soundFavorite.soundFilePath)
            }
        }

        // Read folders
        fileSoundboardFolder.walkTopDown().forEach {

            if (it.isFile){
                soundItemList.add(SoundItem(false, getFileName(it), it, getDuration(it.absolutePath), soundFavoriteItem.contains(it.absolutePath),false))
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