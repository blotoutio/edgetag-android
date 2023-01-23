package com.edgetag.util

object Constant {

  const val AD_ID = "ad_id"
  const val MANIFEST_DATA = "manifest_data"
  const val CONSENT_DATA = "consent_data"
  const val SDK_END_POINT_URL = "endPoint"
  const val CONSENT_AVAILABLE = "consent_available"
  const val TAG_USER_ID = "EdgeTagUserId"
  const val REFFERAL = "refferal"
  const val KEY = "key"
  const val VALUE = "value"
  const val STORAGE_DATA = "kv"
  const val PROVIDER_DATA = "providerData"

  const val EDGE_TAG_REST_API_MANIFEST_PULL_PATH = "/init"
  const val EDGE_TAG_REST_API_EVENTS_PUSH_PATH = "/tag"
  const val EDGE_CONSENT_REST_API_EVENTS_PUSH_PATH = "/consent"
  const val EDGE_USER_EVENTS_PUSH_PATH ="/user"
  const val EDGE_EVENTS_DATA ="/data"
  const val EDGE_EVENTS_GET_KEY ="/keys"

  const val BOSDK_MAJOR_VERSION = 0
  const val BOSDK_MINOR_VERSION = 5
  const val BOSDK_PATCH_VERSION = 1

  val allowedUserKeys = arrayOf("email", "phone", "firstName","lastName","gender","dateOfBirth","country","state","city","zip")

}
