package nl.gillz.soundboard

/**
 * Created by Noam on 9-6-2018.
 * SoundBoard
 */

import android.app.Application

import io.realm.Realm

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this)
    }
}