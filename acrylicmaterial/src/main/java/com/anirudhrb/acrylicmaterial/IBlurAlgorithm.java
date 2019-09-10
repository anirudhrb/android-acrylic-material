package com.anirudhrb.acrylicmaterial;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface to represent a blur algorithm.
 */
public interface IBlurAlgorithm {
    /**
     * Applies a blur on the input bitmap.
     *
     * @param input the input bitmap
     * @return output bitmap
     */
    @Nullable
    Bitmap applyOn(@NonNull Bitmap input, float radius);
}
