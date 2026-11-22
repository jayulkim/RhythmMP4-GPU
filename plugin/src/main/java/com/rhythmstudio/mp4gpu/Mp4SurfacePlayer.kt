package com.rhythmstudio.mp4gpu

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.Log
import android.view.Surface
import java.util.concurrent.atomic.AtomicBoolean

internal class Mp4SurfacePlayer {

    companion object {
        private const val TAG = "RhythmMP4GPU"
    }

    private var player: MediaPlayer? = null
    private var videoSurface: Surface? = null
    private var videoTexture: SurfaceTexture? = null

    private val frameReady = AtomicBoolean(false)

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

                setOnFrameAvailableListener {
                    frameReady.set(true)
                }
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

    fun updateFrameOnRenderThread(): Boolean {
        val texture = videoTexture ?: return false

        if (!frameReady.getAndSet(false)) {
            return false
        }

        return try {
            texture.updateTexImage()
            true
        } catch (e: RuntimeException) {
            Log.e(TAG, "MP4 texture update failed", e)
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
        frameReady.set(false)

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
