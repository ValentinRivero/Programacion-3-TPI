package com.error404.mundialtpi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.error404.mundialtpi.network.RetrofitClient
import com.error404.mundialtpi.repository.AuthRepository
import com.error404.mundialtpi.repository.MundialRepository
import com.error404.mundialtpi.repository.TicketRepository
import com.error404.mundialtpi.ui.navigation.AppNavigation
import com.error404.mundialtpi.ui.theme.MundialTPITheme
import com.error404.mundialtpi.utils.ThemePreferences
import com.error404.mundialtpi.utils.TokenManager
import com.error404.mundialtpi.viewmodel.AuthViewModel
import com.error404.mundialtpi.viewmodel.MundialViewModel
import com.error404.mundialtpi.viewmodel.TicketViewModel
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // API de Retrofit y Utils
        val apiService = RetrofitClient.api
        val tokenManager = TokenManager(applicationContext)
        val themePreferences = ThemePreferences(applicationContext)

        // Repositorios
        val repository = MundialRepository(apiService, tokenManager)
        val authRepository = AuthRepository(apiService, tokenManager)
        val ticketRepository = TicketRepository(apiService, tokenManager)

        // ViewModels
        val viewModel = MundialViewModel(repository, themePreferences)
        val authViewModel = AuthViewModel(authRepository)
        val ticketViewModel = TicketViewModel(ticketRepository)

        setContent {
            val esTemaOscuro by themePreferences.temaOscuroFlow.collectAsState(initial = false)
            MundialTPITheme(darkTheme = esTemaOscuro) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        ticketViewModel = ticketViewModel
                    )
                }
            }
        }
    }
}