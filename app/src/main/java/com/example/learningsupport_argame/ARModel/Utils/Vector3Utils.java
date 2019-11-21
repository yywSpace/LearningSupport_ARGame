package com.example.learningsupport_argame.ARModel.Utils;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.utilities.Preconditions;

public class Vector3Utils {

    public static Vector3 rotateAroundY(Vector3 v, float angle) {
        double radians = angle2Radian(angle);
        // x1= x⋅cosθ + y⋅sinθ
        float x = (float) (v.x * Math.cos(radians) + v.z * Math.sin(radians));
        // y1=−x⋅sinθ + y⋅cosθ
        float z = (float) (-v.x * Math.sin(radians) + v.z * Math.cos(radians));

        return new Vector3(x, v.y, z);
    }

    /**
     * 计算vector绕point Y轴旋转angle角度后得到的新点
     *
     * @param point  所围绕的中心点
     * @param vector 需要旋转的点
     * @param angle  旋转的角度
     * @return
     */
    public static Vector3 rotateAroundY(Vector3 point, Vector3 vector, float angle) {
        double radians = angle2Radian(angle);
        //要转的点:(x1，y1), 中心点:(x2,y2), 角度:θ，
        // x=(x1-x2)cosθ-(y1-y2)sinθ+x2
        // y=(y1-y2)cosθ+(x1-x2)sinθ+y2
        float x1 = vector.x;
        float z1 = vector.z;
        float x2 = point.x;
        float z2 = point.z;
        float x = (float) ((x1 - x2) * Math.cos(radians) - (z1 - z2) * Math.sin(radians) + x2);
        float z = (float) ((z1 - z2) * Math.cos(radians) + (x1 - x2) * Math.sin(radians) + z2);
        return new Vector3(x, vector.y, z);
    }


    public static double angle2Radian(double angle) {
        return angle * Math.PI / 180;
    }

    public static double radian2angle(double radian) {
        return radian * 180 / Math.PI;
    }

    public static Vector3 quaternion2Euler(Quaternion q) {
        double r = Math.atan2(2 * (q.w * q.x + q.y * q.z), 1 - 2 * (q.x * q.x + q.y * q.y));
        double p = Math.asin(2 * (q.w * q.y - q.z * q.x));
        double y = Math.atan2(2 * (q.w * q.z + q.x * q.y), 1 - 2 * (q.z * q.z + q.y * q.y));

        double angleR = radian2angle(r);
        double angleP = radian2angle(p);
        double angleY = radian2angle(y);

        return new Vector3((float) angleR, (float) angleP, (float) angleY);
    }

}
