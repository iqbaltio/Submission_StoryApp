package com.iqbaltio.storyapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.iqbaltio.storyapp.R
import com.iqbaltio.storyapp.data.ListStory
import com.iqbaltio.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detailstory_activity)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val dataDetail = intent.getParcelableExtra<ListStory>(EXTRA_NAME) as ListStory

        binding.apply {
            tvDetailName.text = dataDetail.name
            tvDetailCreated.text  = dataDetail.createdAt?.removeRange(16, dataDetail.createdAt!!.length)
            tvDetailDescription.text = dataDetail.description
            Glide.with(this@DetailActivity)
                .load(dataDetail.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDetailPhoto)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}