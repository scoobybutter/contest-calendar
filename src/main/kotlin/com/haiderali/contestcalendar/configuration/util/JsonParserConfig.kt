package com.haiderali.contestcalendar.configuration.util

import com.beust.klaxon.FieldRenamer
import com.beust.klaxon.Klaxon
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonParserConfig {

    @Bean
    fun klaxon(): Klaxon {
        val renamer = object : FieldRenamer {
            override fun toJson(fieldName: String) = FieldRenamer.camelToUnderscores(fieldName)

            override fun fromJson(fieldName: String) = FieldRenamer.underscoreToCamel(fieldName)
        }

        return Klaxon().fieldRenamer(renamer)
    }
}
