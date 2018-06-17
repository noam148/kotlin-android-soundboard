package nl.gillz.soundboard

/**
 * Created by Noam on 9-6-2018.
 * SoundBoard
 */

import android.app.Application

import io.realm.Realm
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.Crashlytics



class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this)

        val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)           // Enables Crashlytics debugger
                .build()
        Fabric.with(fabric)
    }
}