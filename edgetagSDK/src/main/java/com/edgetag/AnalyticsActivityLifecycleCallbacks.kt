package com.edgetag

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import com.edgetag.util.Constant
import com.google.gson.Gson
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class AnalyticsActivityLifecycleCallbacks :
        Application.ActivityLifecycleCallbacks,
        ComponentCallbacks2,
        DefaultLifecycleObserver {
    private var numberOfActivities: AtomicInteger? = null
    private var trackedApplicationLifecycleEvents: AtomicBoolean? = null
    private var firstLaunch: AtomicBoolean? = null


    companion object {
        private const val TAG = "LifecycleCallbacks"
    }

    init {
        try {
            numberOfActivities = AtomicInteger(1)
            firstLaunch = AtomicBoolean(false)
            trackedApplicationLifecycleEvents = AtomicBoolean(false)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        try {
            DependencyInjectorImpl.getInstance().pageUrl = activity.localClassName.substringAfterLast(delimiter = '.')
            trackDeepLink(activity)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }


    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

    override fun onLowMemory() {

    }

    override fun onTrimMemory(level: Int) {

    }

    private fun trackDeepLink(activity: Activity) {
        try {
            val intent = activity.intent
            if (intent == null || intent.data == null) {
                return
            }
            val properties = hashMapOf<String, Any>()
            val uri = intent.data
            for (parameter in uri!!.queryParameterNames) {
                val value = uri.getQueryParameter(parameter)
                if (value != null && value.trim { it <= ' ' }.isNotEmpty()) {
                    properties[parameter] = value
                }
            }

            DependencyInjectorImpl.getInstance().getSecureStorageService().storeString(Constant.REFFERAL,Gson().toJson(properties))
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }


}
