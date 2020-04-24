package com.lxl.othello.plus.update

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * User: lxl
 * Date: 2020/4/16
 * Desc: 应用内部升级
 * SinceVer: 1.0.0
 */
class InAppUpdate(private val mContext: Activity) {
    companion object {
        private const val TAG = "InAppUpdate"
    }

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(mContext)

    fun onResume() {
        val context = mContext
        // Creates instance of the manager.
        Log.e(TAG, "start query")

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        Log.e(TAG, "query task: $appUpdateInfoTask")

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.e(TAG, "on success:  $appUpdateInfo")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                Log.e(TAG, "on immediate:  $appUpdateInfo")
                // Request the update.
                onImmediateUpdate(appUpdateInfo)
            }
        }
        appUpdateInfoTask.addOnFailureListener {
            Log.e(TAG, "failure", it)

        }
    }

    private fun onImmediateUpdate(appUpdateInfo: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
            AppUpdateType.IMMEDIATE,
            // The current activity making the update request.
            mContext,
            // Include a request code to later monitor this update request.
            1999)
    }
}