package com.mathandoro.coachplus.helpers;

/**
 * Created by Dominik Finkbeiner on 14.05.17.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {

    private boolean hasBorder = false;
    private int borderColor;
    private int borderWidth;

    public CircleTransform(int borderColor, int borderWidth){
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        hasBorder = true;
    }

    public CircleTransform(){
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        if(hasBorder){
            int strokeWidth = borderWidth;
            Paint paint2 = new Paint();
            paint2.setColor(borderColor);
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setAntiAlias(true);
            paint2.setStrokeWidth(strokeWidth);
            canvas.drawCircle(r,r, r-(strokeWidth/2f), paint2);
        }


        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}