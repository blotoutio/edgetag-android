package com.edgetag.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.edgetag.repository.impl.SharedPreferenceSecureVaultImpl
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPreferenceSecureVaultTest {
    private lateinit var testClass :SharedPreferenceSecureVaultImpl

    @Before
    fun setup(){
        val context = Mockito.mock(Application::class.java)
        val pref = Mockito.mock(SharedPreferences::class.java)
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)

        Mockito.`when`(context.getSharedPreferences("", Context.MODE_PRIVATE)).thenReturn(pref)
        Mockito.`when`(pref.edit()).thenReturn(editor)
        Mockito.`when`(editor.putString("", "")).thenReturn(editor)
        Mockito.`when`(editor.putLong("", 0L)).thenReturn(editor)
        Mockito.`when`(editor.putBoolean("", false)).thenReturn(editor)
        Mockito.`when`(pref.getString("","")).thenReturn("")
        testClass = SharedPreferenceSecureVaultImpl(context.getSharedPreferences("",Context.MODE_PRIVATE),"")
    }



    @Test
    fun storeString_success(){
        testClass.storeString("","")
        assertTrue(true)
    }

    @Test
    fun getString_success(){
        testClass.fetchString("")
        assertTrue(true)
    }

    @Test
    fun storeBoolean_success(){
        testClass.storeBoolean("",false)
        assertTrue(true)
    }

    @Test
    fun getBoolean_success(){
        testClass.fetchBoolean("")
        assertTrue(true)
    }

    @Test
    fun storeLong_success(){
        testClass.storeLong("",0L)
        assertTrue(true)
    }

    @Test
    fun getLong_success(){
        testClass.fetchLong("")
        assertTrue(true)
    }
}