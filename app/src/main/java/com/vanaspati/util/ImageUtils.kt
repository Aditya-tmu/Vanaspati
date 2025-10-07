package com.vanaspati.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {
    fun contentUriToBase64(contentResolver: ContentResolver, uri: Uri, maxSize: Int = 1024): String {
        val input: InputStream? = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input)
        input?.close()
        val scaled = scaleBitmap(bitmap, maxSize)
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 90, out)
        val bytes = out.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun scaleBitmap(src: Bitmap, max: Int): Bitmap {
        val w = src.width
        val h = src.height
        val scale = max.toFloat() / maxOf(w, h).toFloat()
        return if (scale >= 1f) src else Bitmap.createScaledBitmap(src, (w * scale).toInt(), (h * scale).toInt(), true)
    }
}
