package com.composemovie2.findmymovie.domain.mapper

interface Mapper<FROM, TO> {

    fun map(from: FROM):TO
}