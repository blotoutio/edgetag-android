package com.edgetag.repository

import android.app.Activity
import android.content.pm.PackageManager
import com.edgetag.DependencyInjectorImpl
import com.edgetag.data.database.EventDatabaseService
import com.edgetag.data.database.entity.EventEntity
import com.edgetag.deviceinfo.device.DeviceInfo
import com.edgetag.model.ErrorCodes
import com.edgetag.model.InternalError
import com.edgetag.model.OnComplete
import com.edgetag.model.Result
import com.edgetag.model.edgetag.EdgeTag
import com.edgetag.model.edgetag.EdgetagMetaData
import com.edgetag.model.edgetag.Storage
import com.edgetag.network.ApiDataProvider
import com.edgetag.repository.data.SharedPreferenceSecureVault
import com.edgetag.util.Constant
import com.google.gson.Gson
import retrofit2.Call




class EventRepository(private var secureStorage: SharedPreferenceSecureVault) {

    companion object {
        private const val TAG = "EventRepository"
        private var globalData = hashMapOf<String,String>()
    }

    lateinit var visibleActivity: Activity

    fun prepareTagEvent(
        tagName: String,
        tagInfo: HashMap<String, Any>,
        providerInfo: HashMap<String, Boolean>
    ): Result<String> {
        return try {
            val consentData = Gson().fromJson<HashMap<String, Boolean>>(
                DependencyInjectorImpl.getInstance().getSecureStorageService()
                    .fetchString(Constant.CONSENT_DATA),
                HashMap::class.java
            )
            var consentInproviderAvailable = false

            if (providerInfo.isEmpty() && consentData.containsValue(true)) {
                consentInproviderAvailable = true
            } else {
                for ((key, value) in providerInfo) {
                    if (key.contentEquals("all") && !value) {
                        consentInproviderAvailable = false
                        return Result.Error(
                            InternalError(
                                code = ErrorCodes.ERROR_CODE_NO_PROVIDER_FOUND,
                                msg = "None of provider selected"
                            )
                        )
                    } else if (consentData?.get("all") == true) {
                        consentInproviderAvailable = true
                    } else if (value && (consentData?.get(key) == true || consentData?.get("all") == true)) {
                        consentInproviderAvailable = true
                    } else if (!value && (consentData?.get(key) == true)) {
                        return Result.Error(
                            InternalError(
                                code = ErrorCodes.ERROR_CODE_CONSENT_AVAILABLE_BUT_NO_PROVIDER_FOUND,
                                msg = "Consent available but provider not selected"
                            )
                        )
                    }
                }
            }
            if (!consentInproviderAvailable) {
                return Result.Error(InternalError(code = 8, msg = "No Consent provided"))
            }

            val tagMetadata = EdgetagMetaData()
            val storage = Storage()
            val edgeTag = EdgeTag()
            edgeTag.providers = getProviders()
            edgeTag.consent = getConsentInfo()
            storage.edgeTag = edgeTag
            storage.data = getRefferalData()
            storage.kv = globalData
            tagMetadata.storage = storage
            tagMetadata.consentString = getConsentInfo()
            tagMetadata.tag_user_id = DependencyInjectorImpl.getInstance().getSecureStorageService()
                .fetchString(Constant.TAG_USER_ID)

            tagMetadata.providers = providerInfo
            if (this::visibleActivity.isInitialized) {
                tagMetadata.pageUrl =
                    visibleActivity.localClassName.substringAfterLast(delimiter = '.')
            }
            val context = DependencyInjectorImpl.getInstance().getApplication()
            tagMetadata.userAgent = DeviceInfo(context).userAgent
            tagMetadata.eventName = tagName
            tagMetadata.data = tagInfo
            publishEvents(tagMetadata)
        } catch (e: Exception) {
            //Log.e(TAG, e.localizedMessage!!)
            Result.Error(
                InternalError(
                    code = ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
                    msg = "Internal error"
                )
            )
        }
    }

    fun prepareConsent(
        consentInfo: HashMap<String, Boolean>
    ): Result<String> {
        return try {
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .storeString(Constant.CONSENT_DATA, Gson().toJson(consentInfo))
            val consentMetadata = EdgetagMetaData()
            val storage = Storage()
            val edgeTag = EdgeTag()
            edgeTag.providers = getProviders()
            edgeTag.consent = consentInfo
            storage.edgeTag = edgeTag
            storage.data = getRefferalData()
            storage.kv = globalData
            consentMetadata.storage = storage
            consentMetadata.tag_user_id =
                DependencyInjectorImpl.getInstance().getSecureStorageService()
                    .fetchString(Constant.TAG_USER_ID)
            consentMetadata.consentString = consentInfo
            if (this::visibleActivity.isInitialized) {
                consentMetadata.pageUrl =
                    visibleActivity.localClassName.substringAfterLast(delimiter = '.')
            }
            val context = DependencyInjectorImpl.getInstance().getApplication()
            consentMetadata.userAgent = DeviceInfo(context).userAgent
            publishConsentEvents(consentMetadata)
        } catch (e: Exception) {
            //Log.e(TAG, e.localizedMessage!!)
            Result.Error(
                InternalError(
                    code = ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
                    msg = "Internal error"
                )
            )
        }
    }

