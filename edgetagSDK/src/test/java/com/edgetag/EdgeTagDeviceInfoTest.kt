package com.edgetag

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.Display
import android.view.WindowManager
import com.edgetag.deviceinfo.device.DeviceInfo
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class EdgeTagDeviceInfoTest {

    private lateinit var context: Application
    private lateinit var deviceInfo: DeviceInfo
    private lateinit var windowManagerMock:WindowManager
    private lateinit var display: Display
    private lateinit var resources:Resources

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = Mockito.mock(Application::class.java)
        deviceInfo = DeviceInfo(context)
        windowManagerMock = Mockito.mock(WindowManager::class.java)
        Mockito.`when`(context.getSystemService(Context.WINDOW_SERVICE)).thenReturn(windowManagerMock)

        display = Mockito.mock(Display::class.java)
        Mockito.`when`(windowManagerMock.defaultDisplay).thenReturn(display)

        resources = Mockito.mock(Resources::class.java)
        Mockito.`when`(context.resources).thenReturn(resources)

    }


    @Test
    fun testDeviceInfo() {
        Assert.assertNotNull(deviceInfo)
    }

    @Test
    fun testSDKVersion() {
        Assert.assertNotNull(deviceInfo.sdkVersion)
    }


    @Test
    fun testDeviceWidth() {
        Assert.assertNotNull(deviceInfo.screenWidth)
    }

    @Test
    fun testDeviceHeight() {
        Assert.assertNotNull(deviceInfo.screenHeight)
    }

    @Test
    fun testDeviceLocale() {
        val config = Mockito.mock(Configuration::class.java)
        Mockito.`when`(context.resources.configuration).thenReturn(config)
        Assert.assertNull(deviceInfo.deviceLocale)
    }

}
