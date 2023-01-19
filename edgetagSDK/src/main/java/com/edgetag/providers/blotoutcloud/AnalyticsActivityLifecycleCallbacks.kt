package com.edgetag.providers.blotoutcloud

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import com.edgetag.EdgeTag
import com.edgetag.providers.blotoutcloud.geasture.Gesture
import com.edgetag.providers.blotoutcloud.geasture.GestureListener
import com.edgetag.providers.blotoutcloud.repository.data.SharedPreferenceSecureVault
import com.edgetag.providers.blotoutcloud.utils.Constant
import com.edgetag.providers.blotoutcloud.utils.Constant.getScreenName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class AnalyticsActivityLifecycleCallbacks(private var secureStorage: SharedPreferenceSecureVault
) :
    Application.ActivityLifecycleCallbacks,
    ComponentCallbacks2,
    DefaultLifecycleObserver,
    View.OnTouchListener,
    GestureListener {

    private var numberOfActivities: AtomicInteger? = null
    private var trackedApplicationLifecycleEvents: AtomicBoolean? = null
    private var firstLaunch: AtomicBoolean? = null
    private var gesture: Gesture? = null
    private var activityReference: WeakReference<Activity>? = null


    companion object {
        private const val TAG = "LifecycleCallbacks"
    }

    init {
        try {
            numberOfActivities = AtomicInteger(1)
            firstLaunch = AtomicBoolean(false)
            trackedApplicationLifecycleEvents = AtomicBoolean(false)
            gesture = Gesture()
            gesture?.addListener(this)
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //Log.d(TAG, "onActivityCreated")
        try {
            if (!trackedApplicationLifecycleEvents!!.getAndSet(true)) {
                numberOfActivities!!.set(0)
                firstLaunch!!.set(true)
                trackApplicationLifecycleEvents(activity)

            }
            trackDeepLink(activity)
            activityReference = WeakReference(activity)
            val view = activity.window.decorView.rootView
            view.setOnTouchListener(this)
            handleUncaughtException()
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    private fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        gesture!!.dispatchTouchEvent(event)
        return true
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG,e.toString())
            null
        }
    }

    override fun onActivityStarted(activity: Activity) {
        try {

        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onActivityResumed(activity: Activity) {
        try {
            EdgeTag.tag(Constant.BO_VISIBILITY_VISIBLE,null, Constant.getProviderInfo(), object :
                com.edgetag.model.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }

    }

    override fun onActivityPaused(activity: Activity) {
        try {
            EdgeTag.tag(Constant.BO_VISIBILITY_HIDDEN,null, Constant.getProviderInfo(), object :
                com.edgetag.model.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
            BlotoutCloud().tag(
                Constant.BO_APPLICATION_BACKGROUNDED,null,
                Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        //eventRepository.prepareSystemEvent(activity, Constant.BO_VISIBILITY_HIDDEN, null, Constant.BO_EVENT_VISIBILITY_HIDDEN)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

    override fun onLowMemory() {

    }

    override fun onTrimMemory(level: Int) {

    }

    private fun trackDeepLink(activity: Activity) {
        //Log.d(TAG,"Deep link Open")
        try {
            val intent = activity.intent
            if (intent == null || intent.data == null) {
                return
            }
            val properties = hashMapOf<String, Any>()
            val uri = intent.data
            for (parameter in uri!!.queryParameterNames) {
                val value = uri.getQueryParameter(parameter)
                if (value != null && !value.trim { it <= ' ' }.isEmpty()) {
                    properties[parameter] = value
                }
            }
            properties["url"] = uri.toString()

            BlotoutCloud().tag(
                Constant.BO_DEEP_LINK_OPENED,properties,
                Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    private fun trackApplicationLifecycleEvents(activity: Activity) {
        try {
            val packageInfo: PackageInfo? = getPackageInfo(activity)
            val currentVersion = packageInfo?.versionName
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activity.getScreenName()

            val previousVersion = secureStorage.fetchString((Constant.BO_VERSION_KEY))
            if (previousVersion.isEmpty()) {
                BlotoutCloud().tag(
                    Constant.BO_APPLICATION_INSTALLED,tagInfo,
                    Constant.getProviderInfo(), object :
                    ProviderInterface.CompletionHandler {
                    override fun onSuccess() {
                    }

                    override fun onError(code: Int, msg: String) {
                    }

                })

            } else if (previousVersion != currentVersion) {
                BlotoutCloud().tag(
                    Constant.BO_APPLICATION_UPDATED,tagInfo,
                    Constant.getProviderInfo(), object :
                    ProviderInterface.CompletionHandler {
                    override fun onSuccess() {
                    }

                    override fun onError(code: Int, msg: String) {
                    }

                })
            }

            secureStorage.storeString(Constant.BO_VERSION_KEY, currentVersion)
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        dispatchTouchEvent(event)
        return true
    }

    override fun onPress(motionEvent: MotionEvent?) {
        try {
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
            BlotoutCloud().tag(Constant.BO_EVENT_CLICK_NAME,null, Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onTap(motionEvent: MotionEvent?) {
        try {
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
            BlotoutCloud().tag(Constant.BO_EVENT_TOUCH_NAME,null, Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onDrag(motionEvent: MotionEvent?) {
        try {
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
            BlotoutCloud().tag(
                Constant.BO_EVENT_SCROLL_NAME,null,
                Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onMove(motionEvent: MotionEvent?) {
        //eventRepository.prepareSystemEvent(activityReference!!.get(), Constant.BO_VISIBILITY_HIDDEN, null, Constant.BO_EVENT_VISIBILITY_HIDDEN)
    }

    override fun onRelease(motionEvent: MotionEvent?) {
        try {
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
            BlotoutCloud().tag(
                Constant.BO_EVENT_KEY_RELEASE_NAME,null,
                Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onLongPress(motionEvent: MotionEvent?) {
        try {
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
            BlotoutCloud().tag(
                Constant.BO_EVENT_KEY_RELEASE_NAME,null,
                Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    override fun onMultiTap(motionEvent: MotionEvent?, clicks: Int) {
        try {
            val tagInfo =  hashMapOf<String,Any>()
            tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
            BlotoutCloud().tag(
                Constant.BO_EVENT_MULTI_CLICK_NAME,null,
                Constant.getProviderInfo(), object :
                ProviderInterface.CompletionHandler {
                override fun onSuccess() {
                }

                override fun onError(code: Int, msg: String) {
                }

            })
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
    }

    /**
     * Catch the Errors that are not handled by application and log to backend
     */
    private fun handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
            CoroutineScope(Dispatchers.Default).launch {
                val properties = hashMapOf<String, Any>()
                properties[Constant.BO_EVENT_ERROR_NAME] = paramThrowable.localizedMessage!!
                val tagInfo =  hashMapOf<String,Any>()
                tagInfo[Constant.BO_EVENT_KEY_PATH_NAME] = activityReference!!.get()!!.getScreenName()
                BlotoutCloud().tag(
                    Constant.BO_EVENT_ERROR_NAME,null,
                    Constant.getProviderInfo(), object :
                    ProviderInterface.CompletionHandler {
                    override fun onSuccess() {
                    }

                    override fun onError(code: Int, msg: String) {
                    }

                })
                delay(500)
            }
        }
    }
}