package com.rezziqbal.moviecatalogue.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.rezziqbal.moviecatalogue.db.DatabaseContract.AUTHORITY
import com.rezziqbal.moviecatalogue.db.DatabaseContract.MovieColumns.Companion.CONTENT_URI_MOVIE
import com.rezziqbal.moviecatalogue.db.DatabaseContract.TABLE_MOVIE
import com.rezziqbal.moviecatalogue.db.MovieHelper

class MovieProvider : ContentProvider() {
    companion object {
        private const val MOVIE = 1
        private const val MOVIE_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var movieHelper: MovieHelper
        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_MOVIE, MOVIE)
            sUriMatcher.addURI(AUTHORITY,
                "$TABLE_MOVIE/#",
                MOVIE_ID)
        }
    }
    override fun onCreate(): Boolean {
        movieHelper = MovieHelper.getInstance(context as Context)
        movieHelper.open()
        return true
    }

    //query favorite movie
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor: Cursor?
        when (sUriMatcher.match(uri)) {
            MOVIE -> cursor = movieHelper.queryByFavorite("iya")
            MOVIE_ID -> cursor = movieHelper.getById(uri.lastPathSegment.toString())
            else -> cursor = null
        }

        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (MOVIE) {
            sUriMatcher.match(uri) -> movieHelper.insertValue(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI_MOVIE, null)

        return Uri.parse("$CONTENT_URI_MOVIE/$added")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val updated: Int = when (MOVIE_ID){
            sUriMatcher.match(uri) -> movieHelper.update(uri.lastPathSegment.toString(), values!!)
            else -> 0
        }
        return updated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (MOVIE_ID) {
            sUriMatcher.match(uri) -> movieHelper.delete(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI_MOVIE, null)

        return deleted
    }
}
