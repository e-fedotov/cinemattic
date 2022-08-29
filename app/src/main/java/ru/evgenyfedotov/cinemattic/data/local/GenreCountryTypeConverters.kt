package ru.evgenyfedotov.cinemattic.data.local

import androidx.room.TypeConverter
import ru.evgenyfedotov.cinemattic.model.MovieCountry
import ru.evgenyfedotov.cinemattic.model.MovieGenre


class GenreCountryTypeConverters {

    @TypeConverter
    fun genresToString(genreList: List<MovieGenre>): String {
        val genreString = mutableListOf<String>()
        genreList.forEach {
            genreString.add(it.genre)
        }

        return genreString.joinToString(",")
    }

    @TypeConverter
    fun countriesToString(countries: List<MovieCountry>): String {
        val countryString = mutableListOf<String>()
        countries.forEach {
            countryString.add(it.country)
        }

        return countryString.joinToString(",")
    }

    @TypeConverter
    fun genreToList(value: String): List<MovieGenre> {
        val movieGenre = mutableListOf<MovieGenre>()
        value.split(",").forEach {
            movieGenre.add(MovieGenre(it))
        }
        return movieGenre
    }

    @TypeConverter
    fun countryToList(value: String): List<MovieCountry> {
        val movieCountry = mutableListOf<MovieCountry>()
        value.split(",").forEach {
            movieCountry.add(MovieCountry(it))
        }
        return movieCountry
    }

}