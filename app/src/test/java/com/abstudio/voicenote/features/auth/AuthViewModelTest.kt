package com.abstudio.voicenote.features.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abstudio.voicenote.data.api.AuthApi
import com.abstudio.voicenote.data.local.TokenManager
import com.abstudio.voicenote.data.models.UserInfo
import com.abstudio.voicenote.data.models.UserSyncRequest
import com.abstudio.voicenote.data.models.UserSyncResponse
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private val authApi: AuthApi = mockk()
    private val tokenManager: TokenManager = mockk(relaxed = true) // Relaxed for simpler setup
    private val googleAuthHelper: GoogleAuthHelper = mockk() // Add mock for GoogleAuthHelper

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authApi, tokenManager, googleAuthHelper)
        
        // Mock deviceModelProvider to return a predictable value
        viewModel.deviceModelProvider = { "TestDevice" }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `authenticate success updates state to Success`() = runTest {
        // Given
        val mockUser = UserInfo("123", "test@example.com", "Test User", "USER")
        val mockResponse = UserSyncResponse("accessToken", "refreshToken", "bearer", mockUser, false)
        val response = Response.success(mockResponse)
        
        // Login succeeds
        coEvery { authApi.login(any()) } returns response

        // When
        viewModel.authenticate("test@example.com", "password")

        // Then
        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            assertEquals(AuthState.Loading, awaitItem())
            val successState = awaitItem() as AuthState.Success
            assertEquals(mockResponse, successState.response)
            
            coVerify { tokenManager.saveToken("accessToken") }
        }
    }

    @Test
    fun `authenticate login fail 401 then register success updates state to Success`() = runTest {
        // Given
        val loginErrorResponse = Response.error<UserSyncResponse>(401, "Unauthorized".toResponseBody())
        coEvery { authApi.login(any()) } returns loginErrorResponse

        val mockUser = UserInfo("123", "new@example.com", "New User", "USER")
        val mockRegResponse = UserSyncResponse("regToken", "refreshToken", "bearer", mockUser, true)
        val regResponse = Response.success(mockRegResponse)
        coEvery { authApi.register(any()) } returns regResponse

        // When
        viewModel.authenticate("new@example.com", "password")

        // Then
        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            assertEquals(AuthState.Loading, awaitItem())
            val successState = awaitItem() as AuthState.Success
            assertEquals(mockRegResponse, successState.response)

            coVerify { tokenManager.saveToken("regToken") }
        }
    }

    @Test
    fun `authenticate failure updates state to Error`() = runTest {
        // Given
        val errorResponse = Response.error<UserSyncResponse>(500, "Server Error".toResponseBody())
        coEvery { authApi.login(any()) } returns errorResponse

        // When
        viewModel.authenticate("test@example.com", "password")

        // Then
        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            assertEquals(AuthState.Loading, awaitItem())
            val errorState = awaitItem() as AuthState.Error
            assertTrue(errorState.message.contains("Login failed"))
        }
    }
    @Test
    fun `signInWithGoogle success updates state to Success`() = runTest {
        val context = mockk<android.content.Context>()
        coEvery { googleAuthHelper.signIn(any()) } returns "valid_id_token"
        
        // No API call expectation as per updated ViewModel logic for MVP

        viewModel.signInWithGoogle(context)
        
        // Wait for coroutine
        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            assertEquals(AuthState.Loading, awaitItem())
            val successState = awaitItem() as AuthState.Success
            assertEquals("google@user.com", successState.response.user.email)
            assertEquals("dummy_google_token", successState.response.accessToken)
        }
    }

    @Test
    fun `signInWithGoogle failure updates error state`() = runTest {
        val context = mockk<android.content.Context>()
        coEvery { googleAuthHelper.signIn(any()) } returns null

        viewModel.signInWithGoogle(context)

        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            assertEquals(AuthState.Loading, awaitItem())
            val errorState = awaitItem() as AuthState.Error
            assertTrue(errorState.message.contains("Google Sign-In failed"))
        }
    }
}
