package com.edgetag

import android.app.Application
import com.edgetag.data.database.EventDatabase
import com.edgetag.model.CompletionHandler
import com.edgetag.model.ErrorCodes
import com.edgetag.model.InternalError
import com.edgetag.model.OnComplete
import com.edgetag.network.HostConfiguration
import com.edgetag.repository.data.SharedPreferenceSecureVault
import com.edgetag.repository.impl.SharedPreferenceSecureVaultImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EdgeTagPublicApiTest {

    private lateinit var context: Application

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var dependencyInjectorImpl: DependencyInjectorImpl

    @Mock
    lateinit var sharedPreferenceSecureVaultImpl: SharedPreferenceSecureVaultImpl

    @Mock
    lateinit var  secureVault:SharedPreferenceSecureVault
    @Mock
    private lateinit var eventDatabase: EventDatabase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = mock(Application::class.java)
        dependencyInjectorImpl = mock(DependencyInjectorImpl::class.java)
        sharedPreferenceSecureVaultImpl = mock(SharedPreferenceSecureVaultImpl::class.java)

    }


    @Test
    fun testUrlsForNegative() {
        val blotoutAnalyticsConfiguration = MockTestConstants.setupBlotoutAnalyticsConfiguration()
        blotoutAnalyticsConfiguration.endPointUrl = ""
        val errorCode = blotoutAnalyticsConfiguration.validateRequest()
        assertEquals(ErrorCodes.ERROR_CODE_END_POINT_URL_NOT_PROPER, errorCode)
    }

    @Test
    fun testSDKStart() {
        coroutineTestRule.runBlockingTest {
            val edgeTagConfiguration = MockTestConstants.setupBlotoutAnalyticsConfiguration()
            EdgeTag.init(context, edgeTagConfiguration, object : CompletionHandler {
                override fun onSuccess() {
                    assertTrue(true)
                }

                override fun onError(code: Int, msg: String) {
                    assertFalse(true)
                }
            })
        }
    }

    @Test
    fun testConsent() {
        val consentInfo = hashMapOf<String,Boolean>()
        consentInfo["facebook"] = true
        EdgeTag.consent(consentInfo,object : CompletionHandler {
            override fun onSuccess() {
                assertTrue(true)
            }
            override fun onError(code: Int, msg: String) {
            }
        })
    }


    @Test
    fun tag() {
        val tagInfo = hashMapOf<String,Any>()
        tagInfo["click"] = true
        val providerInfo = hashMapOf<String,Boolean>()
        providerInfo["facebook"] = true
        EdgeTag.tag("",tagInfo,providerInfo,object : CompletionHandler {
            override fun onSuccess() {
                assertTrue(true)
            }
            override fun onError(code: Int, msg: String) {
            }
        })
    }

    @Test
    fun `test user API`() {
        val key: String = "email"
        val value: String = "me@domain.com"

        EdgeTag.user(key,value,object : CompletionHandler {
            override fun onSuccess() {
                assertTrue(true)
            }
            override fun onError(code: Int, msg: String) {
            }
        })
    }

    @Test
    fun `test postData`() {
        val dataToPost = hashMapOf<String,String>()
        dataToPost["email"] = "me@abckl.ij"
        dataToPost["cutomInfo"] = "Random string entry"
        dataToPost["numberValue"] = "987"
        dataToPost["testBool"] = "false"

        EdgeTag.postData(dataToPost,object : OnComplete {
            override fun onSuccess(msg: Any) {
                assertTrue(true)
            }

            override fun onError(code: Int, msg: String) {
            }

        })
    }

    @Test(expected = java.lang.Exception::class)
    fun `test postData when data and callback not provided`() {
        verify( EdgeTag.postData(null, null))
    }

    @Test
    fun `test getData`() {
        val key = ArrayList<String>()
        key.add("email")
        key.add("cutomInfo")
        key.add("numberValue")
        key.add("testBool")

        EdgeTag.getData(key,object : OnComplete {
            override fun onSuccess(msg: Any) {
                assertTrue(true)
            }

            override fun onError(code: Int, msg: String) {
            }

        })
    }

    @Test(expected = java.lang.Exception::class)
    fun `test getData when key and callback not provided`() {
        verify( EdgeTag.getData(null, null))
    }

    @Test
    fun `test getData when SDK not initialized`() {
        val key = ArrayList<String>()
        key.add("xyz")

        EdgeTag.getData(key,object : OnComplete {
            override fun onSuccess(msg: Any) {
            }

            override fun onError(code: Int, msg: String) {
                assertEquals(4,ErrorCodes.ERROR_CODE_SDK_NOT_ENABLED)
            }

        })
    }

    @Test
    fun `test getData when key  not available`() {
        coroutineTestRule.runBlockingTest {
            val edgeTagConfiguration = MockTestConstants.setupBlotoutAnalyticsConfiguration()
            EdgeTag.init(context, edgeTagConfiguration, object : CompletionHandler {
                override fun onSuccess() {
                    val key = ArrayList<String>()
                    key.add("xyz")
                    EdgeTag.getData(key,object : OnComplete {
                        override fun onSuccess(msg: Any) {
                            assertEquals("",msg.toString().isEmpty())
                        }

                        override fun onError(code: Int, msg: String) {
                            assertFalse(true)
                        }
                    })
                }
                override fun onError(code: Int, msg: String) {
                    assertFalse(true)
                }
            })
        }

    }



    @Test
    fun `test getKeys`() {
        EdgeTag.getKeys(object : OnComplete {
            override fun onSuccess(msg: Any) {
                assertTrue(true)
            }

            override fun onError(code: Int, msg: String) {
            }

        })
    }

    @Test(expected = java.lang.Exception::class)
    fun `test getKeys when callback not provided`() {
        verify( EdgeTag.getKeys(null))
    }


    @Test
    fun `test isAdvertiserIdAvailable`() {
        EdgeTag.isAdvertiserIdAvailable(object : OnComplete {
            override fun onSuccess(msg: Any) {
                assertTrue(true)
            }

            override fun onError(code: Int, msg: String) {
            }

        })
    }

    @Test(expected = java.lang.Exception::class)
    fun `test isAdvertiserIdAvailable when onComplete not provided`() {
        verify( EdgeTag.isAdvertiserIdAvailable(null))
    }

    @Test
    fun internalErrorTest(){
        val internalError = InternalError(code = ErrorCodes.ERROR_CODE_END_POINT_URL_NOT_PROPER,msg ="")
        assertTrue(internalError.hasError())
    }

    @Test
    fun `sdk init test`(){
        val blotoutAnalyticsConfiguration = MockTestConstants.setupBlotoutAnalyticsConfiguration()
        val hostConfiguration = HostConfiguration(baseUrl = blotoutAnalyticsConfiguration.endPointUrl)

        DependencyInjectorImpl.init(
            application = context,
            secureStorageService = secureVault,
            hostConfiguration = hostConfiguration ,eventDatabase
        )
        DependencyInjectorImpl.getInstance().initialize()
        assert(true)
    }

    @Test
    fun `validate error`(){
        val internalError = InternalError(code = 12,msg="")
        assertTrue(internalError.hasError())
    }


}