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
 * <p>Generates a drawable that can be used as a background as an acrylic
 * material background.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * AcrylicMaterial
 *     .with(MainActivity.this)
 *     .background(R.drawable.background_image)
 *     .useDefaults()
 *     .generate();
 * </pre>
 *
 * <p>This will generate an acrylic material background based on the given back-
 * ground image ({@code R.drawable.background_image}) using the default options.</p>
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
    @Nullable
    private IBlurAlgorithm mBlurAlgorithm;
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
     * Creates a new {@code AcrylicMaterial} instance.
     *
     * @param context context
     * @return an {@code AcrylicMaterial} instance
     */
    public static AcrylicMaterial with(@NonNull Context context) {
        Objects.requireNonNull(context);
        return new AcrylicMaterial(context);
    }

    /**
     * Sets the background for the acrylic material effect.
     *
     * @param drawableRes res id of the background drawable
     * @return {@code AcrylicMaterial} instance
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
     * @return current {@code AcrylicMaterial} instance
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
     * <p>
     * Default value is 1.0 i.e. no scaling.
     *
     * @param scaleFactor scaling factor
     * @return current {@code AcrylicMaterial} instance
     */
    @SuppressWarnings("WeakerAccess")
    public AcrylicMaterial scaleBy(float scaleFactor) {
        if (!(scaleFactor >= 0.0f) || !(scaleFactor <= 1.0f))
            throw new AssertionError("scaleFactor must be between 0 and 1");

        mScaleFactor = scaleFactor;
        return this;
    }

    /**
     * Configures the current {@code AcrylicMaterial} instance to use a Gaussian blur
     * with the given blur radius.
     * Allowed range of  `radius` is (0, 25].
     *
     * @param radius radius for the blur
     * @return current {@code AcrylicMaterial} instance
     */
    @SuppressWarnings("unused")
    public AcrylicMaterial gaussianBlur(float radius) {
        mBlurAlgorithm = new GaussianBlur(mContext);
        mBlurRadius = radius;
        return this;
    }

    /**
     * Configures the current {@code AcrylicMaterial} instance to use a stack blur
     * with the given blur radius.
     * <p>
     * More about Stack Blur: http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * <p>
     * {@code radius} must be greater or equal to 1.
     *
     * @param radius blur radius
     * @return current {@code AcrylicMaterial} instance
     */
    @SuppressWarnings("WeakerAccess")
    public AcrylicMaterial stackBlur(int radius) {
        mBlurAlgorithm = new StackBlur();
        mBlurRadius = radius;
        return this;
    }

    /**
     * Sets a noise layer. The noise layer is the topmost layer.
     *
     * @param res res id of the drawable to use as a noise layer
     * @return current {@code AcrylicMaterial} instance
     */
    @SuppressWarnings("WeakerAccess")
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
     * @return current {@code AcrylicMaterial} instance
     */
    @SuppressWarnings("unused")
    public AcrylicMaterial tintColor(@ColorInt int argb) {
        GradientDrawable tintLayer = new GradientDrawable();
        tintLayer.setShape(GradientDrawable.RECTANGLE);
        tintLayer.setColor(argb);

        mTintLayer = tintLayer;

        return this;
    }

    /**
     * Applies a saturation on the blurred background.
     * <p>
     * Default value is 1.0f i.e. no saturation.
     *
     * @param saturation saturation value. 0.0f is gray scale, 1.0f is identity.
     * @return current {@code AcrylicMaterial} instance
     */
    @SuppressWarnings("WeakerAccess")
    public AcrylicMaterial saturation(float saturation) {
        mSaturation = saturation;
        return this;
    }

    /**
     * Sets default options as follows:
     * 1. {@code scaleBy(0.85f)}
     * 2. {@code stackBlur(80)}
     * 3. {@code saturation(2f)}
     * 4. Noise texture: https://www.transparenttextures.com/patterns/dotnoise-light-grey.png
     *
     * @return current {@code AcrylicMaterial} instance
     */
    public AcrylicMaterial useDefaults() {
        return scaleBy(0.85f)
                .stackBlur(80)
                .saturation(2f)
                .noise(R.drawable.noise_layer);
    }

    /**
     * Generates a drawable with the acrylic material effect based on the
     * configuration set using the other methods in this class.
     * <p>
     * This drawable can be used a background wherever the acrylic material
     * effect is needed.
     *
     * @return the resultant drawable
     */
    public Drawable generate() {
        if (mBackground == null) {
            throw new IllegalStateException("No background set.");
        }

        if (mBlurAlgorithm == null) {
            throw new IllegalStateException("No blur algorithm specified.");
        }

        long start = System.currentTimeMillis();

        final Bitmap scaledBackground = scaleBitmap(Utils.bitmapFromDrawable(mBackground), mScaleFactor);
        final Bitmap blurred = mBlurAlgorithm.applyOn(scaledBackground, mBlurRadius);

        if (blurred == null) {
            Log.wtf(TAG, "Blur failed!");
            throw new IllegalArgumentException("Failed to apply blur. " +
                    "Check if the given radius was out of the range of permitted values!");
        }

        final Bitmap blurWithSaturation = Utils.saturateBitmap(blurred, mSaturation);
        final Drawable blurLayer = new BitmapDrawable(mContext.getResources(), blurWithSaturation);

        final List<Drawable> layers = new ArrayList<>();
        layers.add(blurLayer);

        // add tint layer if configured
        if (mTintLayer != null) {
            layers.add(mTintLayer);
        }

        // add noise layer if configured
        if (mNoiseLayer != null) {
            layers.add(mNoiseLayer);
        }

        LayerDrawable result = new LayerDrawable(layers.toArray(new Drawable[]{}));

        Log.i(TAG, String.format("generate() took %d ms", System.currentTimeMillis() - start));
        return result;
    }

    /**
     * Creates a new bitmap that is a scaled version of {@code input}.
     * The size of the scaled bitmap will be {@code input.getWidth() * scale} by
     * {@code input.getHeight() * scale} rounded to the closest int.
     *
     * @param input the bitmap to scale
     * @param scale the factor by which to scale
     * @return scaled bitmap
     */
    private Bitmap scaleBitmap(Bitmap input, float scale) {
        int width = Math.round(input.getWidth() * scale);
        int height = Math.round(input.getHeight() * scale);
        return Bitmap.createScaledBitmap(input, width, height, false);
    }
}
