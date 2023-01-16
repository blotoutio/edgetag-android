package com.edgetag.provider.providers.blotoutcloud.utils

import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.edgetag.providers.blotoutcloud.DependencyInjectorImpl
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class CommonUtils {

    private val TAG = "BOCommonUtils"

    fun getAsciiCustomIntSum(input: String, isCaseSenstive: Boolean): Int {
        var input = input
        input = if (isCaseSenstive) input else input.lowercase(Locale.getDefault())
        var sum = 0
        if (isPureAscii(input)) {
            for (index in 0 until input.length) {
                val c = input[index]
                sum += c.code
            }
        }
        return sum
    }

    fun isPureAscii(input: String?): Boolean {
        try {
            return Charset.forName("US-ASCII").newEncoder().canEncode(input)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return false
    }

    fun getStringMD5CustomIntSumWithCharIndexAdded(event: String, caseSensitive: Boolean): Int {
        var event = event
        var sum = 0
        try {
            event = if (caseSensitive) event else event.lowercase(Locale.getDefault())
            val stringToHash = event
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(stringToHash.toByteArray())
            val digiest = messageDigest.digest()
            val md5String: String = convertToHex(digiest)!!
            for (index in 0 until md5String.length) {
                val c = md5String[index]
                if (Character.isDigit(c)) {
                    sum += Character.getNumericValue(c)
                } else {
                    sum += c.code + index
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, e.toString())
        }
        return sum
    }

    fun convertToHex(data: ByteArray): String? {
        val buf = StringBuffer()
        for (i in data.indices) {
            var halfbyte: Int = data[i] shl 4 and 0x0F
            var two_halfs = 0
            do {
                if (0 <= halfbyte && halfbyte <= 9) buf.append(('0'.code + halfbyte).toChar()) else buf.append(('a'.code + (halfbyte - 10)).toChar())
                halfbyte = data[i] and 0x0F
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }

    infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
    private infix fun Byte.and(that: Int): Int = this.toInt().and(that)


    fun getUserID(): String? {
        //user id generation update =
        //Epoc 13 Digit Time at start +
        // Client SDK Token + UUID generate once +
        // 10 digit random number+ 10 digit random number +
        // Epoc 13 Digit time at the end = Input for SHA 512 or UUID function in case it takes
        var deviceId = ""
        try {
            deviceId = DependencyInjectorImpl.getInstance().getSecureStorageService().fetchString(Constant.BO_ANALYTICS_USER_UNIQUE_KEY)
            if (deviceId != null && deviceId.isNotEmpty()) {
                return deviceId
            } else {

                val stringBuilder = StringBuilder()
                stringBuilder.append(DateTimeUtils().get13DigitNumberObjTimeStamp())
                stringBuilder.append(DependencyInjectorImpl.getInstance().getSecureStorageService().fetchString(Constant.SDK_KEY))
                stringBuilder.append(UUID.nameUUIDFromBytes(getUUID()!!.toByteArray()).toString())
                stringBuilder.append(generateNumber())
                stringBuilder.append(generateNumber())
                stringBuilder.append(DateTimeUtils().get13DigitNumberObjTimeStamp())
                val guidString: String = convertTo64CharUUID(EncryptionUtils().sha256(stringBuilder.toString()))!!
                deviceId = guidString
                DependencyInjectorImpl.getInstance().getSecureStorageService().storeString(Constant.BO_ANALYTICS_USER_UNIQUE_KEY, deviceId)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
        }
        return deviceId
    }

    fun generateNumber(): Long {
        return (Math.random() * 100000 + 3333300000L).toLong()
    }

    fun convertTo64CharUUID(stringToConvert: String?): String? {
        try {
            if (stringToConvert != null && stringToConvert.isNotEmpty()) {
                val str: String = stringToConvert
                val lengthsOfPart = ArrayList(Arrays.asList(16, 8, 8, 8, 24))
                val parts = ArrayList<String?>()
                var range = 0
                for (i in lengthsOfPart.indices) {
                    val stringOfRange = str.substring(range, range + lengthsOfPart[i])
                    parts.add(stringOfRange)
                    range += lengthsOfPart[i]
                }
                return TextUtils.join("-", parts)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
        }
        return stringToConvert
    }

    fun getUUID(): String? {
        return UUID.randomUUID().toString()
    }

    fun getUserBirthTimeStamp(): Long {
        var userCreatedTimestamp: Long = 0
        try {
            userCreatedTimestamp =
                DependencyInjectorImpl.getInstance().getSecureStorageService().fetchLong(Constant.BO_ANALYTICS_USER_BIRTH_TIME_STAMP)

            if (userCreatedTimestamp == 0L) {
                userCreatedTimestamp = DateTimeUtils().get13DigitNumberObjTimeStamp()
                DependencyInjectorImpl.getInstance().getSecureStorageService().storeLong(Constant.BO_ANALYTICS_USER_BIRTH_TIME_STAMP, userCreatedTimestamp)
            }
        } catch (e: java.lang.Exception) {

        }
        return userCreatedTimestamp
    }

    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf("/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su")
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }


}