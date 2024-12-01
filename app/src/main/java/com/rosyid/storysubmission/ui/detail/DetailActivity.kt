package com.rosyid.storysubmission.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.rosyid.storysubmission.R
import com.rosyid.storysubmission.data.remote.pref.ListStoryItem
import com.rosyid.storysubmission.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val story = intent.getParcelableExtra<ListStoryItem>(STORY_ID) as ListStoryItem

        Glide
            .with(this)
            .load(story.photoUrl)
            .into(binding.ivPhotos)
        binding.tvName.text = story.name
        binding.tvDesc.text = story.description
        binding.tvCreated.text = story.createdAt
    }

    companion object {
        const val STORY_ID = "story"
    }
}