package com.example.voicenote.features.auth

import com.example.voicenote.data.api.AuthApi
import com.example.voicenote.data.models.UserSyncRequest
import com.example.voicenote.data.models.UserSyncResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class AuthViewModelTest {

    private val authApi = mockk<AuthApi>()
    
    @Test
    fun `syncDevice success updates state`() = runTest {
        // TDD: we want to ensure syncDevice updates our viewstate correctly
        // This is a placeholder test that we will expand as we build the ViewModel
        val mockResponse = mockk<UserSyncResponse>()
        coEvery { authApi.syncDevice(any()) } returns Response.success(mockResponse)
        
        // viewModel.sync(...) 
        // assert(viewModel.state.value is Success)
    }
}
