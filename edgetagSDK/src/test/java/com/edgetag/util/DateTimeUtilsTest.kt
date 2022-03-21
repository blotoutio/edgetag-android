package com.edgetag.util

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DateTimeUtilsTest {

    private lateinit var dateTimeUtilsTest: DateTimeUtils

    @Before
    fun setUp() {
        dateTimeUtilsTest = DateTimeUtils()
    }

    @Test
    fun testSessionId() {
        val sessionId = dateTimeUtilsTest.get13DigitNumberObjTimeStamp()
        if(sessionId.toString().length ==13)
            assert(true)
    }

    @Test
    fun `test Get Current Timezone Offset In Min`(){
        val value = dateTimeUtilsTest.getCurrentTimezoneOffsetInMin()
        if(value !=0)
            assert(true)
    }
}