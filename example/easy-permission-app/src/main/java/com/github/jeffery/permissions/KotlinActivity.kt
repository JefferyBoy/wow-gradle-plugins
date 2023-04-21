package com.github.jeffery.permissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import top.amake.permission.PermissionResult
import top.amake.permission.annotation.Permission
import top.amake.permission.annotation.PermissionDenied

/**
 * @author mxlei
 * @date   2022/9/22
 */
class KotlinActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_write).setOnClickListener { requestStoragePermission() }
        findViewById<View>(R.id.btn_write).setOnClickListener(this)
        findViewById<View>(R.id.btn_camera).setOnClickListener(this)
        findViewById<View>(R.id.btn_location).setOnClickListener(this)
        findViewById<View>(R.id.btn_voice).setOnClickListener(this)
        findViewById<View>(R.id.btn_normal_test).setOnClickListener(this)
    }

    @Permission(value = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun requestStoragePermission() {
        Log.d(TAG, "requestStoragePermission ok")
    }

    @Permission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationPermission() {
        Log.d(TAG, "requestLocationPermission ok")
    }

    @Permission(value = [Manifest.permission.RECORD_AUDIO])
    fun requestVoicePermission() {
        Log.d(TAG, "requestVoicePermission ok")
    }

    @Permission(value = [Manifest.permission.CAMERA])
    fun requestCameraPermission() {
        Log.d(TAG, "requestCameraPermission ok")
    }

    @PermissionDenied
    fun onPermissionDenied(results: List<PermissionResult?>) {
        Log.d(TAG, "requestPermissionDenied")
        for (result in results) {
            println(result)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_camera -> requestCameraPermission()
            R.id.btn_write -> requestStoragePermission()
            R.id.btn_location -> requestLocationPermission()
            R.id.btn_voice -> requestVoicePermission()
            R.id.btn_normal_test -> KotlinNormalClass().normalClassTest(v.context)
            else -> {}
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}