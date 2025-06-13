package com.learn.blindcashidentify

import com.learn.blindcashidentify.dto.PredictResult
import com.learn.blindcashidentify.dto.PredictValue
import com.learn.blindcashidentify.repository.PredictRepository
import com.learn.blindcashidentify.viewmodel.PredictViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class PredictViewModelTest {
    private val dummyPrediction = listOf(
        PredictValue(
            `class` = "50000",
            class_id = 1,
            confidence = 0.95f
        )
    )

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() 
    }

    @Test
    fun test_predict_setsResultCorrectly() = runTest {
        val dummyResult = PredictResult("50.000", 0.9f, dummyPrediction)

        val dummyRepository = object : PredictRepository() {
            override suspend fun detect(imageFile: File): PredictResult? {
                return dummyResult
            }
        }

        val viewModel = PredictViewModel(dummyRepository)
        viewModel.predict(File("dummy_path"))

        advanceUntilIdle()

        assertEquals(dummyResult, viewModel.result)
        assertFalse(viewModel.isLoading)
    }
}

