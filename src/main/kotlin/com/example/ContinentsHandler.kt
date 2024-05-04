package com.example

import arrow.core.Either
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime

@Controller("api/v1/continents")
class ContinentsHandler {
    @Inject
    lateinit var list: ListContinentPort

    @Get("{?query*}")
    suspend fun list(query: ContinentQuery.All): List<ContinentResponse> = list.execute(query).fold(
        ifLeft = { throw it },
        ifRight = {
            it.map {
                it.toResponse()
            }.toList()
        },
    )
}

@Serdeable
@Serializable
data class ContinentDto(
    val id: Int,
    val name: String,
    var updatedAt: LocalDateTime = nowLocal(),
) {
    var countries: MutableList<CountryDto> = mutableListOf()
    suspend fun toResponse(): ContinentResponse = ContinentResponse(
        id = id,
        name = name,
        countries = countries.map { it.toResponse() },
    )
}


@Serdeable
@Serializable
data class CountryDto(
    val id: Int,
    val continentId: Int?,
    val name: String,
    var updatedAt: LocalDateTime = nowLocal(),
    val imagePath: String? = null,
) {
    var continent: ContinentDto? = null
    suspend fun toResponse(): CountryResponse = CountryResponse(
        id = id,
        name = name,
        imagePath = imagePath ?: ""
    )
}

@Serdeable
sealed interface ContinentQuery : Query {
    @Introspected
    @Serdeable
    class All : ContinentQuery, Query.All<List<ContinentDto>> {
        override var page: Int = 1
        override var size: Int = 100
        override var tz: String = "+00:00"
    }
}

@Serdeable
data class ContinentResponse(
    val id: Int,
    val name: String,
    val countries: List<CountryResponse>,
)

@Serdeable
data class CountryResponse(
    val id: Int,
    val name: String,
    val imagePath: String,
)

fun nowLocal(): LocalDateTime = now().toLocalDateTime(TimeZone.UTC)
interface ListContinentPort : ReadPort<ContinentQuery.All, List<ContinentDto>>
interface ReadPort<Q : Query, R> {
    suspend fun execute(query: Q, ): List<ContinentDto>
}
interface Query {
    interface All<T> : Query {
        val page: Int
        val size: Int
        var tz: String
    }

    interface ById<T> : Query {
        val id: String
        var tz: String
    }
}
fun LocalDateTime.toZonedDateTime(of: ZoneId = ZoneId.of("UTC")): ZonedDateTime {
    return toInstant(TimeZone.UTC).toJavaInstant().atZone(of)
}
