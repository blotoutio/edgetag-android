package com.edgetag.repository

import com.edgetag.DependencyInjectorImpl
import com.edgetag.model.ErrorCodes.ERROR_CODE_MANIFEST_NOT_AVAILABLE
import com.edgetag.model.InternalError
import com.edgetag.model.Result
import com.edgetag.model.edgetag.ManifestConfigurationResponse
import com.edgetag.repository.data.ConfigurationDataManager
import com.edgetag.util.Constant
import com.google.gson.Gson

class ManifestRepository(private val configurationDataManager: ConfigurationDataManager) {


     lateinit var manifestConfigurationResponse:ManifestConfigurationResponse


    suspend fun fetchManifestConfiguration() : Result<String>? {
            when (val result = configurationDataManager.downloadManifestConfiguration()) {
                is Result.Success -> {
                    val manifestResponseString = Gson().toJson(result.data)
                    DependencyInjectorImpl.getInstance().getSecureStorageService()
                        .storeString(Constant.MANIFEST_DATA,manifestResponseString )
                    return result.data?.let { initManifestConfiguration(it) }
                }
                else -> {
                    val manifestConfiguration = Gson().fromJson(
                        DependencyInjectorImpl.getInstance().getSecureStorageService()
                            .fetchString(Constant.MANIFEST_DATA),
                        ManifestConfigurationResponse::class.java
                    )
                    manifestConfiguration?.let {
                        return initManifestConfiguration(manifestConfiguration)
                    }?: run {
                        return Result.Error(InternalError(code = ERROR_CODE_MANIFEST_NOT_AVAILABLE))
                    }
                }
            }
    }

    fun initManifestConfiguration(manifestResponse: ManifestConfigurationResponse):Result<String> {
        manifestConfigurationResponse = manifestResponse
        return Result.Success("")
    }
}
