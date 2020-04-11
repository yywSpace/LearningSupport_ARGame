package com.example.learningsupport_argame.MonitorModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.learningsupport_argame.ARModel.Utils.Vector3Utils;

import java.util.ArrayList;
import java.util.List;

public class CountDownView extends View {
    private int innerCircleColor;
    // 半径
    private float radius = 300;
    // 内外圈间隔
    private float gapOfInnerOuter = 60;
    // 内圈宽度
    private float innerStrokeWidth = 36;
    // 三角箭头底边长
    private float triangleBase = 4;
    // 当前时间
    private float currentSeconds = 0;
    // 初始时间
    private float initialSecond = 0;
    // 时间标签大小
    private float timeLabelSize = 53;
    // 进度条子项个数
    private float processItemCount = 120;
    // 进度条宽度
    private float processItemWidth = 10;
    // 进度表，通过此表及已旋转的角度绘制进度
    private List<Float> timeProcessTable;
    // 当前旋转角度
    private float sweepAngle = 360;
    // 中间所显示的时间
    private String timeLabel = "";

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        innerCircleColor = Color.GRAY;
        timeProcessTable = new ArrayList<>();
        for (int i = 0; i < processItemCount; i++)
            timeProcessTable.add(-i * 360 / processItemCount);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCountDownCircle(canvas);
        drawCountDownTime(canvas, timeLabel);
        drawCountDownTriangle(canvas, -sweepAngle);
        drawTimeProcess(canvas, -sweepAngle);
        if (initialSecond == 0)
            return;
        sweepAngle = (currentSeconds - 1) / initialSecond * 360;
    }


    private PointF getCirclePoint(float sweepAngle, float radius) {
        float endX = getWidth() / 2 + (float) Math.cos(Vector3Utils.angle2Radian(90 - sweepAngle)) * radius;
        float endY = getHeight() / 2 - (float) Math.sin(Vector3Utils.angle2Radian(90 - sweepAngle)) * radius;
        return new PointF(endX, endY);
    }

    private void drawCountDownTriangle(Canvas canvas, float sweepAngle) {
        //  triangle
        PointF a = getCirclePoint(sweepAngle, radius - gapOfInnerOuter);
        PointF b = getCirclePoint(sweepAngle - triangleBase, radius - gapOfInnerOuter - innerStrokeWidth / 2);
        PointF c = getCirclePoint(sweepAngle + triangleBase, radius - gapOfInnerOuter - innerStrokeWidth / 2);
        Paint trianglePaint = new Paint();
        trianglePaint.setAntiAlias(true);
        trianglePaint.setColor(Color.rgb(224, 238, 238));
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setStrokeWidth(10);
        Path triangle_path = new Path();
        triangle_path.moveTo(a.x, a.y);
        triangle_path.lineTo(b.x, b.y);
        triangle_path.lineTo(c.x, c.y);
        triangle_path.close();
        canvas.drawPath(triangle_path, trianglePaint);
    }

    private void drawCountDownCircle(Canvas canvas) {
        // 绘制外圆
        Paint outerCirclePaint = new Paint();
        outerCirclePaint.setAntiAlias(true);
        outerCirclePaint.setColor(Color.BLACK);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(1);
        drawCircle(canvas, radius, outerCirclePaint);
        // 绘制内圆
        Paint innerCirclePaint = new Paint();
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setColor(innerCircleColor);
        innerCirclePaint.setStyle(Paint.Style.STROKE);
        innerCirclePaint.setStrokeWidth(innerStrokeWidth);
        drawCircle(canvas, radius - gapOfInnerOuter, innerCirclePaint);
    }

    void drawCountDownTime(Canvas canvas, String time) {
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(timeLabelSize);
        float stringWidth = textPaint.measureText(time);
        float x = getWidth() / 2 - stringWidth / 2;
        canvas.drawText(time, x, getHeight() / 2, textPaint);
    }

    void drawTimeProcess(Canvas canvas, float sweepAngle) {
        // 为360时指针默认在中间，此时应无进度
        if (sweepAngle == 360) {
            return;
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(140, 140, 240));
        paint.setStrokeWidth(processItemWidth);
        paint.setStyle(Paint.Style.FILL);
        timeProcessTable.stream().filter(angle -> angle <= sweepAngle).forEach(angle -> {
            PointF a = getCirclePoint(angle, radius - gapOfInnerOuter + innerStrokeWidth / 2 + 3);
            PointF b = getCirclePoint(angle, radius);
            canvas.drawLine(a.x, a.y, b.x, b.y, paint);
        });
    }

    private void drawScanArea(Canvas canvas, float sweepAngle, float radius) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//设置抗锯齿，使圆形更加圆滑
        RectF oval = new RectF(getWidth() / 2 - radius, getHeight() / 2 - radius,
                getWidth() / 2 + radius, getHeight() / 2 + radius);
        canvas.drawArc(oval, -90, sweepAngle, true, paint);
    }

    private void drawCircle(Canvas canvas, float radius, Paint paint) {
        //获得圆的圆点坐标
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        canvas.drawCircle(x, y, radius, paint);
    }


    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getGapOfInnerOuter() {
        return gapOfInnerOuter;
    }

    public void setGapOfInnerOuter(float gapOfInnerOuter) {
        this.gapOfInnerOuter = gapOfInnerOuter;
    }

    public float getInnerStrokeWidth() {
        return innerStrokeWidth;
    }

    public void setInnerStrokeWidth(float innerStrokeWidth) {
        this.innerStrokeWidth = innerStrokeWidth;
    }

    public float getTriangleBase() {
        return triangleBase;
    }

    public void setTriangleBase(float triangleBase) {
        this.triangleBase = triangleBase;
    }

    public float getCurrentSeconds() {
        return currentSeconds;
    }

    public void setCurrentSeconds(float currentSeconds) {
        this.currentSeconds = currentSeconds;
    }

    public float getInitialSecond() {
        return initialSecond;
    }

    public void setInitialSecond(float initialSecond) {
        this.initialSecond = initialSecond;
    }

    public float getTimeLabelSize() {
        return timeLabelSize;
    }

    public void setTimeLabelSize(float timeLabelSize) {
        this.timeLabelSize = timeLabelSize;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public float getProcessItemWidth() {
        return processItemWidth;
    }

    public void setProcessItemWidth(float processItemWidth) {
        this.processItemWidth = processItemWidth;
    }

    public float getProcessItemCount() {
        return processItemCount;
    }

    public void setProcessItemCount(float processItemCount) {
        this.processItemCount = processItemCount;
        timeProcessTable = new ArrayList<>();
        for (int i = 0; i < processItemCount; i++)
            timeProcessTable.add(-i * 360 / processItemCount);
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
    }
}
