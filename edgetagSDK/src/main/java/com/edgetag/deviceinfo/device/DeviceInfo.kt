package com.edgetag.deviceinfo.device


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.edgetag.DependencyInjectorImpl
import com.edgetag.util.Constant
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class DeviceInfo(private val context: Context) {

  private val NOT_FOUND_VAL = "unknown"

  /* Device Info: */
  val deviceName: String
    get() {
      val manufacturer = Build.MANUFACTURER
      val model = Build.MODEL
      return if (model.startsWith(manufacturer)) {
        model
      } else {
        "$manufacturer $model"
      }
    }

  val deviceLocale: String?
    get() {
      var locale: String? = null
      val current = context.resources.configuration.locale
      if (current != null) {
        locale = current.toString()
      }

      return locale
    }

  val releaseBuildVersion: String
    get() = Build.VERSION.RELEASE

  val buildVersionCodeName: String
    get() = Build.VERSION.CODENAME

  val manufacturer: String
    get() = Build.MANUFACTURER

  val model: String
    get() = Build.MODEL


  val product: String
    get() = Build.PRODUCT

  val fingerprint: String
    get() = Build.FINGERPRINT

  val hardware: String
    get() = Build.HARDWARE


  val radioVer: String
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    get() = Build.getRadioVersion()


  val device: String
    get() = Build.DEVICE

  val board: String
    get() = Build.BOARD

  val displayVersion: String
    get() = Build.DISPLAY

  val buildBrand: String
    get() = Build.BRAND

  val buildHost: String
    get() = Build.HOST

  val buildTime: Long
    get() = Build.TIME

  val buildUser: String
    get() = Build.USER

  val serial: String
    get() = Build.SERIAL

  val osVersion: String
    get() = Build.VERSION.RELEASE

  val language: String
    get() = Locale.getDefault().language

  val sdkVersion: Int
    get() = Build.VERSION.SDK_INT

  val screenDensity: String
    get() {
      val density = context.resources.displayMetrics.densityDpi
      var scrType = ""
      when (density) {
        DisplayMetrics.DENSITY_LOW -> scrType = "ldpi"
        DisplayMetrics.DENSITY_MEDIUM -> scrType = "mdpi"
        DisplayMetrics.DENSITY_HIGH -> scrType = "hdpi"
        DisplayMetrics.DENSITY_XHIGH -> scrType = "xhdpi"
        else -> scrType = "other"
      }
      return scrType
    }


  val screenHeight: Int
    get() {
      var height = 0
      val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val display = wm.defaultDisplay
      if (Build.VERSION.SDK_INT > 12) {
        val size = Point()
        display.getSize(size)
        height = size.y
      } else {
        height = display.height
      }
      return height
    }

  // deprecated
  val screenWidth: Int
    get() {
      var width = 0
      val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val display = wm.defaultDisplay
      if (Build.VERSION.SDK_INT > 12) {
        val size = Point()
        display.getSize(size)
        width = size.x
      } else {
        width = display.width
      }
      return width
    }


  /* App Info: */
  val versionName: String?
    get() {
      val pInfo: PackageInfo
      try {
        pInfo = context.packageManager.getPackageInfo(
          context.packageName, 0
        )
        return pInfo.versionName
      } catch (e1: Exception) {
        return null
      }

    }

  val versionCode: Int?
    get() {
      val pInfo: PackageInfo
      try {
        pInfo = context.packageManager.getPackageInfo(
          context.packageName, 0
        )
        return pInfo.versionCode
      } catch (e1: Exception) {
        return null
      }

    }

  val packageName: String
    get() = context.packageName

  val activityName: String
    get() = context.javaClass.simpleName

  val appName: String
    get() {
      val packageManager = context.packageManager
      var applicationInfo: ApplicationInfo? = null
      try {
        applicationInfo = packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
      } catch (e: PackageManager.NameNotFoundException) {
      }

      return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else NOT_FOUND_VAL) as String
    }


  val isRunningOnEmulator: Boolean
    get() = (Build.FINGERPRINT.startsWith("generic")
      || Build.FINGERPRINT.startsWith("unknown")
      || Build.MODEL.contains("google_sdk")
      || Build.MODEL.contains("Emulator")
      || Build.MODEL.contains("Android SDK built for x86")
      || Build.MANUFACTURER.contains("Genymotion")
      || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
      || "google_sdk" == Build.PRODUCT
      || Build.PRODUCT.contains("vbox86p")
      || Build.DEVICE.contains("vbox86p")
      || Build.HARDWARE.contains("vbox86"))


  val isDeviceRooted: Boolean
    get() {
      val paths = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
      )
      for (path in paths) {
        if (File(path).exists()) return true
      }
      return false
    }


  val androidId: String
    get() = Settings.Secure.getString(
      context.contentResolver,
      Settings.Secure.ANDROID_ID
    )

  val installSource: String
    get() {
      val pm = context.packageManager
      return pm.getInstallerPackageName(context.packageName).toString()
    }

  val userAgent: String
    get() {
      try {
        val systemUa = System.getProperty("http.agent")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          WebSettings.getDefaultUserAgent(context) + "__" + systemUa
        } else WebView(context).settings.userAgentString + "__" + systemUa
      }catch (e:java.lang.Exception){
        return ""
      }
    }

  @RequiresApi(Build.VERSION_CODES.M)
  @SuppressLint("MissingPermission")
  fun isOnline(context: Context): Boolean {
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
      val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
      if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
          return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
          return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
          return true
        }
      }
    }
    return false
  }

  fun getAdvertisingId() {
    GlobalScope.launch {
      try {
        val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
        val adId = adInfo?.id
        DependencyInjectorImpl.getInstance().getSecureStorageService()
          .storeString(Constant.AD_ID, adId)
      }catch (e:Exception){
      }
    }
  }
}
