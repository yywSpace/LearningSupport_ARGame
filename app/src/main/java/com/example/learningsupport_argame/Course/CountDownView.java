package com.example.learningsupport_argame.Course;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**

 * 重绘View实现倒计时界面

 * 通过setInitialSecond设置总时间

 * 通过updateSecond更新时间

 * 最后调用invalidate更新View

 */

public class CountDownView extends View {

        int radius = 200;

        int strokeWidth = 2;

        private float seconds;

        private float initialSecond;

        Paint mPaint;



        public CountDownView(Context context, AttributeSet attrs) {

            super(context, attrs);

            mPaint = new Paint();

            initPaint();

        }





        @Override

        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            drawCircle(canvas);

            if (initialSecond == 0)

                return;

            float sweepAngle = 360 - seconds / initialSecond * 360;

            //float endX = getWidth() / 2 + (float)Math.cos(90 - sweepAngle) * radius;

            //float endY = getHeight() / 2 - (float)Math.sin(90 - sweepAngle) * radius;



            //drawScanLine(canvas, new PointF(endX, endY));

            drawScanArea(canvas, sweepAngle);



        }





        // 圆-线-圆

        private void drawScanLine(Canvas canvas, PointF endPoint) {

            mPaint.reset();

            mPaint.setStyle(Paint.Style.FILL);

            mPaint.setAntiAlias(true);

            int startX = getWidth() / 2;

            int startY = getWidth() / 2;

            canvas.drawCircle(startX, startY, 6, mPaint);

            canvas.drawLine(startX, startY, endPoint.x, endPoint.y, mPaint);//绘制整点长的刻度

            canvas.drawCircle(endPoint.x, endPoint.y, 6, mPaint);

            initPaint();

        }



        private void drawScanArea(Canvas canvas, float sweepAngle) {

            mPaint.reset();

            mPaint.setColor(Color.GRAY);

            mPaint.setStyle(Paint.Style.FILL);

            mPaint.setAntiAlias(true);//设置抗锯齿，使圆形更加圆滑

            RectF oval = new RectF(getWidth() / 2 - radius, getHeight() / 2 - radius,

                    getWidth() / 2 + radius, getHeight() / 2 + radius);

            canvas.drawArc(oval, -90, sweepAngle, true, mPaint);

            initPaint();

        }



        private void drawCircle(Canvas canvas) {

            //获得圆的圆点坐标

            int x = getWidth() / 2;

            int y = getHeight() / 2;

            canvas.drawCircle(x, y, radius, mPaint);

        }



        private void initPaint() {

            mPaint.reset();

            mPaint.setStyle(Paint.Style.STROKE);//设置描边

            mPaint.setStrokeWidth(strokeWidth);//设置描边线的粗细

            mPaint.setAntiAlias(true);//设置抗锯齿，使圆形更加圆滑

        }



        public void updateSecond(float seconds) {

            this.seconds = seconds;

        }

        public void setInitialSecond(float initialSecond) {

            this.initialSecond = initialSecond;

        }

    }
