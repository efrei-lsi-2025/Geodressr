package net.efrei.android.geodressr;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

public class PermissionUtils {


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
}
