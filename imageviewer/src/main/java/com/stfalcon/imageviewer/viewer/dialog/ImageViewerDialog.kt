/*
 * Copyright 2018 stfalcon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stfalcon.imageviewer.viewer.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.stfalcon.imageviewer.R
import com.stfalcon.imageviewer.listeners.OnDismissListener
import com.stfalcon.imageviewer.listeners.OnImageChangeListener
import com.stfalcon.imageviewer.loader.ImageLoader
import com.stfalcon.imageviewer.loader.OverlayLoader
import com.stfalcon.imageviewer.viewer.builder.BuilderData
import com.stfalcon.imageviewer.viewer.view.ImageViewerView
import kotlin.math.max

class ImageViewerDialog<T> : DialogFragment() {

//    private val dialog: AlertDialog
//    private val viewerView: ImageViewerView<T> = ImageViewerView(context)

    //private lateinit var dialog: AlertDialog
    private lateinit var viewerView: ImageViewerView<T>
    private lateinit var builderData: BuilderData<T>

    private var animateOpen = true

    companion object {
        private const val TAG = "SUKA"

        private const val builderDataKey = "BuilderDataKey"

        fun <T> newInstance(builderData: BuilderData<T>): ImageViewerDialog<T> {
            val args = Bundle()
            args.putSerializable(builderDataKey, builderData)
            val f = ImageViewerDialog<T>()
            f.arguments = args
            return f
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        builderData = arguments?.getSerializable(builderDataKey) as BuilderData<T>

        Log.d(TAG, "onCreateDialog: setupViews")
        setupViewerView()

        val dialog = AlertDialog
            .Builder(requireContext(), R.style.ImageViewerDialog)
            .setView(viewerView)
            .setOnKeyListener { _, keyCode, event -> onDialogKeyEvent(keyCode, event) }
            .create()
            .apply {
                setOnShowListener { viewerView.open(builderData.transitionView, animateOpen) }
                setOnDismissListener { (activity as? OnDismissListener)?.onDismiss() }
            }

        viewerView.onOverlayVisibilityChanged = { visible ->
            if (visible) showSystemBar() else hideSystemBar()
        }

        dialog.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insetsController = window.decorView.windowInsetsController ?: return@let
                val systemBarsAppearance = insetsController.systemBarsAppearance
                insetsController.setSystemBarsAppearance(
                    systemBarsAppearance,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS.inv()
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }

        return dialog
    }

    private fun showSystemBar() {
        dialog?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowInsetsController = window.decorView.windowInsetsController
                windowInsetsController?.show(WindowInsets.Type.statusBars())
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    private fun hideSystemBar() {
        dialog?.window?.let { window ->
            val windowManagerLayoutParams = window.attributes

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                windowManagerLayoutParams.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                windowManagerLayoutParams.fitInsetsTypes = WindowInsets.Type.statusBars()
                val windowInsetsController = window.decorView.windowInsetsController
                windowInsetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                        )
            }
            window.attributes = windowManagerLayoutParams
        }
    }

    fun show(fragmentManager: FragmentManager, animate: Boolean) {
        animateOpen = animate
        show(fragmentManager, "")
    }

    fun close() {
        viewerView.close()
    }

//    override fun dismiss() {
//        super.dismiss()
//        dialog?.dismiss()
//    }

    fun updateImages(images: List<T>) {
        viewerView.updateImages(images)
    }

    fun getCurrentPosition(): Int =
        viewerView.currentPosition

    fun setCurrentPosition(position: Int, smoothScroll: Boolean): Int {
        viewerView.setCurrentPosition(position, smoothScroll)
        return viewerView.currentPosition
    }

    fun updateTransitionImage(imageView: ImageView?) {
        Handler(Looper.getMainLooper()).post {
            Log.d(TAG, "updateTransitionImage: isInit viewer: ${::viewerView.isInitialized}")
            viewerView.updateTransitionImage(imageView)
        }
    }

    private fun onDialogKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
            event.action == KeyEvent.ACTION_UP &&
            !event.isCanceled
        ) {
            if (viewerView.isScaled) {
                viewerView.resetScale()
            } else {
                viewerView.close()
            }
            return true
        }
        return false
    }

    private fun setupViewerView() {
        viewerView = ImageViewerView(requireContext())
        Log.d(TAG, "setupViewerView: viewerView: $viewerView")
        viewerView.apply {
            isZoomingAllowed = builderData.isZoomingAllowed
            isSwipeToDismissAllowed = builderData.isSwipeToDismissAllowed

            containerPadding = builderData.containerPaddingPixels
            imagesMargin = builderData.imageMarginPixels

            overlayView = (activity as? OverlayLoader<T>)?.loadOverlayFor(
                max(viewerView.currentPosition, builderData.startPosition),
            )

            setBackgroundColor(builderData.backgroundColor)

            val imageLoader = activity as? ImageLoader<T>

            if (imageLoader != null) {
                setImages(builderData.images, builderData.startPosition, imageLoader, null)
            }

            onPageChange = { position ->
                (activity as? OnImageChangeListener)?.onImageChange(position)
            }
            onDismiss = {
                dismiss()
                (activity as? OnDismissListener)?.onDismiss()
            }
        }
    }
}