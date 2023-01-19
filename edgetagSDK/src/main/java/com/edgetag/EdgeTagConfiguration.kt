package com.edgetag

import android.webkit.URLUtil
import com.edgetag.model.ErrorCodes
import com.edgetag.util.Constant

class EdgeTagConfiguration {


  var endPointUrl: String? = null
  var disableConsentCheck:Boolean = true

  companion object {
    private const val TAG = "AnalyticsConfiguration"
  }

  fun validateRequest(): Int {
    if (endPointUrl.isNullOrEmpty() || !URLUtil.isValidUrl(endPointUrl))
      return ErrorCodes.ERROR_CODE_END_POINT_URL_NOT_PROPER
    save()
    return ErrorCodes.ERROR_CODE_NO_ERROR
  }

  private fun save() {
    try {
      val storageService = DependencyInjectorImpl.getInstance().getSecureStorageService()
      storageService.storeString(Constant.SDK_END_POINT_URL, endPointUrl!!)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}