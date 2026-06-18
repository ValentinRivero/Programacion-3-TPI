package com.error404.mundialtpi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun comprarTicket(partidoId: Int, tipo: String, cantidad: Int) {
        viewModelScope.launch {
            _ticketState.value = TicketState.Loading
            val result = repository.comprarTicket(partidoId, tipo, cantidad)
            if (result.isSuccess) {
                _ticketState.value = TicketState.Success(result.getOrThrow())
            } else {
                _ticketState.value = TicketState.Error(result.exceptionOrNull()?.message ?: "Error al comprar entrada")
            }
        }
    }

    fun resetState() {
        _ticketState.value = TicketState.Idle
    }
}
