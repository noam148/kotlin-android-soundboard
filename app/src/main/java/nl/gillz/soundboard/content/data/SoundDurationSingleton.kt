package nl.gillz.soundboard.content.data

import nl.gillz.soundboard.model.SoundDuration

/**
 * Created by Noam on 17-6-2018.
 * SoundBoard
 */


class SoundDurationSingleton private constructor() {

    lateinit var soundDurationList: List<SoundDuration>
    var counter: Int = 0


    init {
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: SoundDurationSingleton

        init {
            SoundDurationSingleton()
        }
    }
}