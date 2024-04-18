package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
    private GameDifficulty difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_launch);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setDifficulty();
        setupLocation();
    }

    private void setDifficulty() {
        Intent intent = getIntent();
        this.difficulty = (GameDifficulty) intent.getSerializableExtra("gameDifficulty");
        TextView text = findViewById(R.id.gameLaunchingText);
        text.setText("Recherche d'une zone à proximité... \n Niveau : " + difficulty);
    }

    @SuppressLint("MissingPermission")
    private void setupLocation() {
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        if(!PermissionUtils.hasLocationPermission(this)) {
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
        Pair target = getRandomPointAround(currentLocation, 200);
        TextView text = findViewById(R.id.gameLaunchingText);
        text.setText(String.format("%f, %f", target.first, target.second));
    }

    /**
     * https://gis.stackexchange.com/a/68275
     * @param currentLocation current latitude / longitude
     * @param radius max radius
     * @return pair having longitude and latitude
     */
    public static Pair<Double, Double> getRandomPointAround(Location currentLocation, int radius) {
        double x0 = currentLocation.getLongitude();
        double y0 = currentLocation.getLatitude();
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        return new Pair<>(foundLongitude, foundLatitude);
    }
}