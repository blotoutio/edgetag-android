package com.edgetag

import android.app.Application
import com.edgetag.data.database.EventDatabase
import com.edgetag.model.CompletionHandler
import com.edgetag.model.ErrorCodes
import com.edgetag.model.InternalError
import com.edgetag.network.HostConfiguration
import com.edgetag.repository.data.SharedPreferenceSecureVault
import com.edgetag.repository.impl.SharedPreferenceSecureVaultImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

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
        Assert.assertEquals(ErrorCodes.ERROR_CODE_END_POINT_URL_NOT_PROPER, errorCode)
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
