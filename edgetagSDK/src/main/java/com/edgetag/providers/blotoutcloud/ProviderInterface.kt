package com.edgetag.providers.blotoutcloud

import android.app.Application

interface ProviderInterface {

    interface CompletionHandler {
        fun onSuccess()
        fun onError(code : Int = 0, msg : String = "")
    }
    fun init(application: Application,
             completionHandler: CompletionHandler
    )
    fun tag(
        eventName: String,
        tagInfo: HashMap<String, Any>?,
        providerInfo: HashMap<String, Boolean>?,
        completionHandler: CompletionHandler
    )
    fun load()
}