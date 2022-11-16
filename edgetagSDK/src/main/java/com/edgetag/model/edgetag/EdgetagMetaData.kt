package com.edgetag.model.edgetag

import com.edgetag.util.Constant
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class EdgetagMetaData {

    @SerializedName("pageUrl")
    @Expose
    var pageUrl: String? = null

    @SerializedName("userAgent")
    @Expose
    var userAgent: String? = null

    @SerializedName("consentString")
    @Expose
    var consentString: HashMap<String, Boolean>? = null

    @SerializedName("storage")
    @Expose
    var storage: Storage? = null

    @SerializedName("eventName")
    @Expose
    var eventName: String? = null

    @SerializedName("data")
    @Expose
    var data: HashMap<String, Any>? = null

    @SerializedName("providers")
    @Expose
    var providers: HashMap<String, Boolean>? = null

    @SerializedName(Constant.TAG_USER_ID)
    @Expose
    var tag_user_id: String? = null

    @SerializedName(Constant.KEY)
    @Expose
    var key: String? = null

    @SerializedName(Constant.VALUE)
    @Expose
    var value: String? = null

}
