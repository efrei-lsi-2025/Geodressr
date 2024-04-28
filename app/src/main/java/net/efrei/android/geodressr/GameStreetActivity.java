package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewSource;

import net.efrei.android.geodressr.timer.ThreadedTimer;
import net.efrei.android.geodressr.timer.TimerUtils;


/**
 * Etape 2 du jeu : le streetview
 * Affiche la streetview, le temps passé et la distance estimée dynamique du lieu à trouver.
 *
 * Paramètres :
 * - targetCoordsLongitude : longitude du lieu à trouver
 * - targetCoordsLatitude : latitude du lieu à trouver
 */
public class GameStreetActivity extends AppCompatActivity {
    private ThreadedTimer gameTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_street);

        loadStreetViewFragment();
        // TODO : écouter activement la position de l'utilisateur et
        //   appeler onWinGame lorsque ce dernier est assez proche de la cible
        onWinGame();
    }

    private void loadStreetViewFragment() {
        // get intent targetCoords
        LatLng targetCoords = new LatLng(
                getIntent().getDoubleExtra("targetCoordsLatitude", 0),
                getIntent().getDoubleExtra("targetCoordsLongitude", 0)
        );

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions()
                .position(targetCoords, StreetViewSource.OUTDOOR)
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
        }

        launchTimer();
    }

    private void launchTimer() {
        TextView timerTextView = findViewById(R.id.timerTextView);
        this.gameTimer = new ThreadedTimer(secondsRemaining -> {
            runOnUiThread(() -> timerTextView.setText(TimerUtils.formatTime(secondsRemaining)));
        });
        this.gameTimer.start();
    }
    private void onWinGame() {
        // TODO : replacer par les coordonnées courantes
        LatLng currentCoords = new LatLng(
                getIntent().getDoubleExtra("targetCoordsLatitude", 0),
                getIntent().getDoubleExtra("targetCoordsLongitude", 0)
        );
        long timeSpent = this.gameTimer.getElapsedTime();

        // TODO : retirer timer (laissé pour debug gamePhoto)
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, GamePhotoActivity.class);
            intent.putExtra("timeSpent", timeSpent);
            intent.putExtra("positionLatitude", currentCoords.latitude);
            intent.putExtra("positionLongitude", currentCoords.longitude);
            startActivity(intent);
        }, 1000);
    }
}