package com.example.panaqet.core.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun Uri.toFile(context: Context): File {
    val inputStream = context.contentResolver.openInputStream(this)
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return file
}
