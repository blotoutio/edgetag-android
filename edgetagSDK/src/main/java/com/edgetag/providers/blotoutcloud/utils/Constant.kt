package com.edgetag.provider.providers.blotoutcloud.utils

import android.app.Activity
import android.content.pm.PackageManager

object Constant {

    const val MANIFEST_DATA = "manifest_data"
    const val IS_SDK_ENABLE = "IS_SDK_ENABLE"
    const val BO_ANALYTICS_USER_BIRTH_TIME_STAMP = "BO_ANALYTICS_USER_BIRTH_TIME_STAMP"
    const val SDK_KEY = "sdk_key"
    const val SDK_END_POINT_URL = "SDK_END_POINT_URL"


    const val BO_EVENT_APPLICATION_OPENED = 11001
    const val BO_APPLICATION_OPENED = "Application Opened"
    const val BO_EVENT_APPLICATION_INSTALLED = 11002
    const val BO_APPLICATION_INSTALLED = "Application Installed"
    const val BO_EVENT_APPLICATION_UPDATED = 11003
    const val BO_APPLICATION_UPDATED ="Application Updated"
    const val BO_PUSH_NOTIFICATION_TAPPED = 11004
    const val BO_PUSH_NOTIFICATION_RECEIVED = 11005
    const val BO_REGISTER_FOR_REMOTE_NOTIFICATION = 11006
    const val BO_EVENT_DEEP_LINK_OPENED = 11007
    const val BO_DEEP_LINK_OPENED = "Deep Link Opened"
    const val BO_EVENT_APPLICATION_BACKGROUNDED = 11008
    const val BO_APPLICATION_BACKGROUNDED="Application Backgrounded"
    const val BO_APP_TRACKING = 11009
    const val BO_TRANSACTION_COMPLETED = 11010
    const val BO_EVENT_SDK_START = 11130
    const val BO_SDK_START= "sdk_start"
    const val BO_EVENT_ERROR = 11111
    const val BO_EVENT_ERROR_NAME= "error"

    const val BO_EVENT_VISIBILITY_VISIBLE = 11131
    const val BO_VISIBILITY_VISIBLE= "visibility_visible"

    const val BO_EVENT_VISIBILITY_HIDDEN = 11132
    const val BO_VISIBILITY_HIDDEN= "visibility_hidden"

    //Geasture Events
    const val BO_EVENT_SCROLL = 11114
    const val BO_EVENT_SCROLL_NAME= "scroll"
    const val BO_EVENT_CLICK = 11113
    const val BO_EVENT_CLICK_NAME= "click"
    const val BO_EVENT_MULTI_CLICK = 11112
    const val BO_EVENT_MULTI_CLICK_NAME= "multiclick"
    const val BO_EVENT_TOUCH = 11115
    const val BO_EVENT_TOUCH_NAME= "touchend"
    const val BO_EVENT_LONG_TOUCH = 11116
    const val BO_EVENT_LONG_TOUCH_NAME= "longtouch"
    const val BO_EVENT_KEY_RELEASE = 11117
    const val BO_EVENT_KEY_RELEASE_NAME= "keyrelease"
    const val BO_EVENT_KEY_PATH_NAME= "path"





    const val Api_Endpoint = "Api_Endpoint"
    const val EVENT_PATH = "Event_Path"
    const val Event_PHI_Public_Key = "PHI_Public_Key"
    const val Event_PII_Public_Key = "PII_Public_Key"
    const val Event_DeviceInfoGrain = "Event_Deviceinfo_Grain"
    const val Event_Push_System_Events = "SDK_Push_System_Events"
    const val Event_Push_System_Events_Allowed = "SDK_System_Events"

    const val BO_SYSTEM = "system"
    const val BO_CODIFIED = "codified"

    //transaction events
    const val BO_EVENT_TRANSACTION_NAME = "transaction"

    //item events
    const val BO_EVENT_TRANSACTION_ITEM_NAME = "item"

    //Persona events
    const val BO_EVENT_PERSONA_NAME = "persona"

    const val BO_SDK_REST_API_MANIFEST_PULL_PATH = "v1/manifest/pull"
    const val BO_SDK_REST_API_EVENTS_PUSH_PATH =  "v1/events/publish"

    const val BO_ANALYTICS_USER_UNIQUE_KEY =   "UserUniqueId"
    const val BO_VERSION_KEY = "BOVersionKey"

    const val BOSDK_MAJOR_VERSION = 0
    const val BOSDK_MINOR_VERSION = 0
    const val BOSDK_PATCH_VERSION = 1


    const val BO_PII = "pii"
    const val BO_PHI = "phi"

    const val BO_EVENT_MAP_ID = "map_id"
    const val BO_EVENT_MAP_Provider = "map_provider"


    ///Platform Constants
    const val BO_Android_Phone = 11
    const val BO_Android_Tablet = 12
    const val BO_Android_TV = 13
    const val BO_iPhone = 14
    const val BO_iPad = 15
    const val BO_Mobile_Web = 16
    const val BO_Web_Application = 17
    const val BO_Apple_TV = 18
    const val BO_Apple_Watch = 19
    const val BO_Android_Watch = 20
    const val BO_Amazon_Fire_TV = 21
    const val BO_Amazon_Fire_Stick = 22
    const val BO_Roku_TV = 23
    const val BO_HbbTV = 24
    const val BO_Smart_Android_TV = 25

    const val BO_Smart_TV_All = 40
    const val BO_Android_All = 50
    const val BO_ios_All = 60
    const val BO_Web_All = 70

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