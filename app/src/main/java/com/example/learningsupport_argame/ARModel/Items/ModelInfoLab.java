package com.example.learningsupport_argame.ARModel.Items;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.Task.Task;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelInfoLab {
    private static String TAG = "ModelInfoLab";
    public static List<ModelInfo> mModelInfoList;

    public static void insertModelInfo(ModelInfo modelInfo, Task task) {
        LatLng latLng_l = modelInfo.getModelLatLng();
        String latLng = String.format("%f,%f", latLng_l.latitude, latLng_l.longitude);
        Vector3 position_v = modelInfo.getRelativePosition();
        String position = String.format("%f,%f,%f", position_v.x, position_v.y, position_v.z);
        Quaternion rotation_q = modelInfo.getLocalRotation();
        String rotation = String.format("%f,%f,%f,%f", rotation_q.x, rotation_q.y, rotation_q.z, rotation_q.w);
        Vector3 scale_v = modelInfo.getLocalScale();
        String scale = String.format("%f,%f,%f", scale_v.x, scale_v.y, scale_v.z);
        DbUtils.update(null,
                "insert into task_ar_model values(" +
                        "null, " +
                        "(select task_id from task where task_name = ? and task_create_time = ?), ?, ?, ?, ?, ?);",
                task.getTaskName(),
                task.getTaskCreateTime(),
                latLng,
                modelInfo.getModelName(),
                position,
                rotation,
                scale);
    }

    public static List<ModelInfo> getModelInfoWith(String sql, Object... args) {
        List<ModelInfo> infoList = new ArrayList<>();
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                ModelInfo info = new ModelInfo();
                int task_id = resultSet.getInt("task_id");
                String task_accept_location = resultSet.getString("task_accept_location");
                String model_name = resultSet.getString("model_name");
                String model_relative_position = resultSet.getString("model_relative_position");
                String model_local_rotation = resultSet.getString("model_local_rotation");
                String model_local_scale = resultSet.getString("model_local_scale");
                info.setTaskId(task_id);
                info.setModelName(model_name);
                // model latLng
                String[] latLngArr = task_accept_location.split(",");
                double lat = Double.parseDouble(latLngArr[0]);
                double lng = Double.parseDouble(latLngArr[1]);
                info.setModelLatLng(new LatLng(lat, lng));
                // model position
                String[] positionArr = model_relative_position.split(",");
                float p_x = Float.parseFloat(positionArr[0]);
                float p_y = Float.parseFloat(positionArr[1]);
                float p_z = Float.parseFloat(positionArr[2]);
                info.setRelativePosition(new Vector3(p_x, p_y, p_z));
                // model rotation
                String[] rotationArr = model_local_rotation.split(",");
                float r_x = Float.parseFloat(rotationArr[0]);
                float r_y = Float.parseFloat(rotationArr[1]);
                float r_z = Float.parseFloat(rotationArr[2]);
                float r_w = Float.parseFloat(rotationArr[3]);
                info.setLocalRotation(new Quaternion(r_x, r_y, r_z, r_w));
                // model scale
                String[] scaleArr = model_local_scale.split(",");
                float s_x = Float.parseFloat(scaleArr[0]);
                float s_y = Float.parseFloat(scaleArr[1]);
                float s_z = Float.parseFloat(scaleArr[2]);
                info.setLocalScale(new Vector3(s_x, s_y, s_z));
                infoList.add(info);
            }
        }, sql, args);
        return infoList;
    }

    public static List<ModelInfo> getModelInfoList() {
        List<ModelInfo> infoList = getModelInfoWith("select * from task_ar_model");
        if (mModelInfoList != null)
            infoList = infoList.stream().map(modelInfo -> {
                ModelInfo info = mModelInfoList.stream()
                        .filter(oldInfo -> oldInfo.getTaskId() == modelInfo.getTaskId())
                        .findFirst()
                        .get();
                if (info != null)
                    modelInfo.setHasVibratorShaken(info.isHasVibratorShaken());
                return modelInfo;
            }).collect(Collectors.toList());
        Log.d(TAG, "getModelInfoList: " + infoList.size());
        mModelInfoList = infoList;
        return infoList;
    }

}

