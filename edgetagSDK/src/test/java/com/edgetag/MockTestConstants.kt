package com.edgetag
import com.edgetag.model.edgetag.Capture
import com.edgetag.model.edgetag.ManifestConfigurationResponse
import com.edgetag.model.edgetag.Result
import com.edgetag.model.edgetag.Rules

object MockTestConstants {

    fun setupBlotoutAnalyticsConfiguration():EdgeTagConfiguration {
        val edgeTagAnalyticsConfiguration = EdgeTagConfiguration()
        edgeTagAnalyticsConfiguration.endPointUrl = "https://stage.blotout.io/sdk/"
        edgeTagAnalyticsConfiguration.disableConsentCheck = true
        return edgeTagAnalyticsConfiguration
    }

    fun getManifestResponse():ManifestConfigurationResponse{
        val manifestConfigurationResponse = ManifestConfigurationResponse()
        val result = Result()
        result._package = "facebook"
        val rule = Rules()
        val capture = Capture()
        capture.key = "fbclid"
        capture.persist ="storage"
        capture.type ="query"
        rule.capture = listOf(capture)
        result.rules =rule
        manifestConfigurationResponse.result = listOf(result)
        return manifestConfigurationResponse
    }

    fun getConsentData():HashMap<String,Boolean>{
        val consentData = hashMapOf<String,Boolean>()
        consentData.put("facebook",true)
        consentData.put("google",true)
        consentData.put("all",true)
        return consentData
    }

    fun getAllConsentDataFalse():HashMap<String,Boolean>{
        val consentData = hashMapOf<String,Boolean>()
        consentData.put("all",false)
        return consentData
    }

    fun getAllProviderDataFalse():HashMap<String,Boolean>{
        val providerData = hashMapOf<String,Boolean>()
        providerData.put("all",false)
        return providerData
    }

    fun getProviderData():HashMap<String,Boolean>{
        val providerData = hashMapOf<String,Boolean>()
        providerData.put("facebook",true)
        providerData.put("google",true)
        providerData.put("all",true)
        return providerData
    }

    fun getTagData():HashMap<String,Any>{
        val tagData = hashMapOf<String,Any>()
        tagData.put("OnClick",true)
        tagData.put("Currency",10)
        return tagData
    }
}
