package com.example.task.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.task.ui.models.resmodel.ErrorModel
import com.google.gson.Gson
import okhttp3.ResponseBody
import javax.inject.Inject

class NavigationHelper @Inject constructor()  {

    fun setIntent(cObjection: Class<*>, isFrom: Int, activity: Activity) {
        activity.startActivity(Intent(activity, cObjection))
        when (isFrom) {
            1 -> {
                activity.finish()
            }

            2 -> {
                activity.finishAffinity()
            }
        }
    }

    fun moveFrag(
        fragmentId: Int,
        fragment: androidx.fragment.app.Fragment,
        isBackStack: Boolean,
        frgManager: androidx.fragment.app.FragmentManager,
    ) {
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction = frgManager.beginTransaction()
        if (isBackStack) {
            fragmentTransaction.addToBackStack(fragment.tag)
        }

        fragment.let { fragmentTransaction.add(fragmentId, it) }
//        fragmentTransaction.addToBackStack(fragName)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun toast(context: Context, msg: String) {
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
    }

    fun errorHandler(errorBody: ResponseBody?): ErrorModel? {
        val gson = Gson()
        return gson.fromJson(errorBody?.string(), ErrorModel::class.java)
    }
}