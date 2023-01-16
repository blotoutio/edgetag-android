package com.edgetag.providers.blotoutcloud

import android.app.Application
import android.util.Log
import com.edgetag.EdgeTag
import com.edgetag.provider.providers.blotoutcloud.repository.data.SharedPreferenceSecureVault
import com.edgetag.provider.providers.blotoutcloud.utils.Constant
import com.edgetag.provider.providers.blotoutcloud.utils.DateTimeUtils
import com.edgetag.providers.blotoutcloud.deviceinfo.device.DeviceInfo

class DependencyInjectorImpl private constructor(
    application: Application,
    secureStorageService: SharedPreferenceSecureVault
) : DependencyInjector {


    private val mSecureStorageService = secureStorageService
    private val mApplication = application
    var appSetIdInfo:String?=""



    companion object {
        private const val TAG ="DependencyInjectorImpl"
        private lateinit var instance: DependencyInjectorImpl
        private var sessionID :Long = 0

        @Synchronized
        fun init(
                application: Application,
                secureStorageService: SharedPreferenceSecureVault
        ) :Boolean{
            try {
                instance = DependencyInjectorImpl(application, secureStorageService)
                instance.earlyInit()
            }catch (e:Exception){
                Log.d(TAG,e.localizedMessage!!)
                return false
            }
            return true
        }

        fun getInstance(): DependencyInjectorImpl {
            return instance
        }

        fun getSessionId():Long{
            return sessionID
        }
    }


    override fun getApplication():Application {
        return mApplication
    }

    override fun getSecureStorageService(): SharedPreferenceSecureVault {
        return mSecureStorageService
    }

    fun earlyInit(){
        try {
        sessionID = DateTimeUtils().get13DigitNumberObjTimeStamp()
        val activityLifeCycleCallback =
            AnalyticsActivityLifecycleCallbacks( mSecureStorageService)
        mApplication.registerActivityLifecycleCallbacks(activityLifeCycleCallback)}
        catch (e:Throwable){
                Log.d(TAG,e.localizedMessage!!)
        }
    }

    fun initialize(){
        try {
            EdgeTag.tag(Constant.BO_SDK_START,null,Constant.getProviderInfo(), object :
                com.edgetag.model.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
            DeviceInfo(mApplication).getAppBasedUserID()
        }catch (e:Throwable){
            Log.d(TAG,e.localizedMessage!!)
        }
    }
}