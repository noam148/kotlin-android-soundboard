package nl.gillz.soundboard.model

import io.realm.RealmObject

open class SoundFavorite : RealmObject() {
    var soundFilePath: String = ""
}