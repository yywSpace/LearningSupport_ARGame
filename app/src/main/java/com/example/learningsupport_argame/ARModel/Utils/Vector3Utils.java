package com.example.learningsupport_argame.ARModel.Utils;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.utilities.Preconditions;

public class Vector3Utils {

    public static Vector3 sub(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static double magnitude(Vector3 v) {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    public static  Quaternion eulerAngles(Vector3 var0) {
        Quaternion var1 = new Quaternion(Vector3.right(), var0.x);
        Quaternion var2 = new Quaternion(Vector3.up(), var0.y);
        Quaternion var3 = new Quaternion(Vector3.back(), var0.z);
        return Quaternion.multiply(Quaternion.multiply(var1, var2), var3);
    }


}
