package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.Random;

/**
 * Etape 1 du jeu : recherche d'une zone à proximité
 * Une fois la zone trouvée, va démarrer {@link GameStreetActivity} en lui passant les coordonnées en paramètres
 * Paramètres :
 * - level : facile, moyen, difficile
 */
public class GameLaunchActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private GameDifficultyUtils difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_launch);

        retrieveGameDifficulty();
        setupLocation();
    }

    private void retrieveGameDifficulty() {
        Intent intent = getIntent();
        this.difficulty = GameDifficultyUtils.fromIntent(intent);
        TextView text = findViewById(R.id.gameLaunchingText);
        text.setText("Recherche d'une zone à proximité...");
    }

    @SuppressLint("MissingPermission")
    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        if (!PermissionUtils.hasLocationPermission(this)) {
            PermissionUtils.requestLocationPermission(this, 1);
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        findSurrounding(location);
                    } else {
                        TextView text = findViewById(R.id.gameLaunchingText);
                        text.setText("Erreur : impossible de localiser");
                    }
                });
    }

    private void findSurrounding(Location currentLocation) {
        Location target = getRandomPointAround(currentLocation, this.difficulty.getMinMaxRadius());
        TextView text = findViewById(R.id.gameLaunchingText);
        text.setText("C'est parti !");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, GameStreetActivity.class);
            intent.putExtra("targetCoordsLongitude", (Double) target.getLongitude());
            intent.putExtra("targetCoordsLatitude", (Double) target.getLatitude());
            startActivity(intent);
        }, 1000);
    }

    private static Location getRandomPointAround(Location currentLocation, Pair<Integer, Integer> minMaxRadius) {
        Random random = new Random();
        int min = minMaxRadius.first;
        int max = minMaxRadius.second;
        double distance = random.nextInt(max - min + 1) + min;
        double angle = random.nextInt(360);
        return calculateDestinationPoint(currentLocation, distance, angle);
    }


    private static Location calculateDestinationPoint(Location position, double distanceInMeter, double angle) {
        // https://stackoverflow.com/q/44419722
        double startLatitude = Math.toRadians(position.getLatitude());
        double startLongitude = Math.toRadians(position.getLongitude());
        double bearing = Math.toRadians(angle);
        double earthRadius = 6371e3;
        double angularDistance = distanceInMeter / earthRadius;

        double destinationLatitudeRadians = Math.asin(Math.sin(startLatitude) * Math.cos(angularDistance) +
                Math.cos(startLatitude) * Math.sin(angularDistance) * Math.cos(bearing));
        double destinationLongitudeRadians = startLongitude + Math.atan2(Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(startLatitude),
                Math.cos(angularDistance) - Math.sin(startLatitude) * Math.sin(destinationLatitudeRadians));

        Location result = new Location(position);
        result.setLatitude(Math.toDegrees(destinationLatitudeRadians));
        result.setLongitude((Math.toDegrees(destinationLongitudeRadians) + 540) % 360 - 180);
        return result;
    }
}