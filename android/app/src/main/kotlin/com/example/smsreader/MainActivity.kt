package com.example.smsreader

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.Manifest
import androidx.core.app.ActivityCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.smsreader/sms"
    private val REQUEST_SMS_PERMISSION = 1

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getSms") {
                val sms = getSms()
                if (sms != null) {
                    result.success(sms)
                } else {
                    result.error("UNAVAILABLE", "SMS not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), REQUEST_SMS_PERMISSION)
    }

    private fun getSms(): String? {
        val uri = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = contentResolver.query(uri, arrayOf("address", "body"), null, null, null)
        val smsArray = JSONArray()

        cursor?.use {
            val addressIndex = it.getColumnIndexOrThrow("address")
            val bodyIndex = it.getColumnIndexOrThrow("body")

            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val smsObject = JSONObject()
                smsObject.put("address", address)
                smsObject.put("body", body)
                smsArray.put(smsObject)
            }
        }

        return if (smsArray.length() > 0) smsArray.toString() else null
    }
}
