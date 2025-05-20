package com.example.media_app

import com.example.media_app.api.WebSocketClient
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.media_app.api.AppDatabase
import com.example.media_app.main.MessageHandler
import com.example.media_app.main.PeopleRepository
import com.example.media_app.main.PostRepository
import com.google.android.material.bottomnavigation.BottomNavigationView


interface OnResponseReceivedListener {
    fun onResponseReceived(response: String)
}


//var pvm




class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    var flag_menu :Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        var serverIp = ip_server // IP адрес сервера
//        var serverPort = 3749
//        val Client = WebSocketClient()
//        val application = requireNotNull(this).application
//        val database = AppDatabase.getInstance(application).postDao()
//        val postRepository = PostRepository(Client,database)
//        val peopleRepository = PeopleRepository(Client,database)
//        val handler = MessageHandler(postRepository,peopleRepository)

        //viewModel.sendMessage("сервер!")
        viewModel.posts.observe(this) { message ->
            //Log.d("MyTag", "Получено: $message")
            // Обнови UI здесь
        }

        viewModel.connectionState.observe(this) { connected ->
            Log.d("MainActivity", "Состояние подключения: $connected")
        }


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        //val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)
        val navHostFragment =supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController =navHostFragment.navController
        val builder = AppBarConfiguration.Builder(navController.graph)
        val appBarConfiguration = builder.build()
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavView.setupWithNavController(navController)
        //toolbar.setupWithNavController(navController,appBarConfiguration)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    fun add_menu(){
        Log.d("Mytag_menu","enable")
        flag_menu =true
        findViewById<BottomNavigationView>(R.id.bottom_nav).visibility=View.VISIBLE
    }
    fun hide_menu(){
        Log.d("Mytag_menu","disable")
        flag_menu =true
        findViewById<BottomNavigationView>(R.id.bottom_nav).visibility=View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController)
                || super.onOptionsItemSelected(item)
    }

}