package com.github.jeffery.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import top.amake.permission.annotation.Permission

/**
 * @author mxlei
 * @date   2022/9/23
 */
class KotlinNormalClass {

    @Permission(Manifest.permission.RECORD_AUDIO)
    fun normalClassTest(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            println("normalClassTest success")
        } else {
            throw RuntimeException("normalClassTest fail")
        }
    }
}