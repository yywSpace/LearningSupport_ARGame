package com.example.learningsupport_argame.FeedbackModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.learningsupport_argame.R;

public class RatingBarView extends View {
    private static String TAG = "RatingBarView";
    private Bitmap bitmapRedStar;
    private Bitmap bitmapGrayStar;
    private boolean hasResetImage;
    private float rating = 0.3f;
    private Rect src;
    private RectF dst;

    public RatingBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmapRedStar = BitmapFactory.decodeResource(getResources(),
                R.drawable.feedback_rating_star_red);
        bitmapGrayStar = BitmapFactory.decodeResource(getResources(),
                R.drawable.feedback_rating_star_gray);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!hasResetImage) {
            bitmapRedStar = resetBitmapSize(bitmapRedStar, getWidth(), getHeight());
            bitmapGrayStar = resetBitmapSize(bitmapGrayStar, getWidth(), getHeight());
            // 指定图片绘制区域(小于bitmap进行剪裁)
            src = new Rect(0, 0, (int) (bitmapRedStar.getWidth() * rating), bitmapRedStar.getHeight());
            // 指定图片在屏幕上显示的区域(小于bitmap进行缩放)
            dst = new RectF(0, 0, bitmapRedStar.getWidth() * rating, bitmapRedStar.getHeight());
            hasResetImage = true;
        }

        canvas.drawBitmap(bitmapGrayStar, 0, 0, null);
        canvas.drawBitmap(bitmapRedStar, src, dst, null);
    }

    Bitmap resetBitmapSize(Bitmap bitmap, int width, int height) {
        // 计算缩放比例
        float scaleWidth = ((float) width) / bitmap.getWidth();
        float scaleHeight = ((float) height) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}