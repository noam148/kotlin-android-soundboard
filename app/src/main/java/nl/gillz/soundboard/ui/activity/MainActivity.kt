package nl.gillz.soundboard.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.ListView
import nl.gillz.soundboard.R
import nl.gillz.soundboard.content.adapter.SoundAdapter
import nl.gillz.soundboard.util.SoundList
import java.io.File
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import nl.gillz.soundboard.model.SoundItem
import nl.gillz.soundboard.util.SoundFavoriteInterface

class MainActivity : AppCompatActivity(), SoundFavoriteInterface {

    private lateinit var listViewSound: ListView
    private lateinit var editTextSearch: EditText
    private lateinit var soundItemList: ArrayList<SoundItem>
    private lateinit var soundAdapter: SoundAdapter
    lateinit var mp: MediaPlayer
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        editTextSearch = findViewById(R.id.edit_text_search)
        listViewSound = findViewById(R.id.list_view_sound)

        initSoundListView()

        mp = MediaPlayer()
        mp.setOnCompletionListener { mp -> mp.release() }
    }

    /**
     * If sound file is updated
     */
    override fun onSoundFileFavoriteUpdate(soundFilePath: String, isFavorite: Boolean) {

        // Index that need to remove
        var soundItemIndexRemove: Int = -1

        // Loop through all items and update
        soundItemList.forEachIndexed { index, soundItem ->

            // Check if equals to absolute path
            if (soundItem.file.absolutePath == soundFilePath) {
                if (isFavorite) {
                    // Set favorite to true
                    soundItemList.get(index).isFavorite = true
                } else {
                    // Remove under Favorite section
                    if (soundItem.isInFavoriteSection) {
                        soundItemIndexRemove = index
                    } else {
                        soundItemList.get(index).isFavorite = false
                    }
                }
            }
        }

        // If is favorite add item under favorite section
        if(isFavorite){
            val soundItemFile = File(soundFilePath)
            soundItemList.add(1,SoundItem(false, SoundList.getFileName(soundItemFile), soundItemFile, SoundList.getDuration(soundItemFile.absolutePath), true, true))

        // If remove index isset? remove item
        } else if(soundItemIndexRemove != -1){
            soundItemList.removeAt(soundItemIndexRemove);
        }

        // Set change
        soundAdapter.notifyDataSetChanged()

        // update filter
        soundAdapter.filter.filter(editTextSearch.text)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    setupPermissions()
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        }
    }

    fun initSoundListView() {

        soundItemList = SoundList(this).getSoundItems()

        soundAdapter = SoundAdapter(this,this, soundItemList)
        listViewSound.adapter = soundAdapter
        listViewSound.isTextFilterEnabled = true

        val context = this
        listViewSound.setOnItemClickListener { _, _, position, _ ->
            val selectedSound = soundAdapter.dataSource[position]

            if (!selectedSound.isSection) {
                audioPlayer(selectedSound.file)
            }
        }

        // filter on text change
        editTextSearch.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                soundAdapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun audioPlayer(soundFile: File) {

        if (mp.isPlaying) {
            mp.stop()
            mp.reset()
        }

        mp = MediaPlayer.create(this, Uri.fromFile(soundFile))
        mp.start()

        mp.setOnCompletionListener({ mp ->
            mp.stop()
            mp.reset()
        })
    }
}
