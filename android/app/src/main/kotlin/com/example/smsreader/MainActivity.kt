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
        val cursor: Cursor? = contentResolver.query(uri, arrayOf("address", "body", "date"), null, null, null)
        val smsArray = JSONArray()

        cursor?.use {
            val addressIndex = it.getColumnIndexOrThrow("address")
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val dateIndex = it.getColumnIndexOrThrow("date")

            /*val idIndex = it.getColumnIndexOrThrow("_id")
            val threadIdIndex = it.getColumnIndexOrThrow("thread_id")
            val addressIndex = it.getColumnIndexOrThrow("address")
            val personIndex = it.getColumnIndexOrThrow("person")
            val dateIndex = it.getColumnIndexOrThrow("date")
            val dateSentIndex = it.getColumnIndexOrThrow("date_sent")
            val protocolIndex = it.getColumnIndexOrThrow("protocol")
            val readIndex = it.getColumnIndexOrThrow("read")
            val statusIndex = it.getColumnIndexOrThrow("status")
            val typeIndex = it.getColumnIndexOrThrow("type")
            val replyPathPresentIndex = it.getColumnIndexOrThrow("reply_path_present")
            val subjectIndex = it.getColumnIndexOrThrow("subject")
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val serviceCenterIndex = it.getColumnIndexOrThrow("service_center")
            val lockedIndex = it.getColumnIndexOrThrow("locked")
            val errorCodeIndex = it.getColumnIndexOrThrow("error_code")
            val seenIndex = it.getColumnIndexOrThrow("seen")*/

            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)
                val smsObject = JSONObject()
                smsObject.put("address", address)
                smsObject.put("body", body)
                smsObject.put("date", date)
                smsArray.put(smsObject)
            }
        }

        return if (smsArray.length() > 0) smsArray.toString() else null
    }
}
