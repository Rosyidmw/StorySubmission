package com.rosyid.storysubmission.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.rosyid.storysubmission.R
import com.rosyid.storysubmission.adapter.StoryAdapter
import com.rosyid.storysubmission.databinding.ActivityMainBinding
import com.rosyid.storysubmission.ui.ViewModelFactory
import com.rosyid.storysubmission.ui.login.LoginActivity
import com.rosyid.storysubmission.ui.maps.MapsActivity
import com.rosyid.storysubmission.ui.story.AddStoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val storyAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
        observePagedStories()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        binding.rvStory.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "User session: $user")
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun observePagedStories() {
        viewModel.getPagedStories().observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }

        storyAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Error) {
                binding.progress.visibility = View.GONE
                val error = (loadState.refresh as LoadState.Error).error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            } else {
                binding.progress.visibility = if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

}