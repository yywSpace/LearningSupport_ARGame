package com.example.learningsupport_argame.Course.FloatingDragActionBtn;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.example.learningsupport_argame.Course.CourseMainActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DragFloatActionButton extends FloatingActionButton {

    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;
    private int virtualHeight;

    int[] locationSta=new int[2];
    int xSta;
    int ySta;
    int[] locationEnd=new int[2];
    static int cHeight;

    public DragFloatActionButton(Context context) {
        super(context);
        Log.d("aaaaaaaaaa",String.valueOf(cHeight));
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtils.getScreenHeight(getContext());
        statusHeight = ScreenUtils.getStatusHeight(getContext());
        virtualHeight=ScreenUtils.getVirtualBarHeigh(getContext());

    }

    private int lastX;
    private int lastY;

    private boolean isDrag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        cHeight=CourseMainActivity.cHeight;
        Log.d("该函数","被处罚");

        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        Log.d("getx,geyy",String.valueOf(rawX)+String.valueOf(rawY));
        int moveCount=0;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                Log.e("down---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(false);
                isDrag = true;

                //////////////////

                moveCount++;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些手机无法触发点击事件的问题
                int distance= (int) Math.sqrt(dx*dx+dy*dy);
                Log.e("distance---->",distance+"");
                if(distance<3){//给个容错范围，不然有部分手机还是无法点击
                    if(moveCount!=1) {
                        isDrag = false;
                        break;
                    }
                }

                float x = getX() + dx;
                float y = getY() + dy;

                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
               // y = y < statusHeight ? statusHeight : (y + getHeight() >= screenHeight ? screenHeight - getHeight() : y);
                if (y<0){
                    y=0;
                }
                if (y>screenHeight-statusHeight-getHeight()-cHeight-5){
                    y=screenHeight-statusHeight-getHeight()-cHeight-5;
                }

                setX(x);
                setY(y);
                Log.d("x,y",String.valueOf(x)+" "+String.valueOf(y));

                lastX = rawX;
                lastY = rawY;
                Log.e("move---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf + " " + isDrag+"  statusHeight="+statusHeight+ " virtualHeight"+virtualHeight+ " screenHeight"+ screenHeight+"  getHeight="+getHeight()+" y"+y);
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    Log.e("ACTION_UP---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                    if (rawX >= screenWidthHalf) {
                        animate().setInterpolator(new BounceInterpolator())
                                .setDuration(500)
                                .xBy(screenWidth - getWidth() - getX())
                                .start();
                    } else {
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                        oa.setInterpolator(new BounceInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }

                }

                Log.e("up---->",isDrag+"");
                break;
        }
        //////////////////////////

        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
        //return event.getAction() != MotionEvent.ACTION_UP && (isDrag|| super.onTouchEvent(event));
    }
}
