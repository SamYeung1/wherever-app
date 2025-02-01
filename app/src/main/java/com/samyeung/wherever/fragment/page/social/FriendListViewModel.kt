package com.samyeung.wherever.fragment.page.social

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.samyeung.wherever.model.UserProfile

class FriendListViewModel : ViewModel() {
    val users: MutableLiveData<MutableList<UserProfile>> by lazy {
        MutableLiveData<MutableList<UserProfile>>().apply {
            this.value = arrayListOf()
        }
    }
    fun onDestroy(){
        this.users.value!!.clear()
    }

}
