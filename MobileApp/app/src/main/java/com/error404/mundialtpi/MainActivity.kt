package com.error404.mundialtpi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.error404.mundialtpi.network.RetrofitClient
import com.error404.mundialtpi.repository.AuthRepository
import com.error404.mundialtpi.repository.MundialRepository
import com.error404.mundialtpi.repository.TicketRepository
import com.error404.mundialtpi.ui.navigation.AppNavigation
import com.error404.mundialtpi.ui.theme.MundialTPITheme
import com.error404.mundialtpi.utils.TokenManager
import com.error404.mundialtpi.viewmodel.AuthViewModel
import com.error404.mundialtpi.viewmodel.MundialViewModel
import com.error404.mundialtpi.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //API de Retrofit
        val apiService = RetrofitClient.api
        val tokenManager = TokenManager(applicationContext)

        //Repositorios
        val repository = MundialRepository(apiService)
        val authRepository = AuthRepository(apiService, tokenManager)
        val ticketRepository = TicketRepository(apiService, tokenManager)

        //ViewModels
        val viewModel = MundialViewModel(repository)
        val authViewModel = AuthViewModel(authRepository)
        val ticketViewModel = TicketViewModel(ticketRepository)

        setContent {
            MundialTPITheme(darkTheme = viewModel.isDarkMode) {
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