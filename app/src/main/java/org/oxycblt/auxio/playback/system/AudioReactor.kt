package org.oxycblt.auxio.playback.system

import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioManager
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * Object that manages the AudioFocus state.
 * Adapted from NewPipe (https://github.com/TeamNewPipe/NewPipe)
 * @author OxygenCobalt
 */
class AudioReactor(
    context: Context,
    private val player: SimpleExoPlayer
) : AudioManager.OnAudioFocusChangeListener {
    private val audioManager = ContextCompat.getSystemService(
        context, AudioManager::class.java
    ) ?: error("Cannot obtain AudioManager.")

    private val settingsManager = SettingsManager.getInstance()
    private val playbackManager = PlaybackStateManager.getInstance()

    private val request = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
        .setWillPauseWhenDucked(true)
        .setOnAudioFocusChangeListener(this)
        .build()

    private var pauseWasTransient = false

    /**
     * Request the android system for audio focus
     */
    fun requestFocus() {
        AudioManagerCompat.requestAudioFocus(audioManager, request)
    }

    /**
     * Abandon the current focus request, functionally "Destroying it".
     */
    fun release() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, request)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (!settingsManager.doAudioFocus) {
            // Dont do audio focus if its not enabled
            return
        }

        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> onGain()
            AudioManager.AUDIOFOCUS_LOSS -> onLossPermanent()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> onLossTransient()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> onDuck()
        }
    }

    private fun onGain() {
        if (player.volume == VOLUME_DUCK && playbackManager.isPlaying) {
            unduck()
        } else if (pauseWasTransient) {
            // Play again if the pause was only temporary [AudioManager.AUDIOFOCUS_LOSS_TRANSIENT]
            playbackManager.setPlaying(true)
            pauseWasTransient = false
        }
    }

    private fun onLossTransient() {
        pauseWasTransient = true
        playbackManager.setPlaying(false)
    }

    private fun onLossPermanent() {
        playbackManager.setPlaying(false)
    }

    private fun onDuck() {
        player.volume = VOLUME_DUCK
    }

    private fun unduck() {
        player.volume = VOLUME_DUCK

        ValueAnimator().apply {
            setFloatValues(VOLUME_DUCK, VOLUME_FULL)
            duration = DUCK_DURATION
            addListener(
                onStart = { player.volume = VOLUME_DUCK },
                onCancel = { player.volume = VOLUME_FULL },
                onEnd = { player.volume = VOLUME_FULL }
            )
            addUpdateListener {
                player.volume = animatedValue as Float
            }
            start()
        }
    }

    companion object {
        private const val VOLUME_DUCK = 0.2f
        private const val DUCK_DURATION = 1500L
        private const val VOLUME_FULL = 1.0f
    }
}
