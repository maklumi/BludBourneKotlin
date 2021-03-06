package com.packtpub.libgdx.bludbourne.profile

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Base64Coder
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.ObjectMap
import java.util.*

class ProfileManager private constructor() : ProfileSubject() {
    private val _json = Json()
    private var _profiles = Hashtable<String, FileHandle>()
    private var _profileProperties = ObjectMap<String, Any>()
    private var _profileName: String = DEFAULT_PROFILE
    var isNewProfile = false

    init {
        _profiles.clear()
        storeAllProfiles()
    }

    fun getProfileList(): Array<String> {
        val profiles = Array<String>()
        val iter = _profiles.keys.iterator()
        while (iter.hasNext()) {
            profiles.add(iter.next())
        }
        return profiles
    }


    fun getProfileFile(profileName: String): FileHandle? {
        if (!doesProfileExist(profileName)) return null
        return _profiles[profileName]!!
    }

    fun doesProfileExist(profileName: String): Boolean = _profiles.containsKey(profileName)

    fun setCurrentProfile(profileName: String) = if (doesProfileExist(profileName)) {
        _profileName = profileName
    } else {
        _profileName = DEFAULT_PROFILE
    }

    fun storeAllProfiles() {
        if (Gdx.files.isLocalStorageAvailable) {
            val files = Gdx.files.local(".").list(SAVEGAME_SUFFIX)

            for (file in files) {
                _profiles.put(file.nameWithoutExtension(), file)
            }
        } else {
            //TODO: try external directory here
            return
        }
    }

    fun writeProfileToStorage(profileName: String, fileData: String, overwrite: Boolean) {
        val fullFilename = profileName + SAVEGAME_SUFFIX

        val localFileExists = Gdx.files.local(fullFilename).exists()

        //If we cannot overwrite and the file exists, exit
        if (localFileExists && !overwrite) {
            return
        }

        var file: FileHandle? = null

        if (Gdx.files.isLocalStorageAvailable) {
            file = Gdx.files.local(fullFilename)
            val encodedString = Base64Coder.encodeString(fileData)
            file!!.writeString(encodedString, !overwrite)
        }

        _profiles.put(profileName, file!!)
    }

    fun setProperty(key: String, `object`: Any) {
        _profileProperties.put(key, `object`)
    }

    fun <T : Any> getProperty(key: String, type: Class<T>): T? {
        var property: T? = null
        if (!_profileProperties.containsKey(key)) {
//            return Array<Any>() as T
            return property
        }
        property = _profileProperties.get(key) as T
        return property
    }

    fun saveProfile() {
        notify(this, ProfileObserver.ProfileEvent.SAVING_PROFILE)
        val text = _json.prettyPrint(_json.toJson(_profileProperties))
        writeProfileToStorage(_profileName, text, true)
//        println(text)
    }

    fun loadProfile() {
        if (isNewProfile) {
            notify(this, ProfileObserver.ProfileEvent.CLEAR_CURRENT_PROFILE)
            saveProfile()
        }
        val fullProfileFileName = _profileName + SAVEGAME_SUFFIX
        val doesProfileFileExist = Gdx.files.local(fullProfileFileName).exists()

        if (!doesProfileFileExist) {
            //Gdx.app.debug(TAG, "File doesn't exist!")
            return
        }

        val encodedFile = _profiles[_profileName] as FileHandle
        val s = encodedFile.readString()
        val decodedFile = Base64Coder.decodeString(s)

        _profileProperties = _json.fromJson(ObjectMap::class.java, decodedFile) as ObjectMap<String, Any>

        notify(this, ProfileObserver.ProfileEvent.PROFILE_LOADED)
        isNewProfile = false
    }

    companion object {
        private var _profileManager: ProfileManager? = null

        private val SAVEGAME_SUFFIX = ".sav"
        val DEFAULT_PROFILE = "default"

        val instance: ProfileManager
            get() {
                if (_profileManager == null) {
                    _profileManager = ProfileManager()
                }
                return _profileManager!!
            }
    }

}
