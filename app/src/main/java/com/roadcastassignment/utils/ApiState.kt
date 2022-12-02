package com.roadcastassignment.utils

import com.roadcastassignment.model.EntriesResponse
import com.roadcastassignment.model.Entry

sealed class ApiState
{
    class Success(val data : EntriesResponse): ApiState()
    class Failure(val message : Throwable) : ApiState()
    object Loading : ApiState()
    object Empty : ApiState()
}
