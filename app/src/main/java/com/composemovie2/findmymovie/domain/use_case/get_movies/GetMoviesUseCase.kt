package com.composemovie2.findmymovie.domain.use_case.get_movies

import com.composemovie2.findmymovie.domain.mapper.MoviesDTOMapper
import com.composemovie2.findmymovie.domain.model.Movie
import com.composemovie2.findmymovie.domain.repository.MovieRepository
import com.composemovie2.findmymovie.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val moviesDtoMapper : MoviesDTOMapper
) {

    fun executeGetMovies(searchString: String):Flow<NetworkResult<List<Movie>>>{
        return flow {
            try {

                emit(NetworkResult.Loading())

                val moviesDto = movieRepository.getMoviesDto(searchString)

                println(moviesDto)

                if (moviesDto.response == "True"){
                    if (moviesDto.totalResults=="0"){
                        emit(NetworkResult.Error(message = "No movies found!"))
                    }else{
                        emit(NetworkResult.Success(data = moviesDtoMapper.map(moviesDto)))
                    }
                }else{
                    emit(NetworkResult.Error(message = "Response error!"))
                }
            }catch (e: IOException){
                emit(NetworkResult.Error(message = "Check your internet connection!"))
            }catch (e: HttpException){
                emit(NetworkResult.Error(message = "An error occurred while connecting to the server!"))
            }catch (e: Exception){
                emit(NetworkResult.Error(message = "Unknown error occurred! \n Details: "+ e.localizedMessage))
            }
        }
    }

}