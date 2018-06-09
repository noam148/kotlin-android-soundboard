package nl.gillz.soundboard.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
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




class MainActivity : AppCompatActivity() {

    private lateinit var listViewSound: ListView
    private lateinit var editTextSearch: EditText
    lateinit var mp: MediaPlayer
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        editTextSearch = findViewById(R.id.edit_text_search)
        listViewSound = findViewById(R.id.list_view_sound)

        val soundList = SoundList(this).getSoundItems()
        val adapter = SoundAdapter(this, soundList)
        listViewSound.adapter = adapter
        listViewSound.isTextFilterEnabled = true

        val context = this
        listViewSound.setOnItemClickListener { _, _, position, _ ->
            val selectedSound = adapter.dataSource[position]

            if (!selectedSound.isSection) {
                audioPlayer(selectedSound.path)
            }
        }

        // filter on text change
        editTextSearch.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        mp = MediaPlayer()
        mp.setOnCompletionListener { mp -> mp.release() }
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

    fun audioPlayer(soundFile: File) {
//        val mp1 = MediaPlayer.create(this, Uri.fromFile(soundFile))
//        mp1.start()
//        mp1.setOnCompletionListener(OnCompletionListener { mp -> mp.release() })


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
