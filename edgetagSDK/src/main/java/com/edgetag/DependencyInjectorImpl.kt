package com.edgetag

import android.app.Application
import android.util.Log
import com.android.installreferrer.api.ReferrerDetails
import com.edgetag.data.database.EventDatabase
import com.edgetag.deviceinfo.device.DeviceInfo
import com.edgetag.network.HostConfiguration
import com.edgetag.network.RemoteApiClient
import com.edgetag.network.RemoteApiDataSource
import com.edgetag.referral.InstallRefferal
import com.edgetag.repository.EventRepository
import com.edgetag.repository.ManifestRepository
import com.edgetag.repository.data.ConfigurationDataManager
import com.edgetag.repository.data.SharedPreferenceSecureVault
import com.edgetag.repository.impl.DataManagerImpl
import com.edgetag.util.DateTimeUtils

class DependencyInjectorImpl private constructor(
    application: Application,
    secureStorageService: SharedPreferenceSecureVault,
    hostConfiguration: HostConfiguration,
    eventDatabase: EventDatabase) : DependencyInjector {

    var mReferrerDetails: ReferrerDetails? = null
    var pageUrl:String =""

    companion object {
        private const val TAG ="DependencyInjectorImpl"
        private lateinit var instance: DependencyInjectorImpl
        private var sessionID :Long = 0
        private lateinit var eventRepository :EventRepository
        private lateinit var mSecureStorageService : SharedPreferenceSecureVault
        private lateinit var  mHostConfiguration : HostConfiguration
        private lateinit var mEventDatabase : EventDatabase
        private lateinit var mApplication :Application


        @Synchronized
        fun init(
            application: Application,
            secureStorageService: SharedPreferenceSecureVault,
            hostConfiguration: HostConfiguration,
            eventDatabase: EventDatabase
        ) :Boolean{
            mApplication = application
            mHostConfiguration= hostConfiguration
            mSecureStorageService=secureStorageService
            mEventDatabase=eventDatabase
            try {
                instance = DependencyInjectorImpl(application, secureStorageService, hostConfiguration, mEventDatabase)
                instance.earlyInit()
            }catch (e:Exception){
                e.printStackTrace()
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

        fun getEventRepository():EventRepository{
            return eventRepository
        }
    }

    private val dataManager : DataManagerImpl by lazy{
        DataManagerImpl(this)
    }
    private val mManifestRepository : ManifestRepository by lazy{
        ManifestRepository(dataManager.getConfigurationDataManager())
    }

    override fun getRemoteAPIDataSource(): RemoteApiDataSource {
        return RemoteApiDataSource(RemoteApiClient(getHostService()).getRemoteApiService())
    }

    override fun getHostService():HostConfiguration{
        return mHostConfiguration

    }

    override fun getApplication():Application {
        return mApplication
    }

    override fun getConfigurationManager(): ConfigurationDataManager {
        return dataManager.getConfigurationDataManager()
    }

    override fun getEventDatabase(): EventDatabase = mEventDatabase

    override fun getManifestRepository(): ManifestRepository = mManifestRepository

    override fun getSecureStorageService(): SharedPreferenceSecureVault {
        return mSecureStorageService
    }

    fun earlyInit(){
        try {
            sessionID = DateTimeUtils().get13DigitNumberObjTimeStamp()
            eventRepository = EventRepository(mSecureStorageService)
            val activityLifeCycleCallback =
                AnalyticsActivityLifecycleCallbacks()
            mApplication.registerActivityLifecycleCallbacks(activityLifeCycleCallback)
        }
        catch (e:Throwable){
            Log.d(TAG,e.localizedMessage!!)
        }
    }

    fun initialize(){
        try {
            InstallRefferal().startClient(mApplication)
            DeviceInfo(mApplication).getAdvertisingId()
        }catch (e:Throwable){
            e.printStackTrace()
        }
    }
}