package com.edgetag.repository

import android.app.Application
import android.content.SharedPreferences
import com.edgetag.DependencyInjectorImpl
import com.edgetag.MockTestConstants
import com.edgetag.data.database.EventDatabase
import com.edgetag.deviceinfo.device.DeviceInfo
import com.edgetag.model.ErrorCodes
import com.edgetag.network.HostConfiguration
import com.edgetag.repository.data.SharedPreferenceSecureVault
import com.edgetag.util.Constant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EventRepositoryTest {

    lateinit var eventRepository: EventRepository

    @Mock
    lateinit var secureVault: SharedPreferenceSecureVault


    private lateinit var context: Application
    private lateinit var editor: SharedPreferences.Editor

    @Mock
    private lateinit var eventDatabase: EventDatabase


    private lateinit var deviceInfo: DeviceInfo

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = Mockito.mock(Application::class.java)
        val blotoutAnalyticsConfiguration = MockTestConstants.setupBlotoutAnalyticsConfiguration()
        Mockito.mock(SharedPreferences::class.java)
        editor = Mockito.mock(SharedPreferences.Editor::class.java)
        val hostConfiguration =
            HostConfiguration(baseUrl = blotoutAnalyticsConfiguration.endPointUrl)

        DependencyInjectorImpl.init(
            application = context,
            secureStorageService = secureVault,
            hostConfiguration = hostConfiguration, eventDatabase
        )
        eventRepository = EventRepository(secureVault)

        deviceInfo = DeviceInfo(context)
    }

    @Test
    fun `test tag events when consent not provided`() {
        val result = eventRepository.prepareTagEvent(
            "TagName",
            MockTestConstants.getTagData(),
            MockTestConstants.getProviderData()
        )
        when (result) {
            is com.edgetag.model.Result.Success -> assert(false)
            is com.edgetag.model.Result.Error -> Assert.assertEquals(
                ErrorCodes.ERROR_CODE_NO_CONSENT_PROVIDED,
                result.errorData.code
            )
        }
    }

    @Test
    fun `test  consent when consent not provided`() {
        DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse =
            MockTestConstants.getManifestResponse()
        val result = eventRepository.prepareConsent(MockTestConstants.getConsentData())
        when (result) {
            is com.edgetag.model.Result.Success -> {
                assert(true)
            }
            is com.edgetag.model.Result.Error -> assert(false)
        }
    }

    @Test
    fun `test tag events when consent provided`() {
        DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse =
            MockTestConstants.getManifestResponse()
        Mockito.`when`(
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .fetchString(Constant.CONSENT_DATA)
        ).thenReturn(MockTestConstants.getConsentData().toString())
        val result = eventRepository.prepareTagEvent(
            "TagName",
            MockTestConstants.getTagData(),
            MockTestConstants.getProviderData()
        )
        when (result) {
            is com.edgetag.model.Result.Success -> assert(true)
            is com.edgetag.model.Result.Error -> assert(false)
        }
    }

    @Test
    fun `when tag event throw error`() {
        DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse =
            MockTestConstants.getManifestResponse()
        Mockito.`when`(
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .fetchString(Constant.CONSENT_DATA)
        ).thenReturn(MockTestConstants.getConsentData().toString())
        val result = eventRepository.prepareTagEvent(
            "TagName",
            MockTestConstants.getTagData(),
            MockTestConstants.getProviderData()
        )
        when (result) {
            is com.edgetag.model.Result.Success -> assert(true)
            is com.edgetag.model.Result.Error -> Assert.assertEquals(
                ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
                result.errorData.code
            )
        }
    }

    @Test
    fun `when consent event throw error`() {
        DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse =
            MockTestConstants.getManifestResponse()

        val result = eventRepository.prepareConsent(MockTestConstants.getConsentData())
        when (result) {
            is com.edgetag.model.Result.Success -> assert(true)
            is com.edgetag.model.Result.Error -> Assert.assertEquals(
                ErrorCodes.ERROR_CODE_SDK_INTERNAL_ERROR,
                result.errorData.code
            )
        }
    }

    @Test
    fun ` tag event with no consent for providers`() {
        DependencyInjectorImpl.getInstance()
            .getManifestRepository().manifestConfigurationResponse =
            MockTestConstants.getManifestResponse()
        Mockito.`when`(
            DependencyInjectorImpl.getInstance().getSecureStorageService()
                .fetchString(Constant.CONSENT_DATA)
        ).thenReturn(MockTestConstants.getAllConsentDataFalse().toString())
        val result = eventRepository.prepareTagEvent(
            "TagName",
            MockTestConstants.getTagData(),
            MockTestConstants.getAllProviderDataFalse()
        )
        when (result) {
            is com.edgetag.model.Result.Success -> assert(true)
            is com.edgetag.model.Result.Error -> Assert.assertEquals(
                ErrorCodes.ERROR_CODE_NO_PROVIDER_FOUND,
                result.errorData.code
            )
        }
    }
}