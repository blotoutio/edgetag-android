package com.edgetag.providers.blotoutcloud.utils

import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionUtils(algorithm: String="", passphrase: String="", mode: Int=0) {

    companion object {
        const val ALGORITHM_AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding"
        private val CRYPTO_IVX = "Q0BG17E2819IWZYQ".toByteArray()
        const val CRYPTO_IVX_STRING = "Q0BG17E2819IWZYQ"
        const val TAG = "SimpleCrypto"
        const val MODE_128BIT = 128
        const val MODE_256BIT = 256
        const val MODE_DEFAULT = 1
    }

    private val BASE64_FLAGS = Base64.NO_WRAP

    private var algorithm: String? = null
    private  var rawKey: ByteArray

    init {
        this.algorithm = algorithm
        rawKey = when (mode) {
            MODE_128BIT -> getKeyBytes(passphrase, MODE_128BIT)
            MODE_256BIT -> getKeyBytes(passphrase, MODE_256BIT)
            else -> passphrase.toByteArray()
        }
    }



    fun encrypt(v: String): String? {
        return try {
             toHex(encrypt(v.toByteArray()))
        } catch (e: Exception) {
            Log.e(TAG,e.toString())
             v
        }
    }

    fun encrypt(v: Long): String? {
        return toHex(encrypt(v.toString().toByteArray()))
    }

    fun encrypt(v: Short): String? {
        return toHex(encrypt(v.toString().toByteArray()))
    }

    fun encrypt(v: Int): String? {
        return toHex(encrypt(v.toString().toByteArray()))
    }

    fun encrypt(v: Float): String? {
        return toHex(encrypt(v.toString().toByteArray()))
    }

    fun encrypt(v: Double): String? {
        return toHex(encrypt(v.toString().toByteArray()))
    }

    fun encrypt(b: Boolean): String? {
        return toHex(encrypt(b.toString().toByteArray()))
    }

    fun encrypt(v: ByteArray): ByteArray? {
        return encrypt(rawKey, v)
    }

    fun decrypt(enc: ByteArray): ByteArray? {
        return decrypt(rawKey, enc)
    }

    fun decryptString(encrypted: String): String {
        return try {
            String(decrypt(toByte(encrypted))!!)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            encrypted
        }
    }

    fun decryptLong(encrypted: String): Long? {
        return decryptString(encrypted).toLong()
    }

    fun decryptShort(encrypted: String): Short? {
        return decryptString(encrypted).toShort()
    }

    fun decryptInteger(encrypted: String): Int? {
        return decryptString(encrypted).toInt()
    }

    fun decryptBoolean(encrypted: String): Boolean? {
        return java.lang.Boolean.parseBoolean(decryptString(encrypted))
    }

    fun decryptFloat(encrypted: String): Float? {
        return decryptString(encrypted).toFloat()
    }

    fun decryptDouble(encrypted: String): Double? {
        return decryptString(encrypted).toDouble()
    }

    private fun encrypt(raw: ByteArray, clear: ByteArray): ByteArray {
        return try {
            val skeySpec = SecretKeySpec(raw, "AES")
            val ivSpec = IvParameterSpec(CRYPTO_IVX)
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec)
            cipher.doFinal(clear)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            raw
        }
    }

    private fun decrypt(raw: ByteArray, encrypted: ByteArray): ByteArray {
        return try {
            val skeySpec = SecretKeySpec(raw, "AES")
            val ivSpec = IvParameterSpec(CRYPTO_IVX)
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec)
            cipher.doFinal(encrypted)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            raw
        }
    }

    fun toHex(txt: String): String? {
        return toHex(txt.toByteArray())
    }

    fun fromHex(hex: String): String? {
        return String(toByte(hex))
    }

    fun toByte(hexString: String): ByteArray {
        val len = hexString.length / 2
        val result = ByteArray(len)
        for (i in 0 until len) result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                16).toByte()
        return result
    }

    fun toHex(buf: ByteArray?): String? {
        return try {
            if (buf == null) return ""
            val result = StringBuffer(2 * buf.size)
            for (i in buf.indices) {
                appendHex(result, buf[i])
            }
            result.toString()
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            null
        }
    }

    private val HEX = "0123456789ABCDEF"

    private fun appendHex(sb: StringBuffer, b: Byte) {
        sb.append(HEX.toCharArray()[b shl 4 and 0x0f]).append(HEX.toCharArray()[b and 0x0f])
    }

    infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)
    infix fun Int.shl(that: Byte): Int = this.shl(that.toInt()) // Not necessary in this case because no there's (Int shl Byte)
    infix fun Byte.shl(that: Byte): Int = this.toInt().shl(that.toInt()) // Not necessary in this case because no there's (Byte shl Byte)

    private infix fun Byte.and(that: Int): Int = this.toInt().and(that)
    infix fun Int.and(that: Byte): Int = this.and(that.toInt()) // Not necessary in this case because no there's (Int and Byte)
    infix fun Byte.and(that: Byte): Int = this.toInt().and(that.toInt())

    /**
     * This method generate key byte from pass phrase with strict bits size.
     *
     * @param passPhrase
     * @param keyLenghtInBits should be 128/192/256
     * @return raw bytes of keyLenghtInBits/8 size
     */
    private fun getKeyBytes(passPhrase: String, keyLenghtInBits: Int): ByteArray {
        var passPhrase = passPhrase
        val lenInByte = keyLenghtInBits / 8
        passPhrase = passPhrase.trim { it <= ' ' }
        while (passPhrase.toByteArray().size < lenInByte) {
            passPhrase += "0000000000000000"
        }
        return Arrays.copyOf(passPhrase.toByteArray(), lenInByte)
    }

    fun encryptWithBase64(data: String): String? {
        return try {
            val passphrase = String(rawKey)
            val encrypted = Base64.encodeToString(encrypt(data)!!.toByteArray(), BASE64_FLAGS)
            Log.d(TAG, "Encrypting \"$data\" with passphrase \"$passphrase\"=\"$encrypted\"")
            encrypted
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            data
        }
    }

    fun decryptWithBase64(data: String?): String? {
        return try {
            val encrypted = Base64.decode(data, BASE64_FLAGS)
            val encrpytedString = String(encrypted)
            decryptString(encrpytedString)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            data
        }
    }

    fun md5(input: String): String? {
        return try {
            val md5 = MessageDigest.getInstance("MD5")
            val hash = md5.digest(input.toByteArray())
            toHexString(hash)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            null
        }
    }

    fun sha1(input: String): String? {
        return try {
            val sha1 = MessageDigest.getInstance("SHA-1")
            val hash = sha1.digest(input.toByteArray())
            toHexString(hash)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            null
        }
    }

    fun sha512(input: String): String? {
        return try {
            val sha1 = MessageDigest.getInstance("SHA-512")
            val hash = sha1.digest(input.toByteArray())
            toHexString(hash)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            null
        }
    }

    fun sha256(input: String): String? {
        return try {
            val sha1 = MessageDigest.getInstance("SHA-256")
            val hash = sha1.digest(input.toByteArray())
            toHexString(hash)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
            null
        }
    }

    private fun toHexString(bytes: ByteArray): String? {
        val hexString = StringBuilder()
        for (b in bytes) {
            val hex = Integer.toHexString(0xFF and b.toInt())
            // Append leading '0' for one digit ints
            if (hex.length == 1) {
                hexString.append("0").append(hex)
            } else {
                hexString.append(hex)
            }
        }
        return hexString.toString()
    }


}