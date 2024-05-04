package com.example

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import kotlinx.datetime.toKotlinLocalDateTime
import org.hibernate.proxy.HibernateProxy
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now

@Entity(name = "countries")
data class Country(
    @Id
    val id: Int,
    val name: String,
    @Column(name = "updated_at")
    val updatedAt: ZonedDateTime = now(),
    @Column(name = "image_path")
    val imagePath: String? = null,
) {
    @ManyToOne(optional = true)
    var continent: Continent? = null
    constructor(dto: CountryDto) : this(
        dto.id,
        dto.name,
        dto.updatedAt.toZonedDateTime(),
        dto.imagePath,
    )

    fun toDto() = CountryDto(
        id = id,
        continentId = continent?.id,
        name = name,
        updatedAt = updatedAt.toLocalDateTime().toKotlinLocalDateTime(),
        imagePath = imagePath,
    )

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Country

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id )"
    }

}
