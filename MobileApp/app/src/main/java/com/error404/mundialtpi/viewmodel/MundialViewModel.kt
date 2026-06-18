package com.error404.mundialtpi.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.error404.mundialtpi.models.DTOPartidosDetalle
import com.error404.mundialtpi.models.DTOPartidosLista
import com.error404.mundialtpi.repository.MundialRepository
import kotlinx.coroutines.launch

class MundialViewModel(private val repository: MundialRepository) : ViewModel() {
    var partidosLista by mutableStateOf<List<DTOPartidosLista>>(emptyList())
        private set

    //Agregamos un estado para el partido seleccionado
    var partidoSeleccionado by mutableStateOf<DTOPartidosDetalle?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isDarkMode by mutableStateOf(false)

    fun toggleTheme() {
        isDarkMode = !isDarkMode
    }

    fun cargarPartidos() {
        viewModelScope.launch {
            isLoading = true
            try {
                partidosLista = repository.fetchPartidosLista()
            } catch (e: Exception) { /* error */}
            isLoading = false
        }
    }

    //Agregamos la función para cargar un partido por su ID
    fun cargarPartidoDetalle(id: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                partidoSeleccionado = repository.fetchPartidosDetalle(id)
            } catch (e: Exception) { /* error */}
            isLoading = false
        }
    }

    //Limpiar el estado al salir de la pantalla
    fun limpiarDetalle() {
        partidoSeleccionado = null
    }
}