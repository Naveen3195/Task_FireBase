package com.example.task.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.task.base.BaseViewModel
import com.example.task.network.Response
import com.example.task.ui.fragment.AddFragment
import com.example.task.ui.fragment.HomeFragment
import com.example.task.ui.fragment.MainHomeFragment
import com.example.task.ui.fragment.ProfileFragment
import javax.inject.Inject

class BottomNavigation @Inject constructor() : BaseViewModel() {
    var fragmentList = MutableLiveData(ArrayList<Fragment>())
    var previousFragment = MutableLiveData(Fragment())

    init {
        fragmentList.value!!.add(AddFragment())
        fragmentList.value!!.add(HomeFragment())
        fragmentList.value!!.add(ProfileFragment())
    }

    fun response(): MutableLiveData<Response> {
        return response
    }

}