package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader

object Utility {
    private val TAG = Utility::class.java.simpleName
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
        val filePathResolver = InternalFileHandleResolver()
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


}
