package com.anirudhrb.acrylicmaterial;

import android.graphics.*;

class Utils {
    static Bitmap saturateBitmap(Bitmap src, float saturation) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        ColorMatrix colorMatrix = new ColorMatrix();
        Paint paint = new Paint();

        colorMatrix.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0, 0, paint);

        return result;
    }
}
