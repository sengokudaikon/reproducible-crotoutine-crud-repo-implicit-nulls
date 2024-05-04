package com.example

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import kotlinx.datetime.toKotlinLocalDateTime
import org.hibernate.proxy.HibernateProxy
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now

@Entity(name = "continents")
data class Continent(
    @Id
    val id: Int,
    val name: String,
    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = now(),
) {
    @OneToMany(mappedBy = "continent", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val countries: MutableList<Country> = mutableListOf()

    fun toDto() = ContinentDto(
        id = id,
        name = name,
        updatedAt = updatedAt.toLocalDateTime().toKotlinLocalDateTime()
    ).apply {
        countries = this@Continent.countries.map { it.toDto() }.toMutableList()
    }

    constructor(dto: ContinentDto) : this(
        dto.id,
        dto.name,
        dto.updatedAt.toZonedDateTime(),
    )

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Continent

        return id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id )"
    }

}

