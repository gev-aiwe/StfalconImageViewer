package com.stfalcon.sample.features.demo.grid

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.listeners.OnImageChangeListener
import com.stfalcon.imageviewer.loader.ImageLoader
import com.stfalcon.imageviewer.loader.OverlayLoader
import com.stfalcon.imageviewer.viewer.dialog.ImageViewerDialog
import com.stfalcon.sample.R
import com.stfalcon.sample.common.extensions.getDrawableCompat
import com.stfalcon.sample.common.extensions.loadImage
import com.stfalcon.sample.common.models.Demo
import com.stfalcon.sample.common.models.Poster
import kotlinx.android.synthetic.main.activity_demo_posters_grid.*

class PostersGridDemoActivity : AppCompatActivity(), ImageLoader<String>, OverlayLoader<String>, OnImageChangeListener {

    private lateinit var viewer: StfalconImageViewer<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_posters_grid)

        postersGridView.apply {
            imageLoader = ::loadPosterImage
            onPosterClick = ::openViewer
        }


        if (savedInstanceState != null) {
            viewer = StfalconImageViewer.Builder<String>(this, Demo.posters.map { it.url }).build()
            viewer.show(supportFragmentManager)
//            viewer.updateImages(Demo.posters.map { it.url })
        }
    }

    private fun openViewer(startPosition: Int, target: ImageView) {
        viewer = StfalconImageViewer.Builder<String>(this, Demo.posters.map { it.url })
            .withStartPosition(startPosition)
            .withTransitionFrom(target)
            .show(supportFragmentManager)
    }

    private fun loadPosterImage(imageView: ImageView, poster: Poster?) {
        imageView.apply {
            background = getDrawableCompat(R.drawable.shape_placeholder)
            loadImage(poster?.url)
        }
    }

    override fun onImageChange(position: Int) {
        viewer.updateTransitionImage(postersGridView.imageViews[position])
    }

    override fun loadImage(imageView: ImageView?, image: String?) {
        imageView?.apply {
            background = getDrawableCompat(R.drawable.shape_placeholder)
            loadImage(image)
        }
    }

    override fun loadOverlayFor(position: Int): View? {
        return null
    }
}