package com.rhythmstudio.mp4gpu

import android.util.Log
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GDExtensionAndroidPlugin(
    godot: Godot
) : GodotPlugin(godot) {

    companion object {
        private const val TAG = "RhythmMP4GPU"

        init {
            try {
                System.loadLibrary(
                    BuildConfig.GODOT_PLUGIN_NAME
                )
                Log.i(TAG, "Native library loaded")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Native library error", e)
            }
        }
    }

    private val mp4Player = Mp4SurfacePlayer()

    override fun getPluginName(): String =
        BuildConfig.GODOT_PLUGIN_NAME

    override fun getPluginGDExtensionLibrariesPaths() =
        setOf(
            "res://addons/${BuildConfig.GODOT_PLUGIN_NAME}/plugin.gdextension"
        )

    @UsedByGodot
    fun openMp4(
        filePath: String,
        textureId: Int,
        width: Int,
        height: Int
    ): Boolean {
        return mp4Player.open(
            filePath,
            textureId,
            width,
            height
        )
    }

    @UsedByGodot
    fun updateMp4Frame(): Boolean {
        return mp4Player.updateFrameOnRenderThread()
    }

    @UsedByGodot
    fun getPositionMs(): Int {
        return mp4Player.positionMs()
    }

    @UsedByGodot
    fun pauseMp4() {
        mp4Player.pause()
    }

    @UsedByGodot
    fun resumeMp4() {
        mp4Player.resume()
    }

    @UsedByGodot
    fun closeMp4() {
        mp4Player.close()
    }

    @UsedByGodot
    external fun helloWorld()
}
