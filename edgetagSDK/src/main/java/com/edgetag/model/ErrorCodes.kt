package com.edgetag.model

object ErrorCodes {

    const val ERROR_CODE_NO_ERROR =0
    const val ERROR_CODE_TAG_ERROR =1
    const val ERROR_CODE_END_POINT_URL_NOT_PROPER =2
    const val ERROR_CODE_MANIFEST_NOT_AVAILABLE =3
    const val ERROR_CODE_SDK_NOT_ENABLED =4
    const val ERROR_CODE_SDK_INTERNAL_ERROR =5
    const val ERROR_CODE_NETWORK_ERROR =6
    const val ERROR_CODE_CONSENT_ERROR =7
    const val ERROR_CODE_NO_CONSENT_PROVIDED =8
    const val ERROR_CODE_NO_PROVIDER_FOUND =9
    const val ERROR_CODE_CONSENT_AVAILABLE_BUT_NO_PROVIDER_FOUND =10
    const val ERROR_CODE_KEY_NOT_ALLOWED =11
    const val ERROR_CODE_POST_DATA_ERROR =12
    const val ERROR_CODE_GET_DATA_ERROR =13
    const val ERROR_CODE_GET_KEY_ERROR =14
    const val ERROR_CODE_DATA_MISSING_ERROR =15
    const val ERROR_CODE_JSON_PARSING_ERROR =16
    const val ERROR_CODE_GET_AD_ID_ERROR =17


    const val ERROR_CODE_SDK_NOT_ENABLED_MSG = "SDK is not initialized"
    const val ERROR_CODE_KEY_NOT_ALLOWED_MSG = "Key does not belong to the permitted list of keys , Permmited keys: email, phone, firstName, lastName, gender, dateOfBirth, country, state, city, zip"
}