package net.efrei.android.geodressr.permissions;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionUtils {
    public final static int CAMERA_REQUEST_CODE = 100;
    public final static int GRANTED = PackageManager.PERMISSION_GRANTED;

    public static void requestLocationPermission(AppCompatActivity activity, int requestCode) {
        activity.requestPermissions(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, requestCode);
    }

    public static boolean hasLocationPermission(AppCompatActivity activity) {
        return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
                && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCameraPermission(AppCompatActivity activity) {
        activity.requestPermissions(new String[]{
                Manifest.permission.CAMERA
        }, CAMERA_REQUEST_CODE);
    }

    public static boolean hasCameraPermission(AppCompatActivity activity) {
        return activity.checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCameraJustGranted(int requestCode, @NonNull int[] grantResults) {
        return requestCode == PermissionUtils.CAMERA_REQUEST_CODE
                && grantResults[0] == PermissionUtils.GRANTED;
    }

}
