package com.learn.blindcashidentify

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatWithUnderscoreTest {

    @Test
    fun test_formatWithUnderscore_1234567() {
        val result = formatWithUnderscore(1234567)
        assertEquals("1_234_567", result)
    }

    @Test
    fun test_formatWithUnderscore_1000() {
        val result = formatWithUnderscore(1000)
        assertEquals("1_000", result)
    }

    @Test
    fun test_formatWithUnderscore_0() {
        val result = formatWithUnderscore(0)
        assertEquals("0", result)
    }
}