package com.error404.mundialtpi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.error404.mundialtpi.models.DTOMisTickets
import com.error404.mundialtpi.models.TicketResponse
import com.error404.mundialtpi.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TicketState {
    object Idle : TicketState()
    object Loading : TicketState()
    data class Success(val response: TicketResponse) : TicketState()
    data class Error(val message: String) : TicketState()
}

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {
    private val _ticketState = MutableStateFlow<TicketState>(TicketState.Idle)
    val ticketState: StateFlow<TicketState> = _ticketState.asStateFlow()

    var misTicketsLista by mutableStateOf<List<DTOMisTickets>>(emptyList())
        private set
    var isLoadingTickets by mutableStateOf(false)
        private set

    var errorTickets by mutableStateOf<String?>(null)
        private set

    fun comprarTicket(partidoId: Int, categoriaId: Int, cantidad: Int) {
        viewModelScope.launch {
            _ticketState.value = TicketState.Loading
            val result = repository.comprarTicket(partidoId, categoriaId, cantidad)
            if (result.isSuccess) {
                _ticketState.value = TicketState.Success(result.getOrThrow())
            } else {
                _ticketState.value = TicketState.Error(result.exceptionOrNull()?.message ?: "Error al comprar entrada")
            }
        }
    }

    fun cargarMisTickets() {
        viewModelScope.launch {
            isLoadingTickets = true
            errorTickets = null
            val result = repository.getMisTickets()
            if (result.isSuccess) {
                misTicketsLista = result.getOrDefault(emptyList())
            } else {
                misTicketsLista = emptyList()
                errorTickets = result.exceptionOrNull()?.message ?: "Error de conexión con el servidor"
            }
            isLoadingTickets = false
        }
    }

    fun resetState() {
        _ticketState.value = TicketState.Idle
    }
}