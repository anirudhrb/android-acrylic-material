package com.anirudhrb.acrylicmaterial;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Configures and generates the acrylic material effect.
 * <p>
 * TODO: add example and more info.
 */
public class AcrylicMaterial {
    private static final String TAG = "AcrylicMaterial";

    @NonNull
    private Context mContext;
    @Nullable
    private Drawable mBackground;
    @Nullable
    private Drawable mNoiseLayer;
    @Nullable
    private Drawable mTintLayer;
    private float mSaturation = 1f;

    /**
     * The factor the original background image needs to be scaled by.
     * Scaling down the original image may give better performance while
     * blurring.
     * <p>
     * By default no scaling will be done.
     */
    private float mScaleFactor = 1.0f;

    /**
     * Radius of the blur. Range: (0, 25].
     * Using max radius by default.
     */
    private float mBlurRadius = 25f;

    private AcrylicMaterial(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Creates a new `AcrylicMaterial` instance.
     *
     * @param context context
     * @return an `AcrylicMaterial` instance
     */
    public static AcrylicMaterial with(@NonNull Context context) {
        Objects.requireNonNull(context);
        return new AcrylicMaterial(context);
    }

    /**
     * Sets the background for the acrylic material effect.
     *
     * @param drawableRes res id of the background drawable
     * @return `AcrylicMaterial` instance
     */
    public AcrylicMaterial background(@DrawableRes int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableRes);
        if (drawable == null) {
            throw new IllegalArgumentException("No drawable with the given ID was found");
        }

        mBackground = drawable;

        return this;
    }

    /**
     * Sets the background for the acrylic material effect.
     *
     * @param drawable the drawable to use as background
     * @return current `AcrylicMaterial` instance
     */
    public AcrylicMaterial background(@NonNull Drawable drawable) {
        Objects.requireNonNull(drawable);
        mBackground = drawable;
        return this;
    }

    /**
     * Sets the scaling factor by which the original background should
     * be resized before applying the blur effect on it. If the original
     * image is too large, scaling it down will improve blur performance.
     *
     * Default value is 1.0 i.e. no scaling.
     *
     * @param scaleFactor scaling factor
     * @return current `AcrylicMaterial` instance
     */
    public AcrylicMaterial scaleBy(float scaleFactor) {
        if (!(scaleFactor >= 0.0f) || !(scaleFactor <= 1.0f))
            throw new AssertionError("scaleFactor must be between 0 and 1");

        mScaleFactor = scaleFactor;
        return this;
    }

    /**
     * Radius for the blur. Range: (0, 25].
     *
     * @param blurRadius radius for the blur
     * @return current `AcrylicMaterial` instance
     */
    public AcrylicMaterial blurRadius(float blurRadius) {
        mBlurRadius = blurRadius;
        return this;
    }

    /**
     * Sets a noise layer. The noise layer is the topmost layer.
     *
     * @param res res id of the drawable to use as a noise layer
     * @return current `AcrylicMaterial` instance
     */
    public AcrylicMaterial noise(@DrawableRes int res) {
        mNoiseLayer = ContextCompat.getDrawable(mContext, res);
        if (mNoiseLayer == null) {
            Log.w(TAG, "Unable to set noise layer drawable. " +
                    "Couldn't find the drawable with the given resource id");
        }

        return this;
    }


    /**
     * Sets a tint layer with the given color. The tint layer is just below
     * the noise layer. Do not set 100% opacity for tint color as it will completely
     * obscure the lower layers.
     *
     * @param argb tint color (as int)
     * @return current `AcrylicMaterial` instance
     */
    public AcrylicMaterial tintColor(@ColorInt int argb) {
        GradientDrawable tintLayer = new GradientDrawable();
        tintLayer.setShape(GradientDrawable.RECTANGLE);
        tintLayer.setColor(argb);

        mTintLayer = tintLayer;

        return this;
    }

    /**
     * Applies a saturation on the blurred background.
     *
     * Default value is 1.0f i.e. no saturation.
     *
     * @param saturation saturation value. 0.0f is gray scale, 1.0f is identity.
     * @return current `AcrylicMaterial` instance
     */
    public AcrylicMaterial saturation(float saturation) {
        mSaturation = saturation;
        return this;
    }

    /**
     * Generates a drawable with the acrylic material effect based on the
     * configuration set using the other methods in this class.
     *
     * This drawable can be used a background wherever the acrylic material
     * effect is needed.
     *
     * @return the resultant drawable
     */
    public Drawable generate() {
        if (mBackground == null) {
            throw new IllegalStateException("No background set for acrylic material.");
        }

        long start = System.currentTimeMillis();

        final GaussianBlur gaussianBlur = new GaussianBlur(mContext, mBackground, mScaleFactor, mBlurRadius);
        final Bitmap blurWithSaturation = Utils.saturateBitmap(gaussianBlur.apply(), mSaturation);
        Drawable blurLayer = new BitmapDrawable(mContext.getResources(), blurWithSaturation);

        List<Drawable> layers = new ArrayList<>();
        layers.add(blurLayer);

        // add tint layer if available
        if (mTintLayer != null) {
            layers.add(mTintLayer);
        }

        // add noise layer if available
        if (mNoiseLayer != null) {
            layers.add(mNoiseLayer);
        }

        LayerDrawable result = new LayerDrawable(layers.toArray(new Drawable[] {}));

        Log.i(TAG, String.format("generate took %d ms", System.currentTimeMillis() - start));
        return result;
    }
}
