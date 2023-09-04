package com.stfalcon.imageviewer.loader;

import android.view.View;

import androidx.annotation.Nullable;

import com.stfalcon.imageviewer.viewer.dialog.ImageViewerDialog;

public interface OverlayLoader<T> {

    @Nullable View loadOverlayFor(int position);
}
