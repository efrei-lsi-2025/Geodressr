package net.efrei.android.geodressr.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * https://stackoverflow.com/a/66448779
 */
public class CameraContract extends ActivityResultContract<String, Uri> {
    private Uri photoUri;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String input) {
        return openImageIntent(context);
    }

    @Override
    public Uri parseResult(int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) return null;
        return intent.getData() != null ? intent.getData() : photoUri;
    }

    private Intent openImageIntent(Context context) {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = createSaveFileUri(context);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("image/*");

        List<Intent> yourIntentsList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(camIntent, 0)) {
            Intent finalIntent = new Intent(camIntent);
            finalIntent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
            yourIntentsList.add(finalIntent);
        }

        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(getContentIntent, 0)) {
            Intent finalIntent = new Intent(getContentIntent);
            finalIntent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
            yourIntentsList.add(finalIntent);
        }

        Intent chooser = Intent.createChooser(getContentIntent, "Select source");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, yourIntentsList.toArray(new Intent[0]));

        return chooser;
    }

    private Uri createSaveFileUri(Context context) {
        File file = createFile(context);
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }
    private File createFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            throw new IllegalStateException("Dir not found");
        }
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}