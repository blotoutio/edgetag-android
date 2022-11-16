package com.edgetag

import android.app.Application
import com.edgetag.model.CompletionHandler
import com.edgetag.model.OnComplete

interface EdgeTagInterface {

    fun init(
        application: Application,
        edgeTagConfiguration: EdgeTagConfiguration,
        completionHandler: CompletionHandler
    )

    fun consent(consentInfo: HashMap<String, Boolean>, completionHandler: CompletionHandler)

    fun tag(
        eventName: String,
        tagInfo: HashMap<String, Any>?,
        providerInfo: HashMap<String, Boolean>?,
        completionHandler: CompletionHandler
    )

    fun user(
        key: String,
        value: String,
        completionHandler: CompletionHandler
    )

    fun postData(
        data: HashMap<String, String>?,
        onComplete: OnComplete?
    )

    fun getData(
        keys: ArrayList<String>?,
        onComplete: OnComplete?
    )

    fun getKeys(
        onComplete: OnComplete?
    )

    fun isAdvertiserIdAvailable(
        onComplete: OnComplete?
    )
}
