package com.stfalcon.sample.features.demo.scroll

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.sample.R
import com.stfalcon.sample.common.extensions.getDrawableCompat
import com.stfalcon.sample.common.extensions.loadImage
import com.stfalcon.sample.common.models.Demo
import com.stfalcon.sample.databinding.ActivityDemoScrollingImagesBinding

class ScrollingImagesDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoScrollingImagesBinding

    private val horizontalImageViews by lazy {
        listOf(
            binding.scrollingHorizontalFirstImage,
            binding.scrollingHorizontalSecondImage,
            binding.scrollingHorizontalThirdImage,
            binding.scrollingHorizontalFourthImage)
    }

    private val verticalImageViews by lazy {
        listOf(
            binding.scrollingVerticalFirstImage,
            binding.scrollingVerticalSecondImage,
            binding.scrollingVerticalThirdImage,
            binding.scrollingVerticalFourthImage)
    }

    private lateinit var viewer: StfalconImageViewer<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoScrollingImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        horizontalImageViews.forEachIndexed { index, imageView ->
            loadImage(imageView, Demo.horizontalImages.getOrNull(index))
            imageView.setOnClickListener {
                openViewer(index, imageView, Demo.horizontalImages, horizontalImageViews)
            }
        }

        verticalImageViews.forEachIndexed { index, imageView ->
            loadImage(imageView, Demo.verticalImages.getOrNull(index))
            imageView.setOnClickListener {
                openViewer(index, imageView, Demo.verticalImages, verticalImageViews)
            }
        }
    }

    private fun openViewer(
        startPosition: Int,
        target: ImageView,
        images: List<String>,
        imageViews: List<ImageView>) {
        viewer = StfalconImageViewer.Builder<String>(this, images, ::loadImage)
            .withStartPosition(startPosition)
            .withTransitionFrom(target)
            .withImageChangeListener { viewer.updateTransitionImage(imageViews.getOrNull(it)) }
            .show()
    }

    private fun loadImage(imageView: ImageView, url: String?) {
        imageView.apply {
            background = getDrawableCompat(R.drawable.shape_placeholder)
            loadImage(url)
        }
    }
}