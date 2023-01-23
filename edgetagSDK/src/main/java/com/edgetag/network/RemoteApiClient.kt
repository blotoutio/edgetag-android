package com.edgetag.network

import com.edgetag.DependencyInjectorImpl
import com.edgetag.model.ErrorCodes
import com.edgetag.model.InternalError
import com.edgetag.model.Result
import com.edgetag.util.Constant
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteApiClient(private val hostConfiguration: HostConfiguration) {

    private fun getAdapter(): Retrofit {
        return Retrofit.Builder().run{
            baseUrl(hostConfiguration.baseUrl!!)
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addCallAdapterFactory(RetryCallAdapterFactory.create())
            client(createOkHttpClient())
            build()
        }

    }

    fun getRemoteApiService(): RemoteApiService {
        return getAdapter().create(RemoteApiService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder().
                addHeader("Content-Type","application/json").
                addHeader("cookie", Constant.TAG_USER_ID+"="+DependencyInjectorImpl.getInstance().getSecureStorageService().fetchString(Constant.TAG_USER_ID)).build()
                chain.proceed(request)
            }.addInterceptor(ReceivedCookiesInterceptor())

            connectTimeout(hostConfiguration.mConnectionTimeout, TimeUnit.MILLISECONDS)
            readTimeout(hostConfiguration.mReadTimeout, TimeUnit.MILLISECONDS)
            writeTimeout(hostConfiguration.mWriteTimeout, TimeUnit.MILLISECONDS)
            Dispatcher().maxRequests = 10
            build()
        }
    }

    class ReceivedCookiesInterceptor: Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse = chain.proceed(chain.request())
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                for (header in originalResponse.headers("Set-Cookie")) {
                    header.saveCookies()
                }
            }

            return originalResponse
        }

        fun <T> processNetworkResponse(response: retrofit2.Response<T>): Result<T?> {
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
}
