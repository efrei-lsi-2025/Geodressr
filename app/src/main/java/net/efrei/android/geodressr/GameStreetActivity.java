package net.efrei.android.geodressr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewSource;

import net.efrei.android.geodressr.timer.ThreadedTimer;
import net.efrei.android.geodressr.timer.TimerUtils;


/**
 * Etape 2 du jeu : le streetview
 * Affiche la streetview, le temps passé et la distance estimée dynamique du lieu à trouver.
 * Paramètres :
 * - targetCoordsLongitude : longitude du lieu à trouver (avant correction du StreetView)
 * - targetCoordsLatitude : latitude du lieu à trouver (avant correction du StreetView)
 */
public class GameStreetActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {
    private ThreadedTimer gameTimer;

    private LocationCallback fusedTrackerCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private HandlerThread locationHandlerThread;

    /**
     * Les coordonnées corrigées de la cible (par StreetView)
     */
    private LatLng targetCoords = null;
    private LatLng currentCoords = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_street);

        ProgressBar progressBar = findViewById(R.id.loading);
        runOnUiThread(() -> progressBar.setVisibility(ProgressBar.VISIBLE));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        loadStreetViewFragment();
    }

    @SuppressLint("MissingPermission")
    private void listenToUserPosition() {
        locationHandlerThread = new HandlerThread("RequestLocation");
        locationHandlerThread.start();

        fusedTrackerCallback = new LocationCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if ((locationResult.getLastLocation() != null) && (targetCoords != null)) {

                    if(currentCoords == null) {
                        ProgressBar progressBar = findViewById(R.id.loading);
                        runOnUiThread(() -> progressBar.setVisibility(ProgressBar.INVISIBLE));
                    }

                    currentCoords = new LatLng(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()
                    );

                    float[] distance = new float[1];
                    Location.distanceBetween(
                            currentCoords.latitude,
                            currentCoords.longitude,
                            targetCoords.latitude,
                            targetCoords.longitude,
                            distance
                    );

                    TextView distanceTextView = findViewById(R.id.distanceTextView);
                    runOnUiThread(() -> distanceTextView.setText(String.format("%dm", (int) distance[0])));

                    if (distance[0] < 50 + locationResult.getLastLocation().getAccuracy()) {
                        onWinGame();
                    }
                }
            }
        };

        LocationRequest req = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(100)
                .setIntervalMillis(1000)
                .setDurationMillis(Long.MAX_VALUE)
                .build();

        fusedLocationClient.requestLocationUpdates(req, fusedTrackerCallback, locationHandlerThread.getLooper());
    }

    private void loadStreetViewFragment() {
        LatLng targetIntentCoords = new LatLng(
                getIntent().getDoubleExtra("targetCoordsLatitude", 0),
                getIntent().getDoubleExtra("targetCoordsLongitude", 0)
        );

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions()
                .position(targetIntentCoords, StreetViewSource.OUTDOOR)
                .streetNamesEnabled(false)
                .userNavigationEnabled(false);

        SupportStreetViewPanoramaFragment fragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetViewPanoramaView);
        if (fragment == null) {
            fragment = SupportStreetViewPanoramaFragment.newInstance(options);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.streetViewPanoramaView, fragment)
                    .commit();

            fragment.getStreetViewPanoramaAsync(this);
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setOnStreetViewPanoramaChangeListener(location -> {
            targetCoords = new LatLng(location.position.latitude, location.position.longitude);
            this.listenToUserPosition();
            this.launchTimer();
        });
    }

    private void launchTimer() {
        TextView timerTextView = findViewById(R.id.timerTextView);
        this.gameTimer = new ThreadedTimer(secondsRemaining ->
                runOnUiThread(() ->
                        timerTextView.setText(TimerUtils.formatTime(secondsRemaining))
                )
        );
        this.gameTimer.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            // CHEAT mode pour Logan
            onWinGame();
        }
        return true;
    }

    private void onWinGame() {
        int timeSpent = this.gameTimer.getElapsedTime();

        Intent intent = new Intent(this, GamePhotoActivity.class);
        intent.putExtra("timeSpent", timeSpent);
        intent.putExtra("positionLatitude", currentCoords.latitude);
        intent.putExtra("positionLongitude", currentCoords.longitude);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(fusedTrackerCallback);
        locationHandlerThread.quit();
        gameTimer.stop();
    }
}