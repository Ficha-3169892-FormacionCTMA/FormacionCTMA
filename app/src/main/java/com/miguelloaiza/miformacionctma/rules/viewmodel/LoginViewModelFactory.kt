package com.miguelloaiza.miformacionctma.rules.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.miguelloaiza.miformacionctma.data.AuthRepository

class LoginViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida")
    }
}
