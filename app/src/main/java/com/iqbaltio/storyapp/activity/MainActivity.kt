package com.iqbaltio.storyapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.iqbaltio.storyapp.R
import com.iqbaltio.storyapp.adapter.LoadStoryAdapter
import com.iqbaltio.storyapp.adapter.StoryAdapter
import com.iqbaltio.storyapp.data.ViewModelFactory
import com.iqbaltio.storyapp.databinding.ActivityMainBinding
import com.iqbaltio.storyapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var adaptor : StoryAdapter
    private val mainViewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story"

        adaptor = StoryAdapter()
        with(binding.rvStory){
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = adaptor.withLoadStateFooter(footer = LoadStoryAdapter{adaptor.retry()})
        }
        mainViewModel.getUser().observe(this) { user ->
            if(user != null){
                if (user.isLogin) {
                    mainViewModel.getStory().observe(this@MainActivity) { adaptor.submitData(lifecycle, it) }
                    binding.fabAdd.setOnClickListener { startActivity(Intent(this, AddStoryActivity::class.java)) }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                val loginSession = LoginSession(this)
                loginSession.logoutSession()
                mainViewModel.logout()
            }
        }
        return true
    }
}