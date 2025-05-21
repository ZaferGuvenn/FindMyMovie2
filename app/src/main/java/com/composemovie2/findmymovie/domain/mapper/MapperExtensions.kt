package com.composemovie2.findmymovie.domain.mapper

fun <F,T> Mapper<F,T>.mapList(fromList: List<F>): List<T> = fromList.map{ map(it)}