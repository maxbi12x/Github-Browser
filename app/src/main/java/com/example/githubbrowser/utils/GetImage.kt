package com.example.githubbrowser.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import java.net.URL

object GetImage {
    fun getImage(url: String): Bitmap {
        var bitmap: Bitmap? = null
        var inputStream: InputStream? = null
        try {
            inputStream = URL(url).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap!!
    }
}