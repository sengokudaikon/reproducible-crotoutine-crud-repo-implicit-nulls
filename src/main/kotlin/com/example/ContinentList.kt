package com.example

import arrow.core.Either
import arrow.fx.coroutines.parMap
import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.toList

@Singleton
@CacheConfig("default")
class ContinentList(
    val repository: ContinentRepository,
) : ListContinentPort {
    @Cacheable(value = ["continents"], parameters = ["#query"])
    override suspend fun execute(query: ContinentQuery.All): List<ContinentDto> {
        //if the database is empty, this can return null instead of an empty flow
        val flow =  repository.findAll()
        // this can also return null if the criteria isn't fulfilled
        val flow2 = repository.findAllByCountryCode("US")
        // trying to call .toList() results in NPE
        flow.toList()
        flow2.toList()
        // null-safe call works, but IDE doesn't know that return value is nullable
        val result= flow?.toList() ?: emptyList()
        val result2= flow2?.toList() ?: emptyList()
        return (result + result2).parMap { it.toDto() }
    }
}

