package com.stfalcon.sample.common.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.Insets
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.stfalcon.sample.R
import com.stfalcon.sample.common.extensions.sendShareIntent
import com.stfalcon.sample.common.models.Poster

class PosterOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var onDeleteClick: (Poster) -> Unit = {}

    init {
        View.inflate(context, R.layout.view_poster_overlay, this)
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun update(poster: Poster) {
        findViewById<TextView>(R.id.posterOverlayDescriptionText).text = poster.description
        findViewById<View>(R.id.posterOverlayShareButton).setOnClickListener { context.sendShareIntent(poster.url) }
        findViewById<View>(R.id.posterOverlayDeleteButton).setOnClickListener { onDeleteClick(poster) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val insets = getSystemBarWindowInsetsCompat()
        updatePadding(top = insets.top, bottom = insets.bottom)
    }
}


fun View.getSystemBarWindowInsetsCompat(): Insets {
    return WindowInsetsCompat.toWindowInsetsCompat(rootWindowInsets)
        .getInsets(WindowInsetsCompat.Type.systemBars())
}