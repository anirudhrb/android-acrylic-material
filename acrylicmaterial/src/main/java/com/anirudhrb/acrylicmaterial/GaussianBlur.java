package com.anirudhrb.acrylicmaterial;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

class GaussianBlur implements IBlurAlgorithm {
    @NonNull
    private final Context mContext;

    /**
     * @param context context for RenderScript instance
     */
    GaussianBlur(@NonNull Context context) {
        Objects.requireNonNull(context);
        mContext = context;
    }

    @Nullable
    public Bitmap applyOn(@NonNull Bitmap input, float radius) {
        Bitmap blurred = Bitmap.createBitmap(input);

        RenderScript rs = RenderScript.create(mContext);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation tmpIn = Allocation.createFromBitmap(rs, input);
        Allocation tmpOut = Allocation.createFromBitmap(rs, input);

        blurScript.setRadius(radius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(blurred);

        return blurred;
    }
}
