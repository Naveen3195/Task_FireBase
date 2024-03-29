package com.example.task.base

import javax.inject.Inject

class Validators @Inject constructor() {

    fun isEmptyString(data: String?) : Boolean {
        return data?.length == 0 && data.isEmpty()
    }

    fun validateLength(data: String?, length: Int): Boolean {
        if(data == null || data.isEmpty()) {
            return true
        }
        return data.length == length
    }
}