package com.appnotification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appnotification.domain.model.Email
import com.appnotification.domain.model.Resource
import com.appnotification.domain.usecase.FetchEmailsUseCase
import com.appnotification.domain.usecase.GetEmailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val getEmailsUseCase: GetEmailsUseCase,
    private val fetchEmailsUseCase: FetchEmailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmailUiState>(EmailUiState.Loading)
    val uiState: StateFlow<EmailUiState> = _uiState.asStateFlow()

    init {
        loadEmails()
    }

    fun loadEmails() {
        viewModelScope.launch {
            getEmailsUseCase().collect { emails ->
                _uiState.value = EmailUiState.Success(emails)
            }
        }
    }

    fun refreshEmails() {
        viewModelScope.launch {
            fetchEmailsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = EmailUiState.Loading
                    }
                    is Resource.Success -> {
                        _uiState.value = EmailUiState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = EmailUiState.Error(resource.message)
                    }
                }
            }
        }
    }
}

sealed class EmailUiState {
    data object Loading : EmailUiState()
    data class Success(val emails: List<Email>) : EmailUiState()
    data class Error(val message: String) : EmailUiState()
}
