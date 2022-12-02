package com.roadcastassignment.ui.entries


import com.roadcastassignment.model.EntriesResponse
import com.roadcastassignment.model.Entry
import com.roadcastassignment.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import javax.inject.Inject


class EntryRepository
@Inject
constructor(val apiService: ApiService) {
    fun getPost(): Flow<EntriesResponse> = flow {
        emit(apiService.getPost())
    }.flowOn(Dispatchers.IO)
}
