package com.example.learningsupport_argame.Navi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.adapter.IWTTSPlayer;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.baidu.platform.comapi.walknavi.widget.ArCameraView;



public class YoudaoActivity extends Activity {

    WalkNavigateHelper mNaviHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        mNaviHelper = WalkNavigateHelper.getInstance();

        View view = mNaviHelper.onCreate(YoudaoActivity.this);
        if (view != null) {
            setContentView(view);
        }
        // 开始导航
        mNaviHelper.startWalkNavi(YoudaoActivity.this);
        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            /**
             * 普通步行导航模式和步行AR导航模式的切换
             * @param mode 导航模式
             * @param walkNaviModeSwitchListener 步行导航模式切换的监听器
             */
            @Override
            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener walkNaviModeSwitchListener) {
                mNaviHelper.switchWalkNaviMode(YoudaoActivity.this, mode, walkNaviModeSwitchListener);
            }

            @Override
            public void onNaviExit() {

               // Toast.makeText(YoudaoActivity.this, "已到达目的地，导航结束", Toast.LENGTH_SHORT).show();

            }
        });

//        mNaviHelper.setTTsPlayer(new IWTTSPlayer() {
//            @Override
//            public int playTTSText(final String s, boolean b) {
//
//                return 0;
//            }
//        });

        mNaviHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            //诱导图标更新
            @Override
            public void onRouteGuideIconUpdate(Drawable drawable) {

               // Toast.makeText(YoudaoActivity.this, "诱导图标更新", Toast.LENGTH_SHORT).show();

            }

            //诱导类型枚举
            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {
              //  Toast.makeText(YoudaoActivity.this, "诱导类型枚举", Toast.LENGTH_SHORT).show();

            }

            /**
             * 诱导信息
             *
             * @param charSequence  第一行显示的信息，如“沿当前道路”
             * @param charSequence1 第二行显示的信息，比如“向东出发”，第二行信息也可能为空
             */
            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

               // Toast.makeText(YoudaoActivity.this, "诱导信息", Toast.LENGTH_SHORT).show();

            }

            //总的剩余距离
            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

                //Toast.makeText(YoudaoActivity.this, "总的剩余距离", Toast.LENGTH_SHORT).show();


            }

            //总的剩余时间
            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {
               // Toast.makeText(YoudaoActivity.this, "总的剩余时间", Toast.LENGTH_SHORT).show();

            }

            //GPS状态发生变化，来自诱导引擎的消息
            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {
               // Toast.makeText(YoudaoActivity.this, "GPS状态发生变化，来自诱导引擎的消息", Toast.LENGTH_SHORT).show();
            }

            //已经开始偏航
            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {
               // Toast.makeText(YoudaoActivity.this, "已经开始偏航", Toast.LENGTH_SHORT).show();

            }

            //偏航规划中
            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {
              // Toast.makeText(YoudaoActivity.this, "偏航规划中", Toast.LENGTH_SHORT).show();

            }

            //重新算路成功
            @Override
            public void onReRouteComplete() {
               // Toast.makeText(YoudaoActivity.this, "重新算路成功", Toast.LENGTH_SHORT).show();

            }

            //抵达目的地
            @Override
            public void onArriveDest() {

               // Toast.makeText(YoudaoActivity.this,"已到达目的地，导航结束，onArriveDest",Toast.LENGTH_SHORT);

            }

            @Override
            public void onIndoorEnd(Message message) {

                //Toast.makeText(YoudaoActivity.this,"onIndoorEnd",Toast.LENGTH_SHORT);
            }

            @Override
            public void onFinalEnd(Message message) {

                //Toast.makeText(YoudaoActivity.this, "onFileEnd", Toast.LENGTH_SHORT).show();


            }

            //震动
            @Override
            public void onVibrate() {

               // Toast.makeText(YoudaoActivity.this, "震动", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ArCameraView.WALK_AR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaviHelper.startCameraAndSetMapView(YoudaoActivity.this);
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "需要开启相机使用权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
