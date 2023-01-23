package com.edgetag.providers.blotoutcloud.repository.impl

import android.content.SharedPreferences
import com.edgetag.providers.blotoutcloud.repository.data.SharedPreferenceSecureVault

class SharedPreferenceSecureVaultImpl(private val preference:SharedPreferences) :
    SharedPreferenceSecureVault {

    override fun storeString(key: String, value: String?) {
        preference.edit().putString(key,value).apply()
    }

    override fun fetchString(key: String): String {
        val data = preference.getString(key,"")
        data!!.let { return it }
    }

    override fun storeLong(key: String, value: Long) {
        preference.edit().putLong(key,value).apply()
    }

    override fun fetchLong(key: String): Long {
        val data = preference.getLong(key,0)
        data.let { return it }
    }

    override fun storeBoolean(key: String, value: Boolean) {
        preference.edit().putBoolean(key,value).apply()
    }

    override fun fetchBoolean(key: String): Boolean {
        val data = preference.getBoolean(key,true)
        data.let { return it }
    }
}