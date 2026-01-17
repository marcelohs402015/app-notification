package com.appnotification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appnotification.domain.model.Event
import com.appnotification.domain.model.Resource
import com.appnotification.domain.usecase.ExtractEventsFromEmailUseCase
import com.appnotification.domain.usecase.GetEventsUseCase
import com.appnotification.domain.usecase.ScheduleEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val extractEventsFromEmailUseCase: ExtractEventsFromEmailUseCase,
    private val scheduleEventUseCase: ScheduleEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.Loading)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private val _extractionState = MutableStateFlow<Resource<List<Event>>>(Resource.Loading)
    val extractionState: StateFlow<Resource<List<Event>>> = _extractionState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            getEventsUseCase().collect { events ->
                _uiState.value = EventUiState.Success(events)
            }
        }
    }

    fun extractEventsFromEmail(emailId: String, emailContent: String) {
        viewModelScope.launch {
            extractEventsFromEmailUseCase(emailId, emailContent).collect { resource ->
                _extractionState.value = resource
            }
        }
    }

    fun scheduleEvent(event: Event) {
        viewModelScope.launch {
            scheduleEventUseCase(event).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadEvents()
                    }
                    is Resource.Error -> {
                        _uiState.value = EventUiState.Error(resource.message)
                    }
                    else -> {}
                }
            }
        }
    }
}

sealed class EventUiState {
    data object Loading : EventUiState()
    data class Success(val events: List<Event>) : EventUiState()
    data class Error(val message: String) : EventUiState()
}
