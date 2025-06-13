package com.learn.blindcashidentify

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessibilityUtilsTest {

    @Test
    fun test_announceForAccessibility_doesNotCrash() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        try {
            announceForAccessibility(context, "Halo, ini pengumuman.")
        } catch (e: Exception) {
            fail("Fungsi melempar exception: ${e.message}")
        }
    }
}

