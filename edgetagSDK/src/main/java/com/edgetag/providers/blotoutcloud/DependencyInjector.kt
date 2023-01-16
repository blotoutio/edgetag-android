package com.edgetag.providers.blotoutcloud

import android.app.Application
import com.edgetag.providers.blotoutcloud.repository.data.SharedPreferenceSecureVault

interface DependencyInjector {
    fun getSecureStorageService() : SharedPreferenceSecureVault
    fun getApplication(): Application
}