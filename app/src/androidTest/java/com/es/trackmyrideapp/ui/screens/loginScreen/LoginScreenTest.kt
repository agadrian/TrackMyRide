package com.es.trackmyrideapp.ui.screens.loginScreen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test


class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_loginSuccessAndFailure_behavesCorrectly() {
        val fakeViewModel = FakeLoginViewModel()
        val snackbarHostState = SnackbarHostState()
        val fakeSessionViewModel = FakeSessionViewModel()

        var navigatedToHome = false
        var navigatedToAdmin = false

        // Set composable under test
        composeTestRule.setContent {
            LoginScreen(
                navigateToRegister = {},
                navigateToHome = { navigatedToHome = true },
                navigateToAdminScreen = { navigatedToAdmin = true },
                navigateToForgotPassword = {},
                snackbarHostState = snackbarHostState,
                loginViewModel = fakeViewModel,
                sessionViewModel = fakeSessionViewModel
            )
        }

        // Verificar que el campo email está vacío inicialmente
        composeTestRule.onNodeWithTag("email_input").assertTextContains("", substring = false)

        // Simular login con usuario normal
        composeTestRule.onNodeWithTag("email_input").performTextInput("user@example.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("123456")

        // Verificar que se actualizaron los valores en el ViewModel fake
        assert(fakeViewModel.email == "user@example.com")
        assert(fakeViewModel.password == "123456")

        // Simular login exitoso para usuario normal
        composeTestRule.runOnUiThread {
            fakeViewModel.signIn()
        }
        composeTestRule.waitForIdle()

        // Validar navegación a Home y no a Admin
        assert(navigatedToHome)
        assert(!navigatedToAdmin)

        // Reset flags para siguiente prueba
        navigatedToHome = false
        navigatedToAdmin = false

        // Simular login con usuario admin
        composeTestRule.runOnUiThread {
            fakeViewModel.updateEmail("admin@admin.com")
            fakeViewModel.updatePassword("adminpass")
            fakeViewModel.signIn()
        }
        composeTestRule.waitForIdle()

        // Validar navegación a Admin y no a Home
        assert(navigatedToAdmin)
        assert(!navigatedToHome)

        // Simular login fallido (campos vacíos)
        composeTestRule.runOnUiThread {
            fakeViewModel.updateEmail("")
            fakeViewModel.updatePassword("")
            fakeViewModel.signIn()
        }
        composeTestRule.waitForIdle()

        // El snackbar debe mostrar mensaje de error
        runBlocking {
            val snackbarText = snackbarHostState.currentSnackbarData?.visuals?.message
            assert(snackbarText == "Login failed")
        }
    }

    @Test
    fun loginScreen_noSnackbarShown_onLoginSuccess() {
        val fakeViewModel = FakeLoginViewModel()
        val snackbarHostState = SnackbarHostState()
        val fakeSessionViewModel = FakeSessionViewModel()

        composeTestRule.setContent {
            LoginScreen(
                navigateToRegister = {},
                navigateToHome = {},
                navigateToAdminScreen = {},
                navigateToForgotPassword = {},
                snackbarHostState = snackbarHostState,
                loginViewModel = fakeViewModel,
                sessionViewModel = fakeSessionViewModel
            )
        }

        // Simular login exitoso
        composeTestRule.runOnUiThread {
            fakeViewModel.simulateLoginSuccess("USER")
        }
        composeTestRule.waitForIdle()

        // Verificar que no hay mensaje en el snackbar
        runBlocking {
            val snackbarText = snackbarHostState.currentSnackbarData?.visuals?.message
            assert(snackbarText == null)
        }
    }

    @Test
    fun loginScreen_showsSnackbar_onLoginError() {
        val fakeViewModel = FakeLoginViewModel()
        val snackbarHostState = SnackbarHostState()
        val fakeSessionViewModel = FakeSessionViewModel()

        composeTestRule.setContent {
            LoginScreen(
                navigateToRegister = {},
                navigateToHome = {},
                navigateToAdminScreen = {},
                navigateToForgotPassword = {},
                snackbarHostState = snackbarHostState,
                loginViewModel = fakeViewModel,
                sessionViewModel = fakeSessionViewModel
            )
        }

        // Simular error de login
        composeTestRule.runOnUiThread {
            fakeViewModel.simulateLoginError("Login failed")
        }
        composeTestRule.waitForIdle()

        // Verificar que el snackbar muestra el mensaje de error
        runBlocking {
            val snackbarText = snackbarHostState.currentSnackbarData?.visuals?.message
            assert(snackbarText == "Login failed")
        }
    }
}