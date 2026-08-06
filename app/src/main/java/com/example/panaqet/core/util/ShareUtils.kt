package com.example.panaqet.core.util

import android.content.Context
import android.content.Intent

fun Context.shareText(text: String, title: String = "Share via") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, title))
}
