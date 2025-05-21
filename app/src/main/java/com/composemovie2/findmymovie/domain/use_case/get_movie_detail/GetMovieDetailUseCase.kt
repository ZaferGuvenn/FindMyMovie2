package com.composemovie2.findmymovie.domain.use_case.get_movie_detail

import com.composemovie2.findmymovie.domain.mapper.MovieDetailDTOMapper
import com.composemovie2.findmymovie.domain.model.MovieDetail
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val movieDetailDTOMapper: MovieDetailDTOMapper
) {

    fun executeMovieDetail(imdbId:String):Flow<NetworkResult<MovieDetail>>{

        return flow {

            try {

                emit(NetworkResult.Loading())
                val movieDetailDTO = movieRepository.getMovieDetailDto(imdbId)
                emit(NetworkResult.Success(movieDetailDTOMapper.map(movieDetailDTO)))

            }catch (e: Exception){
                emit(NetworkResult.Error( message = e.localizedMessage ?: "Unknown error!"))
            }

        }

    }

}