    fun prepareUser(
        key: String, value: String
    ): Result<String> {
        return try {
            globalData[key] = value
            val consentMetadata = EdgetagMetaData()
            consentMetadata.key = key
            consentMetadata.value = value
            val storage = Storage()
            val edgeTag = EdgeTag()
            edgeTag.providers = getProviders()
            edgeTag.consent = Gson().fromJson<HashMap<String, Boolean>>(
                DependencyInjectorImpl.getInstance().getSecureStorageService()
                    .fetchString(Constant.CONSENT_DATA),
                HashMap::class.java
            )
            storage.edgeTag = edgeTag
            storage.data = getRefferalData()
            storage.kv= globalData
            consentMetadata.storage = storage
            consentMetadata.tag_user_id =
                DependencyInjectorImpl.getInstance().getSecureStorageService()
                    .fetchString(Constant.TAG_USER_ID)
            consentMetadata.consentString = edgeTag.consent
            if (this::visibleActivity.isInitialized) {
                consentMetadata.pageUrl =
                    visibleActivity.localClassName.substringAfterLast(delimiter = '.')
            }
            val context = DependencyInjectorImpl.getInstance().getApplication()
            consentMetadata.userAgent = DeviceInfo(context).userAgent
            publishUserEvents(consentMetadata)
        } catch (e: Exception) {
            //Log.e(TAG, e.localizedMessage!!)
            Result.Error(
                InternalError(
                    code = ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
                    msg = "Internal error"
                )
            )
        }
    }

    private fun getProviders(): MutableList<String> {
        val manifestConfigurationResponse = DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse
        val providers: MutableList<String> = mutableListOf()
        for (data in manifestConfigurationResponse.result!!) {
            data?._package?.let { providers.add(it) }
        }
        return providers
    }

    private fun getRefferalData(): HashMap<String, HashMap<String, String>> {
        val refferalToPush = hashMapOf<String, HashMap<String, String>>()
        val refferals = DependencyInjectorImpl.getInstance().getSecureStorageService()
            .fetchString(Constant.REFFERAL)
        var refferalInLocalStorage = hashMapOf<Any, Any>()
        if (!refferals.isNullOrEmpty()) {
            refferalInLocalStorage =
                Gson().fromJson(refferals, HashMap::class.java) as HashMap<Any, Any>
        }
        val manifestConfigurationResponse = DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse
        val refferalEvents = hashMapOf<String, String>()
        refferalInLocalStorage.put(
            "advertiserId",
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .fetchString(Constant.AD_ID)
        )
        DependencyInjectorImpl.getInstance().mReferrerDetails?.let {
            refferalInLocalStorage.put(
                "referral",
                it.installReferrer
            )
        }

        for (result in manifestConfigurationResponse.result!!) {
            for (captureRule in result?.rules?.capture!!) {
                if (refferalInLocalStorage.containsKey(captureRule.key!!)) {
                    refferalEvents.put(
                        captureRule.key!!,
                        refferalInLocalStorage.get(captureRule.key!!).toString()
                    )
                    refferalToPush.put(result._package!!, refferalEvents)

                }
            }
        }
        return refferalToPush
    }


    private fun pushEvents(event: EdgetagMetaData): Result<String> {
        return try {
            val eventEntity = EventEntity(Gson().toJson(event))
            EventDatabaseService().insertEvent(eventEntity)
            Result.Success("")
        } catch (e: Exception) {
            Result.Error(
                InternalError(
                    code = ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
                    msg = e.localizedMessage ?: ""
                )
            )
        }
    }

    private fun publishEvents(event: EdgetagMetaData): Result<String> {
        DependencyInjectorImpl.getInstance().getConfigurationManager()
            .publishEvents(event, object : ApiDataProvider<Any?>() {
                override fun onFailed(
                    errorCode: Int,
                    message: String,
                    call: Call<Any?>
                ) {
                }

                override fun onError(t: Throwable, call: Call<Any?>) {
                }

                override fun onSuccess(data: Any?) {
                }
            })
        return Result.Success("")
    }

    private fun publishConsentEvents(event: EdgetagMetaData): Result<String> {
        DependencyInjectorImpl.getInstance().getConfigurationManager()
            .publishConsentEvents(event, object : ApiDataProvider<Any?>() {
                override fun onFailed(
                    errorCode: Int,
                    message: String,
                    call: Call<Any?>
                ) {
                }

                override fun onError(t: Throwable, call: Call<Any?>) {
                }

                override fun onSuccess(data: Any?) {
                }
            })
        return Result.Success("")
    }

