package com.example.learningsupport_argame.ARModel.Utils;

import android.app.Activity;
import android.app.Service;
import android.os.VibrationEffect;
import android.os.Vibrator;

/**
 * 手机震动工具类
 *
 * @author Administrator
 */
public class VibratorUtil {

    /**
     * @param activity     调用该方法的Activity实例
     * @param milliseconds 震动的时长，单位是毫秒
     */
    public static void Vibrate(final Activity activity, long milliseconds) {

        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, 100);
        vib.vibrate(vibrationEffect);
    }

    /**
     * @param service      调用该方法的Service实例
     * @param milliseconds 震动的时长，单位是毫秒
     */
    public static void Vibrate(final Service service, long milliseconds) {

        Vibrator vib = (Vibrator) service.getSystemService(Service.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, 100);
        vib.vibrate(vibrationEffect);
    }


}
