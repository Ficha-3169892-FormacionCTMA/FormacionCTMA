package com.miguelloaiza.miformacionctma.rules.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelloaiza.miformacionctma.data.AuthRepository
import com.miguelloaiza.miformacionctma.ui.estado.OperacionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val loginState: StateFlow<OperacionUiState> = _loginState.asStateFlow()

    fun login(email: String, clave: String) {
        viewModelScope.launch {
            _loginState.value = OperacionUiState.EnCurso
            authRepository.login(email, clave)
                .onSuccess { _loginState.value = OperacionUiState.Exitosa }
                .onFailure { _loginState.value = OperacionUiState.Fallida(it.message ?: "Error desconocido") }
        }
    }

    fun reiniciarEstado() {
        _loginState.value = OperacionUiState.Inactiva
    }
}
