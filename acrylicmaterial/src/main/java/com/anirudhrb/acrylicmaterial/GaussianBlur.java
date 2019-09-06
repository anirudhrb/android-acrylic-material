package com.anirudhrb.acrylicmaterial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;

import java.util.Objects;

class GaussianBlur {
    @NonNull
    private final Drawable mSource;
    private final float mScale;
    private final float mRadius;
    @NonNull
    private final Context mContext;

    /**
     * @param source the drawable that needs to be blurred
     * @param scale scaling (if any) to be applied to the drawable before blurring
     * @param radius blur radius in range (0, 25]
     */
     GaussianBlur(@NonNull Context context,
                        @NonNull Drawable source,
                        float scale,
                        float radius) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(source);
        mContext = context;
        this.mSource = source;
        this.mScale = scale;
        this.mRadius = radius;
    }

    Bitmap apply() {
        Bitmap sourceBitmap = scale(drawableToBitmap(mSource), mScale);
        Bitmap blurred = Bitmap.createBitmap(sourceBitmap);

        RenderScript rs = RenderScript.create(mContext);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation tmpIn = Allocation.createFromBitmap(rs, sourceBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, sourceBitmap);

        blurScript.setRadius(mRadius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(blurred);

        return blurred;
    }

    private Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap scale(Bitmap image, float scale) {
        if (scale == 1f) {
            return image;
        }

        int width = Math.round(image.getWidth() * scale);
        int height = Math.round(image.getHeight() * scale);
        return Bitmap.createScaledBitmap(image, width, height, false);
    }
}
