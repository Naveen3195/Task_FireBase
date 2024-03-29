package com.example.task.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.task.network.Response
import com.example.task.ui.BottomNavigation
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {
    val response = MutableLiveData<Response>()

    @Inject lateinit var navHelper: NavigationHelper

    @Inject lateinit var validate: Validators

    @Inject lateinit var sharHelp: SharedHelper
}