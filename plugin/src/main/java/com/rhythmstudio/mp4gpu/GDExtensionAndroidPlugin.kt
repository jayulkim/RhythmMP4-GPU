package com.rhythmstudio.mp4gpu

import android.util.Log
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GDExtensionAndroidPlugin(
    godot: Godot
) : GodotPlugin(godot) {

    companion object {
        private val TAG =
            GDExtensionAndroidPlugin::class.java.simpleName

        init {
            try {
                Log.v(
                    TAG,
                    "Loading ${BuildConfig.GODOT_PLUGIN_NAME}"
                )
                System.loadLibrary(
                    BuildConfig.GODOT_PLUGIN_NAME
                )
            } catch (e: UnsatisfiedLinkError) {
                Log.e(
                    TAG,
                    "Unable to load native library",
                    e
                )
            }
        }
    }

    override fun getPluginName(): String =
        BuildConfig.GODOT_PLUGIN_NAME

    override fun getPluginGDExtensionLibrariesPaths() =
        setOf(
            "res://addons/${BuildConfig.GODOT_PLUGIN_NAME}/plugin.gdextension"
        )

    @UsedByGodot
    external fun helloWorld()
}
