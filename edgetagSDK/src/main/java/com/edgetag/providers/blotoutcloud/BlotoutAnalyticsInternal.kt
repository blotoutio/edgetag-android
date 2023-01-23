package com.edgetag.providers.blotoutcloud

import android.app.Application
import android.content.Context
import android.util.Log
import com.edgetag.EdgeTag
import com.edgetag.provider.providers.blotoutcloud.model.ErrorCodes
import com.edgetag.providers.blotoutcloud.repository.impl.SharedPreferenceSecureVaultImpl
import com.edgetag.providers.blotoutcloud.utils.Constant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BlotoutAnalyticsInternal : BlotoutAnalyticsInterface {

    companion object {
        private const val TAG = "AnalyticsInternal"
        private var isSdkinitiliazed = false
    }


    override fun initProvider(
        application: Application,
        completionHandler: ProviderInterface.CompletionHandler
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            completionHandler.onError(
                code = ErrorCodes.ERROR_CODE_NETWORK_ERROR,
                msg = exception.localizedMessage!!
            )
        }
        CoroutineScope(Dispatchers.IO + handler).launch {

            val secureVault = SharedPreferenceSecureVaultImpl(
                application.getSharedPreferences(
                    "vault",
                    Context.MODE_PRIVATE
                )
            )

            DependencyInjectorImpl.init(
                application = application,
                secureStorageService = secureVault
            )

            isSdkinitiliazed = true
            DependencyInjectorImpl.getInstance().initialize()
            completionHandler.onSuccess()
        }
    }

    @Synchronized
    override fun enable(enabled: Boolean) {
        try {
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .storeBoolean(Constant.IS_SDK_ENABLE, enabled)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override fun tag(
        eventName: String,
        tagInfo: HashMap<String, Any>?,
        providerInfo: HashMap<String, Boolean>?,
        completionHandler: ProviderInterface.CompletionHandler
    ) {
        EdgeTag.tag(eventName, tagInfo, providerInfo, object :
            com.edgetag.model.CompletionHandler {
            override fun onSuccess() {
                completionHandler.onSuccess()
            }

            override fun onError(code: Int, msg: String) {
                completionHandler.onError(code, msg)
            }

        })
    }


    override fun load() {
        TODO("Not yet implemented")
    }
}