package com.rezziqbal.favoritemovieapp

import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rezziqbal.favoritemovieapp.db.DatabaseContract
import com.rezziqbal.favoritemovieapp.ui.movie.MovieViewModel
import com.rezziqbal.favoritemovieapp.ui.tv.TVViewModel

class MainActivity : AppCompatActivity() {
    private var TAG = MainActivity::class.java.simpleName
    private lateinit var mViewModel : MovieViewModel
    private lateinit var tViewModel: TVViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        tViewModel = ViewModelProviders.of(this).get(TVViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(MovieViewModel::class.java)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_movie, R.id.navigation_tv
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        registerObserver()


    }

    private fun registerObserver() {
        Log.d(TAG, "mulai register")
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        Log.d(TAG, "beres handler")

        val movieObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadMovieAsync()
            }
        }

        val tvObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadTvAsync()
            }
        }

        Log.d(TAG, "akhir register")
        contentResolver.registerContentObserver(DatabaseContract.MovieColumns.CONTENT_URI_MOVIE, true, movieObserver)
        contentResolver.registerContentObserver(DatabaseContract.TVColumns.CONTENT_URI_TV, true, tvObserver)
    }

    private fun loadTvAsync() {
        Log.d(TAG, "ambil data tv")
        tViewModel.getDataFromDatabase(this)
    }

    private fun loadMovieAsync() {
        Log.d(TAG, "ambil data movie")
        mViewModel.getDataFromDatabase(this)
    }
}
