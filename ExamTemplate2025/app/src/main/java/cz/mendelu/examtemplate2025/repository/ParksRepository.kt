package cz.mendelu.examtemplate2025.repository

import cz.mendelu.examtemplate2025.models.Park
import kotlinx.coroutines.flow.Flow

interface ParksRepository {
    fun getParks(): Flow<ApiResult<List<Park>>>
    //fun getTips(): Flow<ApiResult<List<Tip>>>
}