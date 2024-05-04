package com.example

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import kotlinx.coroutines.flow.Flow

@Repository
interface ContinentRepository : CoroutineCrudRepository<Continent, Int> {
    @Query("select c from continents c join countries cr on cr.continent = c where cr.name = :countryCode")
    suspend fun findAllByCountryCode(countryCode: String): Flow<Continent>
}

