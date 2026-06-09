package com.commityarezar.mundialtpi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commityarezar.mundialtpi.models.DTOPartidosLista
import com.commityarezar.mundialtpi.repository.MundialRepository
import kotlinx.coroutines.launch

class MundialViewModel(private val repository: MundialRepository) : ViewModel() {
    var partidosLista by mutableStateOf<List<DTOPartidosLista>>(emptyList())

    var isLoading by mutableStateOf(false)
        private set

    fun cargarPartidos() {
        viewModelScope.launch {
            isLoading = true

            try {
                partidosLista = repository.fetchPartidosLista()
            } catch (e: Exception) { /* error */}
            isLoading = false
        }
    }
}