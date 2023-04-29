/*
 * Copyright (c) 2023 Auxio Project
 * SleepTimerReceiver.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.playback.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/** @author komine Will stop playing after the set sleep time */
@AndroidEntryPoint
class SleepTimerReceiver : BroadcastReceiver() {
    @Inject lateinit var playbackManager: PlaybackStateManager
    override fun onReceive(context: Context?, intent: Intent?) {
        playbackManager.setPlaying(false)
    }
}
