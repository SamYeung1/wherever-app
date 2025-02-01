package com.samyeung.wherever.fragment.page

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread

class ExploreTraceLiveData : MutableLiveData<ExploreTraceLiveData.Model>() {
    fun setNumberOfLatestTrace(number: Int) {
        this.value = Model(number)
    }

    companion object {
        private var sInstance: ExploreTraceLiveData? = null

        @MainThread
        @JvmStatic
        fun get(): ExploreTraceLiveData {
            if (sInstance == null) {
                sInstance = ExploreTraceLiveData()
            }
            return sInstance!!
        }
        fun clear(){
            this.sInstance = null
        }
    }

    data class Model(val numberOfLatestTrace: Int)
}
