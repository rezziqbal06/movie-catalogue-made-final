package com.rezziqbal.favoritemovieapp.ui.movie

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rezziqbal.favoritemovieapp.db.DatabaseContract
import com.rezziqbal.favoritemovieapp.db.DatabaseContract.MovieColumns.Companion.CONTENT_URI_MOVIE
import com.rezziqbal.favoritemovieapp.utils.MappingHelper
import com.rezziqbal.favoritemovieapp.entity.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private var movieData = MutableLiveData<ArrayList<Movie>>()

    fun getDataFromDatabase(context: Context) {
        Log.d("action", "ambil data database")
        GlobalScope.launch(Dispatchers.Main) {
            val deferredMovie = async(Dispatchers.IO) {
                val cursor = context.contentResolver?.query(CONTENT_URI_MOVIE, null, null, null, null) as Cursor
                MappingHelper.mapMovieCursorToArray(cursor)
            }
            val movie = deferredMovie.await()
            if (movie.size > 0) {
                movieData.value = movie
            } else {
                movieData.value = null
            }
        }
    }

    fun getMovie(): LiveData<ArrayList<Movie>>{
        return movieData
    }


    fun removeFavoriteMovie(context: Context, id: String): LiveData<Int>{
        val uriWithId = Uri.parse(CONTENT_URI_MOVIE.toString() + "/" + id)

        val result = MutableLiveData<Int>()
        val values = ContentValues()
        values.put(DatabaseContract.MovieColumns.M_IS_FAVORITE, "tidak")
        result.value = context.contentResolver.update(uriWithId, values, null, null)
        return result
    }


}