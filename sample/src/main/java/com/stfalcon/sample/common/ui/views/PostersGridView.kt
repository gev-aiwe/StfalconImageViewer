package com.stfalcon.sample.common.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.stfalcon.sample.R
import com.stfalcon.sample.common.models.Demo
import com.stfalcon.sample.common.models.Poster

class PostersGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var imageLoader: ((ImageView, Poster?) -> Unit)? = null
    var onPosterClick: ((Int, ImageView) -> Unit)? = null

    val imageViews by lazy {
        mapOf<Int, ImageView>(
            0 to findViewById(R.id.postersFirstImage),
            1 to findViewById(R.id.postersSecondImage),
            2 to findViewById(R.id.postersThirdImage),
            3 to findViewById(R.id.postersFourthImage),
            4 to findViewById(R.id.postersFifthImage),
            5 to findViewById(R.id.postersSixthImage),
            6 to findViewById(R.id.postersSeventhImage),
            7 to findViewById(R.id.postersEighthImage),
            8 to findViewById(R.id.postersNinthImage))
    }

    init {
        View.inflate(context, R.layout.view_posters_grid, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        imageViews.values.forEachIndexed { index, imageView ->
            imageLoader?.invoke(imageView, Demo.posters.getOrNull(index))
            imageView.setOnClickListener { onPosterClick?.invoke(index, imageView) }
        }
    }
}