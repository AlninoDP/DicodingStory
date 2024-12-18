package com.tms.dicodingstory.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.tms.dicodingstory.DataDummy
import com.tms.dicodingstory.MainDispatcherRule
import com.tms.dicodingstory.data.MainRepository
import com.tms.dicodingstory.data.PreferencesRepository
import com.tms.dicodingstory.data.local.entity.StoryEntity
import com.tms.dicodingstory.getOrAwaitValue
import com.tms.dicodingstory.ui.adapter.StoriesPagingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var mainRepository: MainRepository

    @Mock
    private lateinit var preferencesRepository: PreferencesRepository

    @Test
    fun `when Get Story Should Not Null And Return Data`() = runTest {
        val dummyStoryEntities = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStoryEntities)

        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        Mockito.`when`(mainRepository.getAllStories()).thenReturn(expectedStory)
        val homeViewModel = HomeViewModel(mainRepository, preferencesRepository)
        val actualStory: PagingData<StoryEntity> = homeViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot()) // Check if the data is not null
        Assert.assertEquals(dummyStoryEntities.size, differ.snapshot().size) // Check if the data is the same as expected
        Assert.assertEquals(dummyStoryEntities[0], differ.snapshot()[0]) // Check if the first item in the data is the same as expected
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        Mockito.`when`(mainRepository.getAllStories()).thenReturn(expectedStory)
        val homeViewModel = HomeViewModel(mainRepository, preferencesRepository)
        val actualStory: PagingData<StoryEntity> = homeViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        // Check if the data is empty
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {

    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int{
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}