package com.andika.newsfeedsimulator

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    @Test
    fun testSetCategoryFilter() = runTest {
        val testDispatcher = StandardTestDispatcher()
        val testScope = TestScope(testDispatcher)
        val viewModel = NewsViewModel(testScope)

        viewModel.setCategoryFilter("Teknologi")

        val filter = viewModel.categoryFilter.first()
        assertEquals("Teknologi", filter)
    }

    @Test
    fun testMarkAsRead() = runTest {
        val testDispatcher = StandardTestDispatcher()
        val testScope = TestScope(testDispatcher)
        val viewModel = NewsViewModel(testScope)

        viewModel.markAsRead(1)
        viewModel.markAsRead(2)

        val count = viewModel.readArticlesCount.first()
        assertEquals(2, count)
    }

    @Test
    fun testMarkAsReadDuplicate() = runTest {
        val testDispatcher = StandardTestDispatcher()
        val testScope = TestScope(testDispatcher)
        val viewModel = NewsViewModel(testScope)

        viewModel.markAsRead(1)
        viewModel.markAsRead(1)
        viewModel.markAsRead(1)

        val count = viewModel.readArticlesCount.first()
        assertEquals(1, count)
    }

    @Test
    fun testFetchArticleDetail() = runTest {
        val testDispatcher = StandardTestDispatcher()
        val testScope = TestScope(testDispatcher)
        val viewModel = NewsViewModel(testScope)

        val result = viewModel.fetchArticleDetail(999)

        assertEquals(null, result)
    }
}