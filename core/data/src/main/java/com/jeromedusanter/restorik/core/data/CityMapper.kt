package com.jeromedusanter.restorik.core.data

import com.jeromedusanter.restorik.core.database.model.CityEntity
import com.jeromedusanter.restorik.core.model.City
import javax.inject.Inject

internal class CityMapper @Inject constructor() {

    fun mapEntityToDomain(cityEntity: CityEntity): City {
        return City(
            id = cityEntity.id,
            name = cityEntity.name
        )
    }
}
