package net.efrei.android.geodressr;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.efrei.android.geodressr.location.ReverseGeocodingClient;
import net.efrei.android.geodressr.permissions.PermissionUtils;
import net.efrei.android.geodressr.persistance.EntityManager;
import net.efrei.android.geodressr.persistance.GameEntity;
import net.efrei.android.geodressr.timer.TimerUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Etape 3 du jeu : prendre en photo le lieu
 * <p>
 * Paramètres :
 * - positionLatitude + positionLongitude : double - coordonnées GPS trouvées.
 * - timeSpent : long - nombre de secondes passées pour cette partie
 */
public class GamePhotoActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> startCamera;
    private Uri cam_uri;
    private String cityName = "Unknown";
    private double lat;
    private double lon;
    private int timeSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_photo);

        this.lat = getIntent().getDoubleExtra("positionLatitude", 0);
        this.lon = getIntent().getDoubleExtra("positionLongitude", 0);
        this.timeSpent = getIntent().getIntExtra("timeSpent", 1);

        setupText();
        setupIntents();
        setupCity(lat, lon);
    }

    private void setupText() {
        TextView timeSpent = findViewById(R.id.photoTimeSpent);
        timeSpent.setText(TimerUtils.formatTimeMinSecs(this.timeSpent));
    }

    private void setupIntents() {
        startCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::handlePhotoReceived
        );
    }

    public void onAddPhotoClick(View view) {
        if (PermissionUtils.hasCameraPermission(this)) {
            launchCamera();
        } else {
            PermissionUtils.requestCameraPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.isCameraJustGranted(requestCode, grantResults)) {
            launchCamera();
        } else {
            Toast.makeText(this, getString(R.string.missing_camera_permission), Toast.LENGTH_LONG).show();
        }
    }

    public void onSaveButtonClick(View view) {
        GameEntity game = new GameEntity()
                .setLocation(this.lat, this.lon)
                .setDuration(this.timeSpent)
                .setCityName(this.cityName)
                .setPhoto(this.cam_uri.toString());

        try (EntityManager db = new EntityManager(this)) {
            db.save(game);
        }

        this.finish();
    }

    private void handlePhotoReceived(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK) {
            Toast.makeText(this, getString(R.string.cancelled_photo), Toast.LENGTH_LONG).show();
            return;
        }
        // There are no request codes
        ImageView imgView = findViewById(R.id.photoContainer);
        imgView.setImageURI(cam_uri);
        Button saveBtn = findViewById(R.id.photoSaveBtn);
        saveBtn.setEnabled(true);
    }

    private void launchCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        cam_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
        startCamera.launch(cameraIntent);
    }

    private void setupCity(double lat, double lon) {
        String key = getString(R.string.google_maps_key);
        Handler handler = new Handler(Looper.getMainLooper());
        Executor executor = Executors.newSingleThreadExecutor();
        TextView locationText = findViewById(R.id.photoLocationValue);
        // Executes in a worker thread
        executor.execute(() -> {
            String result = new ReverseGeocodingClient(lat, lon, key).executeRequest();
            // go back to render thread
            handler.post(() -> {
                locationText.setText(result);
                cityName = result;
            });
        });
    }
}