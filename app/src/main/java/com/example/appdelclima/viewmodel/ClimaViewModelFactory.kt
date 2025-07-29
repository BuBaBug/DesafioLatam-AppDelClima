package com.example.appdelclima.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appdelclima.repository.ClimaRepository

class ClimaViewModelFactory(
    private val repository: ClimaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClimaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClimaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}