package com.edgetag.providers.blotoutcloud.utils

import android.app.Activity
import android.content.pm.PackageManager

object Constant {


    const val IS_SDK_ENABLE = "IS_SDK_ENABLE"
    const val BO_ANALYTICS_USER_BIRTH_TIME_STAMP = "BO_ANALYTICS_USER_BIRTH_TIME_STAMP"
    const val SDK_KEY = "sdk_key"


    const val BO_APPLICATION_INSTALLED = "Application Installed"
    const val BO_APPLICATION_UPDATED ="Application Updated"
    const val BO_DEEP_LINK_OPENED = "Deep Link Opened"
    const val BO_APPLICATION_BACKGROUNDED="Application Backgrounded"
    const val BO_SDK_START= "sdk_start"
    const val BO_EVENT_ERROR_NAME= "error"
    const val BO_VISIBILITY_VISIBLE= "visibility_visible"
    const val BO_VISIBILITY_HIDDEN= "visibility_hidden"

    //Geasture Events

    const val BO_EVENT_SCROLL_NAME= "scroll"
    const val BO_EVENT_CLICK_NAME= "click"
    const val BO_EVENT_MULTI_CLICK_NAME= "multiclick"
    const val BO_EVENT_TOUCH_NAME= "touchend"
    const val BO_EVENT_LONG_TOUCH_NAME= "longtouch"
    const val BO_EVENT_KEY_RELEASE_NAME= "keyrelease"
    const val BO_EVENT_KEY_PATH_NAME= "path"


    const val BO_ANALYTICS_USER_UNIQUE_KEY =   "UserUniqueId"
    const val BO_VERSION_KEY = "BOVersionKey"




    fun getProviderInfo():HashMap<String,Boolean>{
        val providerInfo = hashMapOf<String,Boolean>()
        providerInfo.put("BlotoutCloud",true)
        return providerInfo
    }

    fun Activity.getScreenName(): String {
        val packageManager = this.packageManager
        return packageManager.getActivityInfo(
            this.componentName, PackageManager.GET_META_DATA
        ).loadLabel(packageManager).toString()
    }

}