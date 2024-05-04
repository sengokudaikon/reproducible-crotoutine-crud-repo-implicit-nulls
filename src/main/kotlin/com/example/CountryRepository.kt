package com.example

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository

@Repository
interface CountryRepository : CoroutineCrudRepository<Country, Int>
