package com.evosouza.myapplication.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(byteArray, 0,byteArray.size)
    }

    @TypeConverter
    fun fromBitmap(bpm: Bitmap): ByteArray{
        val outputSream = ByteArrayOutputStream()
        bpm.compress(Bitmap.CompressFormat.PNG, 100, outputSream)
        return outputSream.toByteArray()
    }
}