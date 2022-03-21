package com.edgetag

import android.app.Application
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.edgetag.model.CompletionHandler
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

  lateinit var context: Context

  lateinit var edgeTagConfiguration: EdgeTagConfiguration


  @Before
  fun setUp() {
    context = InstrumentationRegistry.getInstrumentation().context
    edgeTagConfiguration.endPointUrl = "https://stage.blotout.io/sdk/"
    EdgeTag.init(
      application = context as Application,
      edgeTagConfiguration = edgeTagConfiguration, object : CompletionHandler {
        override fun onSuccess() {
        }

        override fun onError(code: Int, msg: String) {
          TODO("Not yet implemented")
        }


      })
  }

  @Test
  fun testKeys() {
    var errorCode = edgeTagConfiguration.validateRequest()
    Assert.assertEquals(0, errorCode)
  }
}
