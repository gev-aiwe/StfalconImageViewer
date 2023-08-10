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

import android.content.Context
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.stfalcon.imageviewer.R
import com.stfalcon.imageviewer.viewer.builder.BuilderData
import com.stfalcon.imageviewer.viewer.view.ImageViewerView

internal class ImageViewerDialog<T>(
    context: Context,
    private val builderData: BuilderData<T>
) {

    private val dialog: AlertDialog
    private val viewerView: ImageViewerView<T> = ImageViewerView(context)
    private var animateOpen = true

    init {
        setupViewerView()
        dialog = AlertDialog
            .Builder(context, R.style.ImageViewerDialog_Default)
            .setView(viewerView)
            .setOnKeyListener { _, keyCode, event -> onDialogKeyEvent(keyCode, event) }
            .create()
            .apply {
                setOnShowListener { viewerView.open(builderData.transitionView, animateOpen) }
                setOnDismissListener { builderData.onDismissListener?.onDismiss() }
            }

        viewerView.onOverlayVisibilityChanged = { visible ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dialog.window?.insetsController?.let { windowInsetsController ->
                    if (visible) {
                        windowInsetsController.show(WindowInsets.Type.statusBars())
                    } else {
                        windowInsetsController.hide(WindowInsets.Type.statusBars())
                    }
                }
            } else {
                dialog.window?.decorView?.systemUiVisibility = if (visible) {
                    View.SYSTEM_UI_FLAG_VISIBLE
                } else {
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                }
            }
        }
    }

    fun show(animate: Boolean) {
        animateOpen = animate
        dialog.show()
    }

    fun close() {
        viewerView.close()
    }

    fun dismiss() {
        dialog.dismiss()
    }

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
        viewerView.updateTransitionImage(imageView)
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
        viewerView.apply {
            isZoomingAllowed = builderData.isZoomingAllowed
            isSwipeToDismissAllowed = builderData.isSwipeToDismissAllowed

            containerPadding = builderData.containerPaddingPixels
            imagesMargin = builderData.imageMarginPixels
            overlayView = builderData.overlayView

            setBackgroundColor(builderData.backgroundColor)


            setImages(builderData.images, builderData.startPosition, builderData.imageLoader,
                    builderData.viewHolderLoader)

            onPageChange = { position -> builderData.imageChangeListener?.onImageChange(position) }
            onDismiss = { dialog.dismiss() }
        }
    }
}