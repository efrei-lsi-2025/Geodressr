package net.efrei.android.geodressr;

import static net.efrei.android.geodressr.location.LocationUtils.getRandomPointAround;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import net.efrei.android.geodressr.game.GameDifficultyUtils;
import net.efrei.android.geodressr.permissions.PermissionUtils;

/**
 * Etape 1 du jeu : recherche d'une zone à proximité
 * Une fois la zone trouvée, va démarrer {@link GameStreetActivity} en lui passant les coordonnées en paramètres
 * Paramètres :
 * - level : facile, moyen, difficile
 */
public class GameLaunchActivity extends AppCompatActivity {
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
        text.setText(R.string.recherche_zone);
    }

    @SuppressLint("MissingPermission")
    private void setupLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                        text.setText(R.string.erreur_impossible_localiser);
                    }
                });
    }

    private void findSurrounding(Location currentLocation) {
        Location target = getRandomPointAround(currentLocation, this.difficulty.getMinMaxRadius());
        TextView text = findViewById(R.id.gameLaunchingText);
        text.setText(R.string.c_est_parti);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, GameStreetActivity.class);
            intent.putExtra("targetCoordsLongitude", (Double) target.getLongitude());
            intent.putExtra("targetCoordsLatitude", (Double) target.getLatitude());
            startActivity(intent);
            this.finish();
        }, 1000);
    }

}