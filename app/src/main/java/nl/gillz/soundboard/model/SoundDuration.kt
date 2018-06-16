package nl.gillz.soundboard.model

import io.realm.RealmObject

open class SoundDuration : RealmObject() {
    var soundFilePath: String = ""
    var soundDuration: Int = 0
}