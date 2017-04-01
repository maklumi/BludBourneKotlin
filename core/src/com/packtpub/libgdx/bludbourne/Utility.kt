package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.MusicLoader
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin

object Utility {
    private val TAG = Utility::class.java.simpleName
    private val filePathResolver = InternalFileHandleResolver()
    val STATUSUI_TEXTURE_ATLAS_PATH = "skins/statusui.atlas"
    val STATUSUI_SKIN_PATH = "skins/statusui.json"
    val ITEMS_TEXTURE_ATLAS_PATH = "skins/items.atlas"
    val ITEMS_SKIN_PATH = "skins/items.json"

    val STATUSUI_TEXTUREATLAS = TextureAtlas(STATUSUI_TEXTURE_ATLAS_PATH)
    val ITEMS_TEXTUREATLAS = TextureAtlas(ITEMS_TEXTURE_ATLAS_PATH)
    val STATUSUI_SKIN = Skin(Gdx.files.internal(STATUSUI_SKIN_PATH), STATUSUI_TEXTUREATLAS)

    val assetManager = AssetManager()

    fun unloadAsset(assetFilenamePath: String) {
        if (assetManager.isLoaded(assetFilenamePath)) {
            assetManager.unload(assetFilenamePath)
        } else {
            Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath);
        }
    }

    fun loadCompleted(): Float = assetManager.progress

    fun numberAssetsQueued(): Int = assetManager.queuedAssets

    fun updateAssetLoading(): Boolean = assetManager.update()

    fun isAssetLoaded(fileName: String): Boolean = assetManager.isLoaded(fileName)

    fun loadMapAsset(mapFilenamePath: String) {
        if (assetManager.isLoaded(mapFilenamePath)) return
        if (filePathResolver.resolve(mapFilenamePath).exists()) {
            assetManager.setLoader(TiledMap::class.java, TmxMapLoader(filePathResolver))
            assetManager.load(mapFilenamePath, TiledMap::class.java)
            assetManager.finishLoadingAsset(mapFilenamePath)
            Gdx.app.debug(TAG, "Map loaded!: " + mapFilenamePath)
        } else {
            Gdx.app.debug(TAG, "Map doesn't exist!: " + mapFilenamePath)
        }
    }


    fun getMapAsset(mapFilenamePath: String): TiledMap {
        val map: TiledMap
        if (assetManager.isLoaded(mapFilenamePath)) {
            return assetManager.get(mapFilenamePath, TiledMap::class.java)
        } else {
            Gdx.app.debug(TAG, "Map is not loaded: " + mapFilenamePath)
            return TiledMap()
        }
    }

    fun loadTextureAsset(textureFilenamePath: String) {
        if (assetManager.isLoaded(textureFilenamePath)) return
        val filePathResolver = InternalFileHandleResolver()
        if (filePathResolver.resolve(textureFilenamePath).exists()) {
            assetManager.setLoader(Texture::class.java, TextureLoader(filePathResolver))
            assetManager.load(textureFilenamePath, Texture::class.java)
            assetManager.finishLoadingAsset(textureFilenamePath)
        } else {
            Gdx.app.debug(TAG, "Texture doesn't exist!: " + textureFilenamePath)
        }
    }

    fun getTextureAsset(textureFilenamePath: String): Texture {
        return assetManager.get(textureFilenamePath, Texture::class.java)
    }


    fun loadSoundAsset(soundFilenamePath: String) {
        if (soundFilenamePath.isEmpty()) return

        if (assetManager.isLoaded(soundFilenamePath)) return

        //load asset
        if (filePathResolver.resolve(soundFilenamePath).exists()) {
            assetManager.setLoader(Sound::class.java, SoundLoader(filePathResolver))
            assetManager.load(soundFilenamePath, Sound::class.java)
            //Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(soundFilenamePath)
            Gdx.app.debug(TAG, "Sound loaded!: " + soundFilenamePath)
        } else {
            Gdx.app.debug(TAG, "Sound doesn't exist!: " + soundFilenamePath)
        }
    }

    fun getSoundAsset(soundFilenamePath: String): Sound? {
        var sound: Sound? = null

        // once the asset manager is done loading
        if (assetManager.isLoaded(soundFilenamePath)) {
            sound = assetManager.get(soundFilenamePath, Sound::class.java)
        } else {
            Gdx.app.debug(TAG, "Sound is not loaded: " + soundFilenamePath)
        }

        return sound
    }

    fun loadMusicAsset(musicFilenamePath: String) {
        if (musicFilenamePath.isEmpty()) return

        if (assetManager.isLoaded(musicFilenamePath)) return

        //load asset
        if (filePathResolver.resolve(musicFilenamePath).exists()) {
            assetManager.setLoader(Music::class.java, MusicLoader(filePathResolver))
            assetManager.load(musicFilenamePath, Music::class.java)
            //Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(musicFilenamePath)
            Gdx.app.debug(TAG, "Music loaded!: " + musicFilenamePath)
        } else {
            Gdx.app.debug(TAG, "Music doesn't exist!: " + musicFilenamePath);
        }
    }

    fun getMusicAsset(musicFilenamePath: String): Music? {
        var music: Music? = null

        // once the asset manager is done loading
        if (assetManager.isLoaded(musicFilenamePath)) {
            music = assetManager.get(musicFilenamePath, Music::class.java)
        } else {
            Gdx.app.debug(TAG, "Music is not loaded: " + musicFilenamePath)
        }

        return music
    }

}
