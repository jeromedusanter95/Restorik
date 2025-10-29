package com.jeromedusanter.restorik.core.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class CapturePhotoContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(context, CameraActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        if (resultCode == Activity.RESULT_OK) intent?.data else null
}