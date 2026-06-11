package com.commityarezar.mundialtpi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.commityarezar.mundialtpi.network.RetrofitClient
import com.commityarezar.mundialtpi.repository.MundialRepository
import com.commityarezar.mundialtpi.ui.navigation.AppNavigation
import com.commityarezar.mundialtpi.ui.theme.MundialTPITheme // Fijate que coincida con tu carpeta theme
import com.commityarezar.mundialtpi.viewmodel.MundialViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //API de Retrofit
        val apiService = RetrofitClient.api

        //Le pasamos la API al Repositorio
        val repository = MundialRepository(apiService)

        //Le pasamos el Repositorio al ViewModel
        val viewModel = MundialViewModel(repository)

        setContent {
            MundialTPITheme(darkTheme = viewModel.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}