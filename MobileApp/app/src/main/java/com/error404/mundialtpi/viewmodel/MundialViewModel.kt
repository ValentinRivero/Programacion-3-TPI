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
import android.util.Log
import com.error404.mundialtpi.utils.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MundialViewModel(private val repository: MundialRepository, private val themePreferences: ThemePreferences) : ViewModel() {
    var partidosLista by mutableStateOf<List<DTOPartidosLista>>(emptyList())
        private set

    var partidoSeleccionado by mutableStateOf<DTOPartidosDetalle?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    val isDarkMode = themePreferences.temaOscuroFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun toggleTheme() {
        viewModelScope.launch {
            themePreferences.guardarTema(!isDarkMode.value)
        }
    }

    fun cargarPartidos() {
        viewModelScope.launch {
            isLoading = true
            try {
                partidosLista = repository.fetchPartidosLista()
            } catch (e: Exception) {
                Log.e("ERROR_API", "Falló Retrofit: ${e.message}", e)
            }
            isLoading = false
        }
    }

    //Agregamos la función para cargar un partido por su ID
    fun cargarPartidoDetalle(id: Int) {
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