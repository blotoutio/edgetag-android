package com.edgetag.network

import android.util.Log
import com.edgetag.DependencyInjectorImpl
import com.edgetag.model.*
import com.edgetag.model.edgetag.EdgetagMetaData
import com.edgetag.model.edgetag.ManifestConfigurationResponse
import com.edgetag.util.Constant
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

class RemoteApiDataSource(private val remoteApiService: RemoteApiService) {

  suspend fun getSDKManifest(): Result<ManifestConfigurationResponse?> {
    return processNetworkResponse(remoteApiService.getSDKManifest())
  }

  fun postEvents(events: EdgetagMetaData): Call<Any> {
    return remoteApiService.postEvents(events)
  }

  fun postConsentEvents(events: EdgetagMetaData): Call<Any> {
    return remoteApiService.postConsentEvents(events)
  }

  private fun <T> processNetworkResponse(response: Response<T>): Result<T?> {
    return try {
      when (response.isSuccessful) {
        true -> {
          response.headers().get("set-cookie")!!.saveCookies()
          Result.Success(response.body())
        }
        false -> {
          val errorResponseString = response.errorBody()?.string()
          Result.Error(
            errorData = InternalError(code = response.code(), msg = response.message())
          )
        }
      }
    } catch (e: Exception) {
      Result.Error(errorData = InternalError(code = ErrorCodes.ERROR_CODE_NETWORK_ERROR))
    }

  }

   fun String.saveCookies()  {
    if (DependencyInjectorImpl.getInstance().getSecureStorageService()
        .fetchString(Constant.TAG_USER_ID).isEmpty()
    ) {
      val cookiesArray = split(";")
      for (cookie in cookiesArray) {
        val individualcookie = cookie.split("=")
        if (individualcookie[0].contentEquals(Constant.TAG_USER_ID))
          DependencyInjectorImpl.getInstance().getSecureStorageService()
            .storeString(Constant.TAG_USER_ID, individualcookie[1])
      }
    }
  }
}
