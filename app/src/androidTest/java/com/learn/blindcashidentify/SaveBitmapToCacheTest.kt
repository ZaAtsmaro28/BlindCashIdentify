package com.learn.blindcashidentify

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveBitmapToCacheTest {

    @Test
    fun test_saveBitmapToCache_returnsFile() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cacheDir = context.cacheDir

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val file = saveBitmapToCache(cacheDir, bitmap)

        assertNotNull(file)
        assertTrue(file!!.exists())
        assertTrue(file.length() > 0)

        file.delete()
    }
}