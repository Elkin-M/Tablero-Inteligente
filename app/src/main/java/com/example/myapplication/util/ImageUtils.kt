package com.example.myapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageUtils {
    /**
     * Comprime una imagen desde una URI a un ByteArray para subir a Firebase Storage.
     * Reduce dimensiones si exceden el maxDimension y aplica compresión JPEG.
     */
    fun compressImage(context: Context, uri: Uri, maxDimension: Int = 1024, quality: Int = 80): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            var scale = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                scale = Math.pow(2.0, Math.ceil(Math.log(maxDimension.toDouble() / Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)).toInt().toDouble()).toInt()
            }

            val finalOptions = BitmapFactory.Options().apply { inSampleSize = scale }
            val finalInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(finalInputStream, null, finalOptions)
            finalInputStream?.close()

            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
