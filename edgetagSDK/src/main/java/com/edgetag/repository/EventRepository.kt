package com.edgetag.repository

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import com.edgetag.DependencyInjectorImpl
import com.edgetag.data.database.EventDatabaseService
import com.edgetag.data.database.entity.EventEntity
import com.edgetag.deviceinfo.device.DeviceInfo
import com.edgetag.model.*
import com.edgetag.model.edgetag.EdgetagMetaData
import com.edgetag.model.edgetag.EdgeTag
import com.edgetag.model.edgetag.Storage
import com.edgetag.network.ApiDataProvider
import com.edgetag.repository.data.SharedPreferenceSecureVault
import com.edgetag.util.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class EventRepository(private var secureStorage: SharedPreferenceSecureVault) {

  companion object {
    private const val TAG = "EventRepository"
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
            return Result.Error(InternalError(code = ErrorCodes.ERROR_CODE_NO_PROVIDER_FOUND, msg = "None of provider selected"))
          } else if (consentData?.get("all") == true) {
            consentInproviderAvailable = true
          } else if (value && (consentData?.get(key) == true || consentData?.get("all") == true)) {
            consentInproviderAvailable = true
          } else if (!value && (consentData?.get(key) == true)) {
            return Result.Error(InternalError(code = ErrorCodes.ERROR_CODE_CONSENT_AVAILABLE_BUT_NO_PROVIDER_FOUND, msg = "Consent available but provider not selected"))
          }
        }
      }
      if (!consentInproviderAvailable) {
        return Result.Error(InternalError(code = 8, msg = "No Consent provided"))
      }

      val consentInfo = Gson().fromJson(
        DependencyInjectorImpl.getInstance().getSecureStorageService()
          .fetchString(Constant.CONSENT_DATA),
        HashMap::class.java
      ) as HashMap<String, Boolean>?
      val tagMetadata = EdgetagMetaData()
      val storage = Storage()
      val edgeTag = EdgeTag()
      edgeTag.providers = getProviders()
      edgeTag.consent = consentInfo
      storage.edgeTag = edgeTag
      storage.data = getRefferals()
      tagMetadata.storage = storage
      tagMetadata.consentString = consentInfo
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

  private fun getProviders(): MutableList<String> {
    val manifestConfigurationResponse = DependencyInjectorImpl.getInstance()
      .getManifestRepository().manifestConfigurationResponse
    val providers: MutableList<String> = mutableListOf()
    for (data in manifestConfigurationResponse.result!!) {
      data?._package?.let { providers.add(it) }
    }
    return providers
  }

  private fun getRefferals(): HashMap<String, HashMap<String, String>> {
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
    DependencyInjectorImpl.getInstance().mReferrerDetails?.let { refferalInLocalStorage.put(
      "referral",
      it.installReferrer
    ) }

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
      Log.e(TAG, e.localizedMessage!!)
      Result.Error(
        InternalError(
          code = ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
          msg = e.localizedMessage!!
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

  fun Activity.getScreenName(): String {
    val packageManager = this.packageManager
    return packageManager.getActivityInfo(
      this.componentName, PackageManager.GET_META_DATA
    ).loadLabel(packageManager).toString()
  }

  fun publishEvent() {
    EventDatabaseService().getEvents()
  }


}
