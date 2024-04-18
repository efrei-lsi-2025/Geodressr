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
        text.setText("Recherche d'une zone à proximité... \n Niveau : " + difficulty.toString());
    }

    @SuppressLint("MissingPermission")
    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        Pair target = getRandomPointAround(currentLocation, this.difficulty.getMinMaxRadius());
        TextView text = findViewById(R.id.gameLaunchingText);
        text.setText(String.format("C'est parti ! Niveau : %s %nCoords %f, %f", this.difficulty.toString(), target.first, target.second));
    }


    public static Pair<Double, Double> getRandomPointAround(Location currentLocation, Pair<Integer, Integer> minMaxRadius) {
        double longitude = currentLocation.getLongitude();
        double latitude = currentLocation.getLatitude();
        double minRadius = minMaxRadius.first;
        double maxRadius = minMaxRadius.second;

        // Convertir les rayons de mètres en degrés (approximation)
        double maxRadiusDegrees = metersToDegrees(maxRadius);
        double minRadiusDegrees = metersToDegrees(minRadius);

        // Générer des coordonnées aléatoires dans le rectangle défini par les rayons
        Random random = new Random();
        double randomLongitudeOffset = random.nextDouble() * (maxRadiusDegrees - minRadiusDegrees) + minRadiusDegrees;
        double randomLatitudeOffset = random.nextDouble() * (maxRadiusDegrees - minRadiusDegrees) + minRadiusDegrees;

        // Ajouter les offsets aux coordonnées d'origine
        double newLongitude = longitude + randomLongitudeOffset;
        double newLatitude = latitude + randomLatitudeOffset;

        return new Pair<>(newLongitude, newLatitude);
    }

    private static double metersToDegrees(double meters) {
        // Approximation simple pour convertir les mètres en degrés à l'équateur
        return meters / 111000.0;
    }
}