package com.edgetag.providers.blotoutcloud.deviceinfo

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.util.Log
import com.edgetag.provider.providers.blotoutcloud.utils.CommonUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL
import java.util.*

class DeviceAndAppFraudController(private val context: Context) {

    private val TAG = "FraudController"



    private var isDeviceCompromisedCalculated = false
    private var isDeviceCompromisedValue = false

    private var isAppCompromisedCalculated = false
    private var isAppCompromisedValue = false



    fun isDeviceJailbroken(): Boolean {
        return isDeviceCompromised()
    }

    fun isDeviceCompromised(): Boolean {
        return if (isDeviceCompromisedCalculated) {
            try {
                val isTestBuild = isTestBuild()
                val hasSuperuserAPK = checkRootPackages()
                val hasChainfiresupersu = hasChainfiresupersu(context)
                val hasSU = hasSU()
                isDeviceCompromisedValue = isTestBuild || hasSuperuserAPK || hasChainfiresupersu || hasSU
                isDeviceCompromisedCalculated = true
                isDeviceCompromisedValue
            } catch (e: Exception) {
                isDeviceCompromisedValue
            }
        } else {
            isDeviceCompromisedValue
        }
    }

    fun isAppCompromised(): Boolean {
        return if (isAppCompromisedCalculated) {
            try {
                isAppCompromisedValue = (isPackageInstalled("dylib_name", context) && isPackageInstalled("libcycript", context) && isDebuggable(context)
                        && detectDebugger())
                isAppCompromisedCalculated = true
                isAppCompromisedValue
            } catch (e: Exception) {
                Log.e(TAG,e.toString())
                isAppCompromisedValue
            }
        } else {
            isAppCompromisedValue
        }
    }

    /**************************************** Checker methods  */
    private fun isDebuggable(context: Context): Boolean {
        try {
            return context.applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
        return false
    }

    private fun detectDebugger(): Boolean {
        return Debug.isDebuggerConnected()
    }

    private fun detect_threadCpuTimeNanos(): Boolean {
        try {
            val start = Debug.threadCpuTimeNanos()
            for (i in 0..999999) continue
            val stop = Debug.threadCpuTimeNanos()
            return stop - start >= 10000000
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
        return false
    }

    fun isTestBuild(): Boolean {
        try {
            val buildTags = Build.TAGS
            return buildTags != null && buildTags.contains("test-keys")
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
        return false
    }

    fun isRunningOnVM(): Boolean {
        try {
            return CommonUtils().isEmulator()
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
        return false
    }

    private fun hasSuperuserAPK(): Boolean {
        return try {
            val file = File("/system/app/Superuser.apk")
            file.exists()
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
            false
        }
    }

    private fun checkRootPackages(): Boolean {
        try {
            val places = arrayOf(
                    "/system/app/Superuser.apk",
                    "/system/etc/init.d/99SuperSUDaemon",
                    "/dev/com.koushikdutta.superuser.daemon/",
                    "/system/xbin/daemonsu")
            for (where in places) {
                if (File(where).exists()) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
            return false
        }
        return false
    }

    private fun hasChainfiresupersu(context: Context): Boolean {
        try {
            return isPackageInstalled("eu.chainfire.supersu", context)
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
        return false
    }

    private fun hasSU(): Boolean {
        try {
            return findBinary("su") || executeCommand(arrayOf("/system/xbin/which", "su")) || executeCommand(arrayOf("which", "su"))
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
        }
        return false
    }


    /**************************************** Helper methods  */
    fun isPackageInstalled(packagename: String?, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packagename!!, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG,e.toString())
            false
        }
    }

    private fun findBinary(binaryName: String): Boolean {
        val places = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/",
                "/system/xbin/busybox")
        for (where in places) {
            if (File(where + binaryName).exists()) {
                return true
            }
        }
        return false
    }

    private fun executeCommand(command: Array<String>): Boolean {
        var localProcess: Process? = null
        var `in`: BufferedReader? = null
        return try {
            localProcess = Runtime.getRuntime().exec(command)
            `in` = BufferedReader(InputStreamReader(localProcess.inputStream))
            `in`.readLine() != null
        } catch (e: Exception) {
            false
        } finally {
            localProcess?.destroy()
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }



    fun isVPN(): Boolean {
        var iface = ""
        try {
            for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp) iface = networkInterface.name
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true
                }
            }
        } catch (e: SocketException) {
            Log.e(TAG,e.toString())
        }
        return false
    }

    fun isProxied(): Boolean {
        return try {
            val link = "https://blotout.io/"
            val url = URL(link)
            val conn = url.openConnection() as HttpURLConnection
            conn.connect()
            val `is` = conn.inputStream
            conn.usingProxy()
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
            false
        }
    }
}