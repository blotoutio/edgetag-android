package com.edgetag.network

import android.app.Application
import android.content.SharedPreferences
import com.edgetag.CoroutineTestRule
import com.edgetag.DependencyInjectorImpl
import com.edgetag.MockTestConstants
import com.edgetag.data.database.EventDatabase
import com.edgetag.model.ErrorCodes
import com.edgetag.model.InternalError
import com.edgetag.repository.ManifestRepository
import com.edgetag.repository.data.ConfigurationDataManager
import com.edgetag.repository.data.SharedPreferenceSecureVault
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ManifestServiceTest {

    @Mock
    lateinit var configureDataManager: ConfigurationDataManager
    @Mock
    private lateinit var eventDatabase: EventDatabase

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var manifestRepository: ManifestRepository
    private lateinit var  context:Application
    @Mock
    lateinit var  secureVault:SharedPreferenceSecureVault
    private lateinit var  editor:SharedPreferences.Editor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        configureDataManager = Mockito.mock(ConfigurationDataManager::class.java)
        manifestRepository = ManifestRepository(configureDataManager)
        context = Mockito.mock(Application::class.java)
        val blotoutAnalyticsConfiguration = MockTestConstants.setupBlotoutAnalyticsConfiguration()
        Mockito.mock(SharedPreferences::class.java)
        editor = Mockito.mock(SharedPreferences.Editor::class.java)
        val hostConfiguration = HostConfiguration(baseUrl = blotoutAnalyticsConfiguration.endPointUrl)

        DependencyInjectorImpl.init(
            application = context,
            secureStorageService = secureVault,
            hostConfiguration = hostConfiguration ,eventDatabase
        )
    }

    @Test
    fun `test manifest when server provide response`() {

        val apiResult = com.edgetag.model.Result.Success(MockTestConstants.getManifestResponse())
        coroutineTestRule.runBlockingTest {
            Mockito.`when`(configureDataManager.downloadManifestConfiguration()).thenReturn(apiResult)

            when(manifestRepository.fetchManifestConfiguration()){
                is com.edgetag.model.Result.Success-> assert(true)
                else-> assert(false)
            }
        }

    }

    @Test
    fun `test manifest when server not provide response`() {
        val apiResult = com.edgetag.model.Result.Error(InternalError(code = ErrorCodes.ERROR_CODE_MANIFEST_NOT_AVAILABLE))
        coroutineTestRule.runBlockingTest {
            Mockito.`when`(configureDataManager.downloadManifestConfiguration()).thenReturn(apiResult)

            when(manifestRepository.fetchManifestConfiguration()){
                is com.edgetag.model.Result.Success-> assert(false)
                else-> assert(true)
            }
        }
    }
}