    private fun publishUserEvents(event: EdgetagMetaData): Result<String> {
        DependencyInjectorImpl.getInstance().getConfigurationManager()
            .publishUserEvents(event, object : ApiDataProvider<Any?>() {
                override fun onFailed(
                    errorCode: Int,
                    message: String,
                    call: Call<Any?>
                ) {
                }

                override fun onError(t: Throwable, call: Call<Any?>) {
                }

                override fun onSuccess(data: Any?) {
                }
            })
        return Result.Success("")
    }

    fun postData(data: HashMap<String, String>, onComplete: OnComplete) {
        globalData.putAll(data)
        val tagMetadata = EdgetagMetaData()
        val storage = Storage()
        val edgeTag = EdgeTag()
        edgeTag.providers = getProviders()
        edgeTag.consent = getConsentInfo()
        storage.edgeTag = edgeTag
        storage.data = getRefferalData()
        storage.kv= globalData
        tagMetadata.storage = storage
        tagMetadata.data = data as HashMap<String, Any>
        tagMetadata.tag_user_id = DependencyInjectorImpl.getInstance().getSecureStorageService()
            .fetchString(Constant.TAG_USER_ID)

        DependencyInjectorImpl.getInstance().getConfigurationManager()
            .publishDataEvents(tagMetadata, object : ApiDataProvider<Any?>() {
                override fun onFailed(
                    errorCode: Int,
                    message: String,
                    call: Call<Any?>
                ) {
                    onComplete.onError(code = errorCode)
                }

                override fun onError(t: Throwable, call: Call<Any?>) {
                    onComplete.onError(msg = t.toString())
                }

                override fun onSuccess(data: Any?) {
                    try {
                        onComplete.onSuccess(msg = data.toString())
                    }catch (e:java.lang.Exception){
                        onComplete.onError(code = ErrorCodes.ERROR_CODE_JSON_PARSING_ERROR)
                    }
                }
            })
    }

    fun getDataEvents(keys: ArrayList<String>, onComplete: OnComplete) {

        val commaSeparatedKeys = StringBuilder("")
        for (eachstring in keys) {
            commaSeparatedKeys.append(eachstring).append(",")
        }

        DependencyInjectorImpl.getInstance().getConfigurationManager()
            .getDataEvents(commaSeparatedKeys.toString(), object : ApiDataProvider<Any?>() {
                override fun onFailed(
                    errorCode: Int,
                    message: String,
                    call: Call<Any?>
                ) {
                    onComplete.onError(code = errorCode)
                }

                override fun onError(t: Throwable, call: Call<Any?>) {
                    onComplete.onError(msg = t.toString())
                }

                override fun onSuccess(data: Any?) {

                    try {
                        val values = Gson().toJsonTree(data).getAsJsonObject().getAsJsonObject("result")
                        val retMap = Gson().fromJson<HashMap<String, String>>(
                            values.toString(),
                            HashMap::class.java
                        )

                        onComplete.onSuccess(msg = retMap)
                    }catch (e:java.lang.Exception){
                        onComplete.onError(code = ErrorCodes.ERROR_CODE_JSON_PARSING_ERROR)
                    }
                }
            })
    }

    fun getKeyEvents(onComplete: OnComplete) {
        DependencyInjectorImpl.getInstance().getConfigurationManager()
            .getKeyEvents(object : ApiDataProvider<Any?>() {
                override fun onFailed(
                    errorCode: Int,
                    message: String,
                    call: Call<Any?>
                ) {
                    onComplete.onError(code = errorCode)
                }

                override fun onError(t: Throwable, call: Call<Any?>) {
                    onComplete.onError(msg = t.toString())
                }

                override fun onSuccess(data: Any?) {
                    try {
                        val values = Gson().toJsonTree(data).getAsJsonObject().getAsJsonArray("result");

                        val retMap = Gson().fromJson<ArrayList<String>>(
                            values.toString(),
                            ArrayList::class.java
                        )

                        onComplete.onSuccess(msg = retMap)
                    }catch (e:java.lang.Exception){
                        onComplete.onError(code = ErrorCodes.ERROR_CODE_JSON_PARSING_ERROR)
                    }
                }
            })
    }

    fun Activity.getScreenName(): String {
        val packageManager = this.packageManager
        return packageManager.getActivityInfo(
            this.componentName, PackageManager.GET_META_DATA
        ).loadLabel(packageManager).toString()
    }

    fun publishEvent() {
        EventDatabaseService().getEvents()
    }

    private fun getConsentInfo():HashMap<String, Boolean>{
        val consentInfo = Gson().fromJson(
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .fetchString(Constant.CONSENT_DATA),
            HashMap::class.java
        ) as HashMap<String, Boolean>
        return consentInfo
    }


}