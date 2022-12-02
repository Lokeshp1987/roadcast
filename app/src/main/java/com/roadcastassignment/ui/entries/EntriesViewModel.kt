package com.roadcastassignment.ui.entries

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadcastassignment.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel
@Inject
constructor(val entryRepository: EntryRepository): ViewModel()
{
    val response : MutableState<ApiState> = mutableStateOf(ApiState.Empty)
    init {
        getPost()
    }
    fun getPost () = viewModelScope.launch {
        entryRepository.getPost().onStart {
            response.value = ApiState.Loading
        }.catch {
            response.value = ApiState.Failure(it)

        }.collect{
            response.value = ApiState.Success(it)
        }
    }
}