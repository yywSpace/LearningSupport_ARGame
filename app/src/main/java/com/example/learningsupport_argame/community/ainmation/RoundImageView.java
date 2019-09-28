package com.example.learningsupport_argame.community.ainmation;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.example.learningsupport_argame.R;



/**

 * 自定义图片样式，顶部圆角显示

 */




@SuppressLint("AppCompatCustomView")
public class RoundImageView extends ImageView {



    private Path mPath;

    private RectF mRectF;

    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/

    private float[] rids = new float[8];

    private PaintFlagsDrawFilter paintFlagsDrawFilter;



    public RoundImageView(Context context) {

        this(context, null);

    }



    public RoundImageView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

    }



    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        float mRadius = array.getDimension(R.styleable.RoundImageView_radius, 10);

        rids[0] = mRadius;

        rids[1] = mRadius;

        rids[2] = mRadius;

        rids[3] = mRadius;

        rids[4] = 0f;

        rids[5] = 0f;

        rids[6] = 0f;

        rids[7] = 0f;

        array.recycle();

        //用于绘制的类

        mPath = new Path();

        //抗锯齿

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //关闭硬件加速，同时其他地方依然享受硬件加速

        setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }



    @Override

    protected void onDraw(Canvas canvas) {

        //   Log.d("mylog", "onDraw: ");

        //重置path

        mPath.reset();

        //p1:大小，p2:圆角，p3:CW:顺时针绘制path，CCW:逆时针

        mPath.addRoundRect(mRectF, rids, Path.Direction.CW);

        //添加抗锯齿

        canvas.setDrawFilter(paintFlagsDrawFilter);

        canvas.save();

        //该方法不支持硬件加速，如果开启会导致效果出不来，所以之前设置关闭硬件加速

        //Clip(剪切)的时机：通常理解的clip(剪切)，是对已经存在的图形进行clip的。

        // 但是，在android上是对canvas（画布）上进行clip的，要在画图之前对canvas进行clip，

        // 如果画图之后再对canvas进行clip不会影响到已经画好的图形。一定要记住clip是针对canvas而非图形

        //开始根据path裁剪

        canvas.clipPath(mPath);

        super.onDraw(canvas);

        canvas.restore();

    }



    int a,b;

    //执行在onDraw()之前

    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        //  Log.d("mylog", "onSizeChanged: ");

        a = w;

        b = h;

        mRectF = new RectF(0, 0, w, h);

        Log.d("mylog", "onSizeChanged: "+w+"-----"+h);

    }



}

