package com.example.studytime

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.studytime.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private   lateinit var imghome: ImageView
    private   lateinit var imggif: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
         imghome=findViewById(R.id.imageView4)
        imggif=findViewById(R.id.imageView2)
        binding.appBarMain.fab.setOnClickListener { view ->
          Snackbar.make(view, "Creating new Task", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show()
            val intent = Intent(this@MainActivity, Dataset::class.java)

            startActivity(intent)



        }
        //animation
       // Glide.with(this).load(R.drawable.homeback).into(imghome)

        //val runsplash = java.util.Timer()
        //val ShowSplash: TimerTask = object : TimerTask() {
        //    override fun run() {
          //      finish()

            //}
        //}
        //runsplash.schedule(ShowSplash,System.currentTimeMillis())
        //animation
        Glide.with(this).load(R.drawable.lofi).into(imggif)

        val runsplash2 = java.util.Timer()
        val ShowSplash2: TimerTask = object : TimerTask() {
            override fun run() {
                finish()

            }
        }
        runsplash2.schedule(ShowSplash2,System.currentTimeMillis())



        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
      //  navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { item ->

            val id = item.itemId



            when (id) {

                R.id.nav_about -> {

                    val intent = Intent(this@MainActivity, About::class.java)

                    startActivity(intent)

                }

                R.id.nav_login -> {

                    val intent = Intent(this@MainActivity, Login::class.java)

                    startActivity(intent)

                }



                R.id.nav_timer -> {

                    val intent = Intent(this@MainActivity, Timer::class.java)

                    startActivity(intent)

                }
                R.id.nav_dataset -> {

                    val intent = Intent(this@MainActivity, Dataset::class.java)

                    startActivity(intent)

                }
                R.id.nav_view -> {

                    val intent = Intent(this@MainActivity, View::class.java)

                    startActivity(intent)

                }



            }

            true

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}