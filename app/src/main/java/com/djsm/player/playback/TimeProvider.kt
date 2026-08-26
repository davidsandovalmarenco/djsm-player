package com.djsm.player.playback

import android.os.SystemClock
import javax.inject.Inject

interface TimeProvider {
    fun elapsedRealtime(): Long
}

class DefaultTimeProvider @Inject constructor() : TimeProvider {
    override fun elapsedRealtime(): Long = SystemClock.elapsedRealtime()
}
