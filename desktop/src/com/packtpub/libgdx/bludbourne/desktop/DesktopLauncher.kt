package com.packtpub.libgdx.bludbourne.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.packtpub.libgdx.bludbourne.BludBourne

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()

        config.title = "BludBourne"
        config.useGL30 = false
        config.width = 800
        config.height = 600

        Gdx.app = LwjglApplication(BludBourne(), config)
        Gdx.app.logLevel = Application.LOG_DEBUG

    }
}
