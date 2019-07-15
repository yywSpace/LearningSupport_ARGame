package com.example.learningsupport_argame.ARModel.Utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.Queue;


public class LocationSensor implements SensorEventListener {
    private static LocationSensor sLocationSensor;
    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器
    private float mCurrentDegree;
    private Context mContext;
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private Queue<Float> degreeQueue;
    BufferedWriter out;

    private LocationSensor(Context context) {
        mContext = context;
        degreeQueue = new LinkedList<>();
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        float[] values = calculateOrientation();
        if (degreeQueue.size() == 5) {
            degreeQueue.poll();
        }
        degreeQueue.offer(values[0]);
        mCurrentDegree = (float) Math.round(Math.toDegrees(calculateDegree(degreeQueue)) * 100) / 100;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float[] calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        return values;
    }

    public void onResume() {
        mSensorManager.registerListener(this,
                accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, magnetic,
                Sensor.TYPE_MAGNETIC_FIELD);
    }

    private float calculateDegree(Queue<Float> degreeQueue) {
        return (float) degreeQueue
                .stream()
                .mapToDouble(Float::floatValue)
                .average()
                .getAsDouble();
    }

    public static LocationSensor get(Context context) {
        if (sLocationSensor == null) {
            sLocationSensor = new LocationSensor(context);
        }
        return sLocationSensor;
    }

    public void onPause() {
        mSensorManager.unregisterListener(this);
    }

    /**
     * @return 返回手机当前的方向角
     */
    public float getCurrentDegree() {
        return mCurrentDegree;
    }

}
