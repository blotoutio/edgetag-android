package com.edgetag.providers.blotoutcloud.repository.data

interface SharedPreferenceSecureVault {
    fun storeString(key:String,value:String?)
    fun fetchString(key:String) : String

    fun storeLong(key:String,value:Long)
    fun fetchLong(key:String) : Long

    fun storeBoolean(key:String,value:Boolean)
    fun fetchBoolean(key:String) : Boolean
}