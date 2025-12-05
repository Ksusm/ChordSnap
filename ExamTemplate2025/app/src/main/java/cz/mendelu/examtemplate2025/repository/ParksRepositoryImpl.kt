package cz.mendelu.examtemplate2025.repository

import cz.mendelu.examtemplate2025.api.ParksApi
import cz.mendelu.examtemplate2025.models.Park
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ParksRepositoryImpl @Inject constructor(
    private val api: ParksApi
) : ParksRepository {

    override fun getParks(): Flow<ApiResult<List<Park>>> = flow {
        emit(ApiResult.Loading)
        try {
            val response = api.getParks()
            if (response.isSuccessful && response.body() != null) {
                val parks = response.body()!!.parks
                emit(ApiResult.Success(parks))
            } else {
                emit(ApiResult.Error("Chyba při načítání dat: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Neznámá chyba"))
        }
    }.flowOn(Dispatchers.IO)


}