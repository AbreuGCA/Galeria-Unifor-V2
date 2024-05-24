package com.example.galeriauniforv2

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.util.Base64

fun saveImageToFile(context: Context, imageBase64: String): String? {
    val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
    val fileName = "image_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, fileName)

    return try {
        FileOutputStream(file).use { fos ->
            fos.write(decodedBytes)
            fos.flush()
        }
        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
