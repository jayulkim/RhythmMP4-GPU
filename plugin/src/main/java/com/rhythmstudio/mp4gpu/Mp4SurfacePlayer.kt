package com.rhythmstudio.mp4gpu

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.Log
import android.view.Surface

internal class Mp4SurfacePlayer {

    companion object {
        private const val TAG = "RhythmMP4GPU"
    }

    private var player: MediaPlayer? = null
    private var videoSurface: Surface? = null
    private var videoTexture: SurfaceTexture? = null

    fun open(
        filePath: String,
        textureId: Int,
        width: Int,
        height: Int
    ): Boolean {
        close()

        if (textureId <= 0) {
            Log.e(TAG, "Invalid external texture ID")
            return false
        }

        return try {
            videoTexture = SurfaceTexture(textureId).apply {
                setDefaultBufferSize(
                    width.coerceAtLeast(1),
                    height.coerceAtLeast(1)
                )
            }

            videoSurface = Surface(videoTexture)

            player = MediaPlayer().apply {
                setDataSource(filePath)
                setSurface(videoSurface)

                setOnPreparedListener {
                    it.start()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(
                        TAG,
                        "MediaPlayer error: $what / $extra"
                    )
                    true
                }

                prepareAsync()
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Unable to open MP4", e)
            close()
            false
        }
    }

    fun getSurfaceTexture(): SurfaceTexture? {
        return videoTexture
    }

    fun positionMs(): Int {
        return try {
            player?.currentPosition ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }

    fun pause() {
        player?.let {
            if (it.isPlaying) it.pause()
        }
    }

    fun resume() {
        player?.start()
    }

    fun close() {
        player?.let {
            try {
                it.release()
            } catch (_: Exception) {
            }
        }

        player = null
        videoSurface?.release()
        videoSurface = null
        videoTexture?.release()
        videoTexture = null
    }
}
